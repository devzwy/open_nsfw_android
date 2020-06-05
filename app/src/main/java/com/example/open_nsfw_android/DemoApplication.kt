package com.example.open_nsfw_android

import android.app.Application
import com.example.open_nsfw_android.view.mModules
import com.zwy.opennsfw.core.Classifier
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(mModules)
        }
        Classifier.Build()
            .context(this) //必须调用 否则会有异常抛出
//            .isOpenGPU(true)//默认不开启GPU加速 部分机型开启会奔溃，自行选择,默认false
//            .numThreads(10) //分配的线程数 根据手机配置设置，默认1
            .build()

    }

}
