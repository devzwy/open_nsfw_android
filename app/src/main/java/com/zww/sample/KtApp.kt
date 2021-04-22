package com.zww.sample

import android.app.Application
import android.widget.Toast
import io.github.devzwy.nsfw.NSFWHelper

class KtApp:Application() {
    override fun onCreate() {
        super.onCreate()
        NSFWHelper.openDebugLog()
        NSFWHelper.initHelper(
            context = this,
            modelPath = "${this.filesDir.path}/nsfw.tflite",
            isOpenGPU = true,
            onInitError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onInitSuccess = {
                Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show()
            })
    }
}