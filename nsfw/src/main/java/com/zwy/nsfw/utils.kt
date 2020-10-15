package com.zwy.nsfw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

@JvmField
var isOpenLog = true

fun Any.log(content: String) {
    if (isOpenLog)
        Log.d("NSFW", content)
}

fun Any.logE(content: String) {
    if (isOpenLog)
        Log.e("NSFW", content)
}

/**
 * [nsfwScore]不适宜度
 * [sfwScore]适宜度
 * [TimeConsumingToLoadData]装载数据耗时
 * [TimeConsumingToScanData]扫描数据耗时
 */
data class NSFWScoreBean(
    val nsfwScore: Float,
    val sfwScore: Float,
    val timeConsumingToLoadData: Long,
    val timeConsumingToScanData: Long
) {
    override fun toString(): String {
        return "nsfwScore:$nsfwScore, sfwScore:$sfwScore, TimeConsumingToLoadData:$timeConsumingToLoadData ms, TimeConsumingToScanData=$timeConsumingToScanData ms)"
    }
}

fun Bitmap.getNSFWScore() = NSFWHelper.getNSFWScore(this)
fun File.getNSFWScore() = try {
    BitmapFactory.decodeFile(this.path)
} catch (e: Exception) {
    throw RuntimeException("File2Bitmap过程失败,请确认文件是否存在或是否为图片")
}.getNSFWScore()