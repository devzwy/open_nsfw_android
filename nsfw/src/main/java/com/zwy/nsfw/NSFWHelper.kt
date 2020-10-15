package com.zwy.nsfw

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.os.SystemClock
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.DecimalFormat

object NSFWHelper {

    private lateinit var mInterpreter: Interpreter

    private lateinit var imgData: ByteBuffer

    /**
     * 数据宽高
     */
    private val INPUT_WIDTH = 224

    /**
     * 数据宽高
     */
    private val INPUT_HEIGHT = 224

    /**
     * SDK是否初始化完成
     */
    private var isSDKInit = false

    /**
     * 手动初始化时必须传入模型文件的路径否则会有异常抛出
     * 自动初始化需要将模型存放在资源文件assets根目录下,且名称必须为：'nsfw.tflite'，不可改动
     */
    fun init(context: Context? = null, modelPath: String? = null) {
        if (context == null && modelPath==null) throw RuntimeException("初始化失败，您必须选择一种初始化方式，初始化方式一：NSFWHelper.init(context = this@Application)，初始化方式二：[implementation 'com.zwy.nsfw:nsfw_initializer:+']，初始化方式三：NSFWHelper.init(modelPath = \"模型文件存放路径\")。说明：方式一和方式二均需要手动将模型文件放在Assets根目录下，并命名为nsfw.tflite,方式三适用于产品对apk大小控制严格，无法将模型文件直接放在apk中，可在用户打开Apk后台静默下载后指定模型路径进行初始化，三种方式任选其一即可")
        if (modelPath != null && modelPath.isNotEmpty()) {
            log("手动初始化...")
            File(modelPath).let { modelFile ->
                if (!modelFile.exists()) throw FileNotFoundException("模型文件路径配置错误，请重新配置，如果确定路径正确请检测文件权限是否申请")
                mInterpreter = try {
                    initInterpreterByFile(modelFile, true)
                } catch (e: Exception) {
                    initInterpreterByFile(modelFile)
                }
            }
        } else {
            loadModelFile(context!!).let {
                if (it == null) throw Resources.NotFoundException("资源文件下未找到模型文件，请检测模型是否在assets目录下，名称是否为nsfw.tflite")
                mInterpreter = try {
                    initInterpreterByMappedByteBuffer(it, true).also {
                        log("NSFW自动初始化成功,已开启GPU加速")
                    }
                } catch (e: Exception) {
                    initInterpreterByMappedByteBuffer(it).also {
                        log("NSFW自动初始化成功,未开启GPU加速")
                    }
                }
            }
        }
        imgData = ByteBuffer.allocateDirect(1 * INPUT_WIDTH * INPUT_HEIGHT * 3 * 4)
        imgData.order(ByteOrder.LITTLE_ENDIAN)
        isSDKInit = true
    }

    //
    //初始化方式一：NSFWHelper.init(context = this@Application)，初始化方式二：[implementation 'com.zwy.nsfw:nsfw_initializer:+']，初始化方式三：NSFWHelper.init(modelPath = "模型文件存放路径")。说明：方式一和方式二均需要手动将模型文件放在Assets根目录下，并命名为nsfw.tflite,方式三适用于产品对apk大小控制严格，无法将模型文件直接放在apk中，可在用户打开Apk后台静默下载后指定模型路径进行初始化，三种方式任选其一即可
    //
    //
    /**
     * 关闭日志输出
     */
    fun disEnableLog() {
        isOpenLog = false
    }

