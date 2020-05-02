package com.zwy.opennsfw.core

import NsfwBean
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import d
import mClassifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.lang.Math.max
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier private constructor(config: Config) {

    /**
     * DIM_BATCH_SIZE
     */
    private val DIM_BATCH_SIZE = 1

    /**
     * DIM_PIXEL_SIZE
     */
    private val DIM_PIXEL_SIZE = 3

    /**
     * 数据宽高
     */
    private val INPUT_WIDTH = 224

    /**
     * 数据宽高
     */
    private val INPUT_HEIGHT = 224

    /**
     * 通道
     */
    private val BYTES_PER_CHANNEL_NUM = 4

    /**
     * Resize后的数据源
     */
    private val intValues = IntArray(INPUT_WIDTH * INPUT_HEIGHT)


    /**
     * Tensorflow Lite
     */
    private lateinit var tflite: Interpreter

    /**
     * 喂入模型的最终数据源
     */
    private lateinit var imgData: ByteBuffer

    companion object {
        private var config = Config(context = null)
        private var instance: Classifier? = null
            get() {
                if (field == null) {
                    if (config.context == null) throw RuntimeException("context函数未调用,请使用Classifier.Build().context(context)初始化")
                    field = Classifier(config)
                    mClassifier = field
                }
                return field
            }

        @Synchronized
        private fun get(config: Config): Classifier {
            this.config = config
            return instance!!
        }


    }

    class Build {
        private val config = Config(context = null)

        fun isOpenGPU(isOpenGPU: Boolean): Build {
            config.isOpenGPU = isOpenGPU
            return this
        }

        fun numThreads(numThreads: Int): Build {
            config.numThreads = numThreads
            return this
        }

        fun context(context: Context): Build {
            config.context = context
            return this
        }

        fun build(): Classifier {
            return get(config)
        }
    }

    init {

        val tfliteOptions = Interpreter.Options()
        if (config.isOpenGPU) tfliteOptions.addDelegate(GpuDelegate())
        tfliteOptions.setNumThreads(config.numThreads)
        tflite = Interpreter(loadModelFile(config.context!!), tfliteOptions)

        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE
                    * INPUT_WIDTH
                    * INPUT_HEIGHT
                    * DIM_PIXEL_SIZE
                    * BYTES_PER_CHANNEL_NUM
        )

        imgData.order(ByteOrder.LITTLE_ENDIAN)
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("nsfw.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap_: Bitmap) {
        imgData.rewind()
        val W = bitmap_.width
        val H = bitmap_.height

        val w_off = max((W - INPUT_WIDTH) / 2, 0)
        val h_off = max((H - INPUT_HEIGHT) / 2, 0)

        //把每个像素的颜色值转为int 存入intValues
        bitmap_.getPixels(intValues, 0, INPUT_WIDTH, h_off, w_off, INPUT_WIDTH, INPUT_HEIGHT)

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
        "数据装载成功，耗时:${(endTime - startTime)} ms".d()
    }

    fun run(bitmap: Bitmap): NsfwBean {

        val bitmap_256 = Bitmap.createScaledBitmap(bitmap, 256, 256, true)

        convertBitmapToByteBuffer(bitmap_256)

        val startTime = SystemClock.uptimeMillis()
        // out
        val outArray = Array(1) { FloatArray(2) }

        tflite.run(imgData, outArray)

        val endTime = SystemClock.uptimeMillis()

        "扫描完成，耗时: ${(endTime - startTime)} ms".d()
        return NsfwBean(outArray[0][0], outArray[0][1])
    }
}