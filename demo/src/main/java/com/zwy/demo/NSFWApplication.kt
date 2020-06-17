package com.zwy.demo

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.zwy.demo.utils.ActivityLifecycleCallback
import com.zwy.demo.utils.networkModule
import com.zwy.demo.utils.repositoryModule
import com.zwy.demo.utils.viewModelModules
import com.zwy.opennsfw.core.Classifier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.*


class NSFWApplication : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()

        startTime = Date().time

        context = this

        //全局配置actionbar title
        registerActivityLifecycleCallbacks(ActivityLifecycleCallback())

        //初始化鉴黄库NSFW
        Classifier.Build()
            .context(this) //1.3.4版本可不用调用该代码。其他版本必须调用，否则会有异常抛出
//            .isOpenGPU(true)//默认不开启GPU加速，默认为true
//            .numThreads(100) //分配的线程数 根据手机配置设置，默认1
//            .nsfwModuleFilePath("/data/user/0/com.zwy.demo/files/nsfw.tflite") //1.3.4版本必须配置模型存放路径，否则会有异常抛出
            .build()
        //全局注入对象
        startKoin {
            androidContext(this@NSFWApplication)
            androidLogger()
            modules(networkModule)
            modules(repositoryModule)
            modules(viewModelModules)
        }


    }

    companion object {
        lateinit var context: Context
        var startTime: Long = 0
    }


}