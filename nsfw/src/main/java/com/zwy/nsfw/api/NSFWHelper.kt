package com.zwy.nsfw.api

import android.graphics.Bitmap
import com.zwy.nsfw.core.Classifier
import com.zwy.nsfw.core.NSFWConfig
import com.zwy.nsfw.core.NsfwBean

object NSFWHelper {

    private lateinit var classifier: Classifier

    private var isInit = false

    @Synchronized
    fun init(nsfwConfig: NSFWConfig): NSFWHelper {
        classifier = Classifier.create(nsfwConfig.assetMannager, nsfwConfig.userGPU, 10)
        isInit = true
        return this
    }


    /**
     * 同步扫描图片(建议使用者自己放在线程中处理)
     */
    @Synchronized
    fun scanBitmap(bitmap: Bitmap): NsfwBean {
        if (!isInit) {
            throw ExceptionInInitializerError("Initialize the scanned object 'NSFWHelper.init(nsfwConfig: NSFWConfig)' by calling the function first.")
        }
        return classifier.run(bitmap)
    }


    /**
     * 不会销毁该单利和配置的参数，建议页面处理完成后调用该代码对扫描器进行关闭,关闭后下次扫描不需要再次调用init方法
     */
    fun destroyFactory() {
        classifier.close()
    }


}