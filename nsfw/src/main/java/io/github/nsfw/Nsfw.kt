package io.github.nsfw

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import kotlin.math.max

object Nsfw {

    private const val INPUT_WIDTH = 224

    private const val INPUT_HEIGHT = 224

    private val VGG_MEAN = floatArrayOf(103.939f, 116.779f, 123.68f)

    private var mInterpreter: Interpreter?=null

    /**
     * 初始化Interpreter 默认从assets读取名称为nsfw.tflite的文件
     */
    fun init(application: Application) {
        mInterpreter = Interpreter(
            application.assets.openFd("nsfw.tflite")
                .let { fileDescriptor ->
                    FileInputStream(fileDescriptor.fileDescriptor).channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        fileDescriptor.startOffset,
                        fileDescriptor.declaredLength
                    )
                }
        )
    }

    /**
     * 释放资源
     */
    fun unInit(){
        mInterpreter?.close()
    }

    /**
     * 初始化Interpreter 传入nsfw.tflite文件
     */
    fun initByFile(file: File) {
        mInterpreter = Interpreter(file)
    }

    /**
     * 从bitmap解析nsfw数值
     */
    fun decode(bitmap: Bitmap): FloatArray {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()

        Array(1) { FloatArray(2) }.apply {
            mInterpreter?.run(
                bitmapToByteBuffer(Bitmap.createScaledBitmap(bitmap, 256, 256, true)),
                this
            )
            DecimalFormat("0.00000000").let {
                return floatArrayOf(
                    it.format(this[0][0]).toFloat(),
                    it.format(this[0][1]).toFloat()
                )
            }
        }
    }

    /**
     * bitmap转换为ByteBuffer，会按照模型的需要进行转换 RGB->BGR
     */
    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        ByteBuffer.allocateDirect(1 * INPUT_WIDTH * INPUT_HEIGHT * 3 * 4).also { imgData ->

            imgData.order(ByteOrder.LITTLE_ENDIAN)

            SystemClock.uptimeMillis().let { startTime ->
                imgData.rewind()
                IntArray(INPUT_WIDTH * INPUT_HEIGHT).let {
                    //把每个像素的颜色值转为int 存入intValues
                    bitmap.getPixels(
                        it,
                        0,
                        INPUT_WIDTH,
                        Math.max((bitmap.height - INPUT_HEIGHT) / 2, 0),
                        Math.max((bitmap.width - INPUT_WIDTH) / 2, 0),
                        INPUT_WIDTH,
                        INPUT_HEIGHT
                    )
                    for (color in it) {
                        imgData.putFloat(Color.blue(color) - VGG_MEAN[0])
                        imgData.putFloat(Color.green(color) - VGG_MEAN[1])
                        imgData.putFloat(Color.red(color) - VGG_MEAN[2])
                    }
                }
                return imgData
            }
        }
    }

}