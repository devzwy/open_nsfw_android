package com.zww.sample

import android.app.Application
import android.widget.Toast
import io.github.devzwy.nsfw.NSFWHelper

class KtApp : Application() {
    override fun onCreate() {
        super.onCreate()

        //开启日志输出，可选
        NSFWHelper.openDebugLog()

        //扫描前必须初始化
        NSFWHelper.initHelper(
            context = this, isOpenGPU = false)

        //初始化api原型
        /* NSFW初始化函数 内部日志默认关闭，调试环境可使用openDebugLog()开启日志*/
//        fun initHelper(
//            context: Context, //建议传入application,传入activity可能会有内存泄漏
//            modelPath: String? = null,//模型文件路径，为空时将默认从Assets下读取
//            isOpenGPU: Boolean = true, //是否开启GPU扫描加速，部分机型兼容不友好的可关闭。默认开启
//            numThreads: Int = 4 //扫描数据时内部分配的线程 默认4
//        )
    }
}
