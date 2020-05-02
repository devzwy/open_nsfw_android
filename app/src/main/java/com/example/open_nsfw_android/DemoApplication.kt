package com.example.open_nsfw_android

import android.app.Application
import com.zwy.opennsfw.core.Classifier

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Classifier.Build().context(this).build()
    }
}