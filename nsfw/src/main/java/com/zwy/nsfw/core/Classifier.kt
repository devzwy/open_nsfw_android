package com.zwy.nsfw.core

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.lang.Math.max
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier
private constructor(assetManager: AssetManager, isGPU: Boolean?, numThreads: Int) {

    /**
     * tensor input img size
     */
    private val INPUT_WIDTH = 224
    private val INPUT_HEIGHT = 224

    /**
     * BytesPerChannel
     */
    private val BYTES_PER_CHANNEL_NUM = 4

    /**
     * Preallocated buffers for storing image data in.
     */
    private val intValues = IntArray(INPUT_WIDTH * INPUT_HEIGHT)
    /**
     * The loaded TensorFlow Lite model.
     */
    private var tfliteModel: MappedByteBuffer? = null

    /**
     * Optional GPU delegate for accleration.
     */
    private var gpuDelegate: GpuDelegate? = null

    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    private var tflite: Interpreter? = null

    /**
     * A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.
     */
    private val imgData: ByteBuffer?

    init {
        tfliteModel = loadModelFile(assetManager)
        val tfliteOptions = Interpreter.Options()
        if (isGPU == true) {
            gpuDelegate = GpuDelegate()
            tfliteOptions.addDelegate(gpuDelegate)
        }
        tfliteOptions.setNumThreads(numThreads)
        tflite = Interpreter(tfliteModel!!, tfliteOptions)

        val tensor = tflite!!.getInputTensor(tflite!!.getInputIndex("input"))
        val stringBuilder = (" \n"
                + "dataType : " +
                tensor.dataType() +
                "\n" +
                "numBytes : " +
                tensor.numBytes() +
                "\n" +
                "numDimensions : " +
                tensor.numDimensions() +
                "\n" +
                "numElements : " +
                tensor.numElements() +
                "\n" +
                "shape : " +
                tensor.shape().size)
        Log.d(TAG, stringBuilder)

        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE
                    * INPUT_WIDTH
                    * INPUT_HEIGHT
                    * DIM_PIXEL_SIZE
                    * BYTES_PER_CHANNEL_NUM
        )

        imgData!!.order(ByteOrder.LITTLE_ENDIAN)
        Log.d(TAG, "Tensorflow Lite Image Classifier Initialization Success.")
    }

    /**
     * Memory-map the model file in Assets.
     */
    private fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd("nsfw.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    /**
     * Writes Image data into a `ByteBuffer`.
     */
    private fun convertBitmapToByteBuffer(bitmap_: Bitmap) {
        if (imgData == null || bitmap_ == null) {
            return
        }
        imgData.rewind()
        val W = bitmap_.width
        val H = bitmap_.height

        val w_off = max((W - INPUT_WIDTH) / 2, 0)
        val h_off = max((H - INPUT_HEIGHT) / 2, 0)

        //把每个像素的颜色值转为int 存入intValues
        bitmap_.getPixels(intValues, 0, INPUT_WIDTH, h_off, w_off, INPUT_WIDTH, INPUT_HEIGHT)
        // Convert the image to floating point.
        val startTime = SystemClock.uptimeMillis()
        for (color in intValues) {
            val r1 = Color.red(color)
            val g1 = Color.green(color)
            val b1 = Color.blue(color)

            val rr1 = r1 - 123
            val gg1 = g1 - 117
            val bb1 = b1 - 104

            imgData.putFloat(bb1.toFloat())
            imgData.putFloat(gg1.toFloat())
            imgData.putFloat(rr1.toFloat())
        }
        val endTime = SystemClock.uptimeMillis()
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + (endTime - startTime) + "ms")
    }

    fun run(bitmap: Bitmap): NsfwBean {

        val bitmap_256 = Bitmap.createScaledBitmap(bitmap, 256, 256, true)

        //Writes image data into byteBuffer
        convertBitmapToByteBuffer(bitmap_256)

        val startTime = SystemClock.uptimeMillis()
        // out
        val outArray = Array(1) { FloatArray(2) }

        tflite!!.run(imgData, outArray)

        val endTime = SystemClock.uptimeMillis()

        Log.d(TAG, "SFW score :" + outArray[0][0] + ",NSFW score :" + outArray[0][1])
        Log.d(TAG, "Timecost to run model inference: " + (endTime - startTime) + "ms")
        return NsfwBean(outArray[0][0], outArray[0][1])
    }

    /**
     * Closes the interpreter and model to release resources.
     */
    fun close() {
        if (tflite != null) {
            tflite!!.close()
            tflite = null
            Log.d(TAG, "Tensorflow Lite Image Classifier close.")
        }
        if (gpuDelegate != null) {
            gpuDelegate!!.close()
            Log.d(TAG, "Tensorflow Lite Image gpuDelegate close.")
            gpuDelegate = null
        }
        tfliteModel = null
        Log.d(TAG, "Tensorflow Lite destroyed.")
    }

    companion object {

        val TAG = "open_nsfw_android"
        /**
         * Dimensions of inputs.
         */
        private val DIM_BATCH_SIZE = 1

        private val DIM_PIXEL_SIZE = 3

        fun create(assetManager: AssetManager, isAddGpuDelegate: Boolean?, numThreads: Int): Classifier {
            return Classifier(assetManager, isAddGpuDelegate!!, numThreads)
        }

    }

}
