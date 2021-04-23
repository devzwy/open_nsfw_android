package io.github.devzwy.nsfw

import java.lang.Exception
import java.nio.ByteBuffer

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

fun Boolean.assetBoolean(onTrue: ()-> Unit,onFalse: ()-> Unit){
    if (this) onTrue() else onFalse()
}

class NSFWException(str:String):Exception(str)

data class CovertBitmapResultBean(val imgData: ByteBuffer,val exceTime:Long)
