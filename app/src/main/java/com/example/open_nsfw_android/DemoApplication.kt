package com.example.open_nsfw_android

import android.app.Application
import com.zwy.opennsfw.core.Classifier

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Classifier.Build()
            .context(this) //必须调用 否则会有异常抛出
//            .isOpenGPU(true)//默认不开启GPU加速 部分机型开启会奔溃，自行选择,默认false
//            .numThreads(10) //分配的线程数 根据手机配置设置，默认1
            .build()
    }
}