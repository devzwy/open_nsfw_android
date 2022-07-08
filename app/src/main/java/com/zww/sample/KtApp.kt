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
            context = this)

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
//2022-07-08 11:00:05 add ~
//2022-07-08 11:40:04 add ~
//2022-07-08 12:00:04 add ~
//2022-07-08 12:40:06 add ~
//2022-07-08 13:00:04 add ~
//2022-07-08 13:40:04 add ~
//2022-07-08 14:00:04 add ~
//2022-07-08 14:40:04 add ~
//2022-07-08 15:00:05 add ~
//2022-07-08 15:40:05 add ~
//2022-07-08 16:00:04 add ~
//2022-07-08 16:40:05 add ~
//2022-07-08 17:00:05 add ~
//2022-07-08 17:40:05 add ~
//2022-07-08 18:00:04 add ~
//2022-07-08 18:40:05 add ~
//2022-07-08 19:00:04 add ~
//2022-07-08 19:40:05 add ~
//2022-07-08 20:00:05 add ~
//2022-07-08 20:40:05 add ~
//2022-07-08 21:00:04 add ~
//2022-07-08 21:40:05 add ~
//2022-07-08 22:00:05 add ~
//2022-07-08 22:40:05 add ~
//2022-07-08 23:00:06 add ~
//2022-07-08 23:40:06 add ~
//2022-07-09 00:00:06 add ~
//2022-07-09 00:40:06 add ~
//2022-07-09 01:00:05 add ~
//2022-07-09 01:40:04 add ~
