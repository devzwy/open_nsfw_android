package com.zwy.opennsfw.core

import NsfwBean
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import d
import e
import mClassifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.ByteArrayOutputStream
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
    private var tflite: Interpreter

    /**
     * 喂入模型的最终数据源
     */
    private var imgData: ByteBuffer

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
        try {
            tflite =
                Interpreter(loadModelFile(config.context!!), getTfLiteOptions(config.isOpenGPU))
            if (config.isOpenGPU) "开启GPU加速成功".d()
        } catch (e: Exception) {
            "不支持GPU加速".e()
            tflite = Interpreter(loadModelFile(config.context!!), getTfLiteOptions(false))
        }

        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE
                    * INPUT_WIDTH
                    * INPUT_HEIGHT
                    * DIM_PIXEL_SIZE
                    * BYTES_PER_CHANNEL_NUM
        )

        imgData.order(ByteOrder.LITTLE_ENDIAN)
    }

    private fun getTfLiteOptions(isOpenGPU: Boolean = true): Interpreter.Options {
        return Interpreter.Options().apply {
            if (isOpenGPU) this.addDelegate(GpuDelegate())
            this.setNumThreads(config.numThreads)
            this.setAllowBufferHandleOutput(true)
            this.setAllowFp16PrecisionForFp32(true)
        }
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

    //    # 根据路径获取图片 Image.open(path)
//    # 判断图片的像素格式是否为RGB，如果不是RGB则转换为RGB(24位彩色图像,每个像素用24个bit表示，分别表示红色、绿色和蓝色三个通道)
//    # 重制图片大小为256*256并使用 官方解释：若要调整大小，请对所有可能影响输出值的像素使用线性插值计算输出像素值。对于其他变换，使用输入图像中2x2环境上的线性插值。
//    # 对resize的结果转换为io流并保存为JPEG格式
//    # Convert to 64-bit floating point.asType float32 定义变量存储转换后的32位float图片数据
//    # 获取图片的宽高，截取 224*224大小 x从16位开始，取到16+224位置  y亦是如此
//    # 将取的数值转换位float32
//    # 将每一个颜色值*255
//    # 将每一个颜色减去一定的阈值 104....
//    # [[[127.64.-18]]] 转换为 [[[[127.64.-18]]]]
//    # 使用index关键字喂入模型
//    # 删除所有单维度的条目
//    # 输出扫描结果
    fun run(bitmap: Bitmap): NsfwBean {
        //缩放位图时是否应使用双线性过滤。如果这是正确的，则在缩放时将使用双线性滤波，从而以较差的性能为代价具有更好的图像质量。如果这是错误的，则使用最近邻居缩放，这将使图像质量较差但速度更快。推荐的默认设置是将滤镜设置为“ true”，因为双线性滤镜的成本通常很小，并且改善的图像质量非常重要
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
        val bitmap_256 = Bitmap.createScaledBitmap(bitmap, 256, 256, true)

        convertBitmapToByteBuffer(bitmap_256)
//
        val startTime = SystemClock.uptimeMillis()
        // out
        val outArray = Array(1) { FloatArray(2) }

        tflite.run(imgData, outArray)

        val endTime = SystemClock.uptimeMillis()
        val nsfw = NsfwBean(outArray[0][0], outArray[0][1])
        "扫描完成[${nsfw}]耗时: ${(endTime - startTime)} ms".d()
        return nsfw
    }

    fun bitmap2RGB(bitmap: Bitmap): ByteArray? {
        val bytes = bitmap.byteCount //返回可用于储存此位图像素的最小字节数
        val buffer =
            ByteBuffer.allocate(bytes) //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer) // 将位图的像素复制到指定的缓冲区
        val rgba = buffer.array()
        val pixels = ByteArray(rgba.size / 4 * 3)
        val count = rgba.size / 4

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (i in 0 until count) {
            pixels[i * 3] = rgba[i * 4] //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1] //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2] //B
        }
        return pixels
    }
}