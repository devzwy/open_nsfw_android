package com.zwy.opennsfw.core

import android.content.Context

data class Config(
    /**
     * 是否开启GPU加速
     */
    var isOpenGPU: Boolean = true,
    /**
     * 扫描占用的线程数
     */
    var numThreads: Int = 1,
    /**
     * 全局配置的context
     */
    var context: Context?,

    /**
     * nsfw模型存放目录
     */
    var nsfwModuleFilePath: String? = null
)