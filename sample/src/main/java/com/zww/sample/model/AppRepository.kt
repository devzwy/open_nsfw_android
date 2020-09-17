package com.zww.sample.model

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.gson.Gson
import com.zww.sample.MyNSFWBean
import com.zww.sample.ScoreBean
import com.zww.sample.d
import com.zww.sample.e
import com.zwy.opennsfw.core.Classifier
import getNsfwScore
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * 数据源 这里决定是从缓存获取还是从网络获取
 */
class AppRepository constructor(
    private val mApiService: ApiService,
    val mGson: Gson
) :
    Repository {

    val mContext by inject(Application::class.java)

    val nsfwPath by lazy {
        "${Environment.getExternalStorageDirectory().absolutePath}/nsfw.tflite"
    }

    //NSFW是否初始化完成
    var isSDKInit = false

    override fun dowloadNSFWModelFile(): Observable<ResponseBody> {
        return mApiService.dowloadNSFWModelFile()
    }

    /**
     * 保存模型文件
     */
    override fun saveNSFWModel(body: ResponseBody) {
        try {
            //文件大小
            val contentLength: Long = body.contentLength()
            //读取文件
            val inputStream: InputStream = body.byteStream()

            //创建一个文件夹
            val file = File(Environment.getExternalStorageDirectory(), "nsfw.tflite")
            val outputStream = FileOutputStream(file)
            val bytes = ByteArray(1024)
            var len = 0
            //循环读取文件的内容，把他放到新的文件目录里面
            while (inputStream.read(bytes).also({ len = it }) != -1) {
                outputStream.write(bytes, 0, len)
                val length = file.length()
                //获取下载的大小，并把它传给页面
                val progress = (length * 100 / contentLength).toInt()
            }
            "文件保存成功".d()
        } catch (e: Exception) {
            e.printStackTrace()
            "文件保存失败,$e".e()
        }
    }

    /**
     * 模型是否已下载
     */
    override fun isFileDownloaded(): Boolean {
        return File(nsfwPath).exists()
    }

    /**
     * 初始化模型
     */
    override fun initNSFW() {
        Classifier.Build().context(mContext).isOpenGPU(true).nsfwModuleFilePath(nsfwPath).build()
        "NSFW初始化成功".d()
        isSDKInit = true
    }

    /**
     * 扫描文件并返回扫描结果,传入参数为空时扫描资源文件
     */
    override fun getNSFWScoreDataList(files: List<File>?): ArrayList<ScoreBean> {
        return arrayListOf<ScoreBean>().also { returnList ->
            if (files == null) {
                mContext.resources.assets.also { asset ->
                    asset.list("img")?.forEach {
                        "img/$it".also { assetFilePath ->
                            val startTime = Date().time
                            BitmapFactory.decodeStream(asset.open(assetFilePath)).also {
                                it.getNsfwScore().apply {
                                    returnList.add(
                                        ScoreBean(
                                            assetFilePath,
                                            MyNSFWBean(this.nsfw, this.sfw),
                                            0x00,
                                            (Date().time - startTime).toInt()
                                        )
                                    )
                                }
                                it.recycle()
                            }

                        }
                    }
                }
            } else {
                files.forEach {
                    val startTime = Date().time
                    it.getNsfwScore().apply {
                        returnList.add(
                            ScoreBean(
                                it.path,
                                MyNSFWBean(this.nsfw, this.sfw),
                                0x01,
                                (Date().time - startTime).toInt()
                            )
                        )
                    }
                }
            }
        }
    }

}

