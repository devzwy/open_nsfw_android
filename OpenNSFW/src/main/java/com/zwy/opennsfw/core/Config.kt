package com.zwy.opennsfw.core

import android.content.Context

data class Config(
    /**
     * 是否开启GPU加速
     */
    var isOpenGPU: Boolean = true,
    var numThreads: Int = 1,
    var context: Context?
)