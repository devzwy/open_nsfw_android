package io.github.devzwy.nsfw

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.*
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.DecimalFormat


object NSFWHelper {

    /*为空时表示未初始化SDK*/
    private var nsfwApplication: Context? = null

    /*扫描器*/
    private lateinit var mInterpreter: Interpreter

    /*数据宽*/
    private val INPUT_WIDTH = 224

    /*数据高*/
    private val INPUT_HEIGHT = 224

    /*日志输出控制*/
    private var isEnableLog = false


    /**
     * NSFW初始化函数 内部日志默认关闭，调试环境可使用openDebugLog()开启日志
     * [application] 建议传入application,传入activity可能会有内存泄漏
     * [modelPath] 模型文件路径，为空时将默认从Assets下读取
     * [isOpenGPU] 是否开启GPU扫描加速，部分机型兼容不友好的可关闭。默认开启
     * [numThreads] 扫描数据时内部分配的线程 默认4
     */
    fun initHelper(
        context: Context,
        modelPath: String? = null,
        isOpenGPU: Boolean = true,
        numThreads: Int = 4
    ) {

        nsfwApplication?.let {

            logD("NSFWHelper已初始化，自动跳过本次初始化！")

            return
        }


        nsfwApplication = context

        getInterpreterOptions(isOpenGPU, numThreads).let { options ->

            if (modelPath.isNullOrEmpty()) {
                logD("未传入模型路径，尝试从Assets下读取'nsfw.tflite'模型文件")
                //指定模型为空时默认寻找assets目录下名称为nsfw.tflite的模型
                try {

                    mInterpreter = Interpreter(
                        nsfwApplication!!.assets.openFd("nsfw.tflite")
                            .let { fileDescriptor ->
                                FileInputStream(fileDescriptor.fileDescriptor).channel.map(
                                    FileChannel.MapMode.READ_ONLY,
                                    fileDescriptor.startOffset,
                                    fileDescriptor.declaredLength
                                )
                            }, options
                    )
                } catch (mFileNotFoundException: FileNotFoundException) {

                    nsfwApplication = null

                    logE("未从Assets下成功读取'nsfw.tflite'模型")

                    throw NSFWException("未从Assets下成功读取'nsfw.tflite'模型")
                }

                logD("从Assets下加载模型文件成功!")

            } else {

                logD("尝试从传入的模型路径读取模型")

                //指定路径下寻找模型文件进行初始化
                try {
                    modelPath.let {
                        File(it).let { modelFile ->
                            modelFile.exists().assetBoolean({
                                mInterpreter = Interpreter(
                                    modelFile,
                                    options
                                )
                            }, {
                                throw FileNotFoundException("未找到模型文件")
                            })
                        }
                    }

                    logD("模型加载成功!")

                } catch (e: Exception) {

                    nsfwApplication = null

                    logE("模型配置错误，读取失败")
                    throw NSFWException("未能正确读取到模型文件 '${modelPath}'")
                }
            }

        }

        logD("NSFWHelper初始化成功!${if (isOpenGPU) "GPU加速已成功开启" else "GPU加速未开启"}")

    }

    /**
     * 开启日志
     */
    fun openDebugLog() {
        isEnableLog = true
    }

    private fun logD(content: String) {
        if (isEnableLog) Log.d(javaClass.name, content)
    }

    private fun logE(content: String) {
        if (isEnableLog) Log.e(javaClass.name, content)
    }


    private fun getInterpreterOptions(openGPU: Boolean, numThreads: Int): Interpreter.Options {
        return Interpreter.Options().also {
            it.setNumThreads(numThreads)
            if (openGPU) {
                it.addDelegate(GpuDelegate())
                /*CPU转到GPU可以直接读取或直接写入数据到GPU中的硬件缓冲区并绕过可避免的memory copies*/
                it.setAllowBufferHandleOutput(true)
                /*CPU转到GPU处理提升扫描速度*/
                it.setAllowFp16PrecisionForFp32(true)
            }
        }
    }


    /**
     * 同步扫描文件NSFW数值
     */
    fun getNSFWScore(file: File): NSFWScoreBean {
        return getNSFWScore(BitmapFactory.decodeFile(file.path))
    }


    /**
     * 异步扫描文件NSFW数值
     */
    fun getNSFWScore(file: File, onResult: ((NSFWScoreBean) -> Unit)) {

        GlobalScope.launch(Dispatchers.IO) {
            getNSFWScore(BitmapFactory.decodeFile(file.path)).let { result ->
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            }
        }

    }

    /**
     * 同步扫描文件NSFW数值
     */
    fun getNSFWScore(filePath: String): NSFWScoreBean {
        return getNSFWScore(BitmapFactory.decodeFile(filePath))
    }


    /**
     * 异步扫描文件NSFW数值
     */
    fun getNSFWScore(filePath: String, onResult: ((NSFWScoreBean) -> Unit)) {

        GlobalScope.launch(Dispatchers.IO) {
            getNSFWScore(BitmapFactory.decodeFile(filePath)).let { result ->
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            }
        }

    }

    /**
     * 同步扫描bitmap
     */
    fun getNSFWScore(bitmap: Bitmap): NSFWScoreBean {

        nsfwApplication?.let {
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
                    ).let { result ->
                        // out
                        Array(1) { FloatArray(2) }.apply {
                            synchronized(this@NSFWHelper) {
                                mInterpreter.run(result.imgData, this)

                                DecimalFormat("0.000").let {
                                    return NSFWScoreBean(
                                        it.format(this[0][1]).toFloat(),
                                        it.format(this[0][0]).toFloat(),
                                        result.exceTime,
                                        SystemClock.uptimeMillis() - startTime
                                    ).also {
                                        logD("扫描完成(${result}) -> $it")
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        throw NSFWException("请调用NSFWHelper.init(...)函数后再试!")

    }

    /**
     * 异步扫描文件NSFW数值
     */
    fun getNSFWScore(bitmap: Bitmap, onResult: ((NSFWScoreBean) -> Unit)) {
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

        GlobalScope.launch(Dispatchers.IO) {
            getNSFWScore(bitmap).let { result ->
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            }
        }
    }

    /**
     * 装载扫描数据
     */
    private fun convertBitmapToByteBuffer(bitmap_: Bitmap): CovertBitmapResultBean {

        ByteBuffer.allocateDirect(1 * INPUT_WIDTH * INPUT_HEIGHT * 3 * 4).let { imgData ->

            imgData.order(ByteOrder.LITTLE_ENDIAN)

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
                return CovertBitmapResultBean(imgData, SystemClock.uptimeMillis() - startTime)
            }
        }

    }

}