    /**
     * 装载扫描数据
     */
    private fun convertBitmapToByteBuffer(bitmap_: Bitmap): Long {
        SystemClock.uptimeMillis().let { startTime ->
            imgData.rewind()
            IntArray(INPUT_WIDTH * INPUT_HEIGHT).let {
                //把每个像素的颜色值转为int 存入intValues
                bitmap_.getPixels(
                    it,
                    0,
                    INPUT_WIDTH,
                    Math.max((bitmap_.height - INPUT_HEIGHT) / 2, 0),
                    Math.max((bitmap_.width - INPUT_WIDTH) / 2, 0),
                    INPUT_WIDTH,
                    INPUT_HEIGHT
                )
                for (color in it) {
                    imgData.putFloat((Color.blue(color) - 104).toFloat())
                    imgData.putFloat((Color.green(color) - 117).toFloat())
                    imgData.putFloat((Color.red(color) - 123).toFloat())
                }
            }
            return SystemClock.uptimeMillis() - startTime
        }
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
    fun getNSFWScore(bitmap: Bitmap): NSFWScoreBean {
        if (!isSDKInit) throw RuntimeException(
            "SDK未初始化，请初始化后使用。方式一：如果您的模型文件存放在Assets下并名称必须为：nsfw.tflite，请直接引用[implementation 'com.zwy.nsfw:nsfw_initializer:1.3.7']可免去初始化过程（模型文件在demo中有存放）" +
                    "方式二：否则需要指定模型文件的路径，使用：'NSFWHelper.init(modelPath = \"模型文件的路径\")'进行初始化，两者任选其一即可（后者适用于如果模型置于Apk中导致Apk体积超出产品预算时，可将模型文件后台下载至用户手机中后指定路径进行手动初始化）"
        )
        SystemClock.uptimeMillis().let { startTime ->
            //缩放位图时是否应使用双线性过滤。如果这是正确的，则在缩放时将使用双线性滤波，从而以较差的性能为代价具有更好的图像质量。如果这是错误的，则使用最近邻居缩放，这将使图像质量较差但速度更快。推荐的默认设置是将滤镜设置为“ true”，因为双线性滤镜的成本通常很小，并且改善的图像质量非常重要
            ByteArrayOutputStream().let { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.close()
                convertBitmapToByteBuffer(
                    Bitmap.createScaledBitmap(
                        bitmap,
                        256,
                        256,
                        true
                    )
                ).let { timeConsumingToLoadData ->
                    // out
                    Array(1) { FloatArray(2) }.apply {
                        mInterpreter.run(imgData, this)

                        DecimalFormat("0.000").let {
                            return NSFWScoreBean(
                                it.format(this[0][1]).toFloat(),
                                it.format(this[0][0]).toFloat(),
                                timeConsumingToLoadData,
                                SystemClock.uptimeMillis() - startTime
                            ).also {
                                log("扫描完成 -> $it")
                            }
                        }
                    }
                }

            }
        }
    }

    @Throws(Exception::class)
    private fun initInterpreterByFile(modelFile: File, isOpenGPU: Boolean = false): Interpreter {
        return Interpreter(
            modelFile,
            Interpreter.Options().also {
                it.setNumThreads(4)
                if (isOpenGPU) it.addDelegate(GpuDelegate())
                it.setAllowBufferHandleOutput(true)
                it.setAllowFp16PrecisionForFp32(true)
            })
    }

    @Throws(Exception::class)
    private fun initInterpreterByMappedByteBuffer(
        modelMappedByteBuffer: MappedByteBuffer,
        isOpenGPU: Boolean = false
    ): Interpreter {
        return Interpreter(
            modelMappedByteBuffer,
            Interpreter.Options().also {
                it.setNumThreads(4)
                if (isOpenGPU) it.addDelegate(GpuDelegate())
                it.setAllowBufferHandleOutput(true)
                it.setAllowFp16PrecisionForFp32(true)
            })
    }

    private fun loadModelFile(context: Context): MappedByteBuffer? {
        try {
            context.assets.openFd("nsfw.tflite").let { fileDescriptor ->
                return FileInputStream(fileDescriptor.fileDescriptor).channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.startOffset,
                    fileDescriptor.declaredLength
                )
            }
        } catch (e: Exception) {
            return null
        }
    }

}