package com.zww.sample

import NsfwBean
import android.content.Intent
import android.util.Log

/**
 * 控制UI的实体
 */
data class AppLiveData(
    //UI改变类型
    var type: AppLiveDataType,
    //可空参数，页面跳转时不能为空
    var intent: Intent? = null,
    //需要回掉时传入
    var requestCode: Int? = 0x00,
    //跳转页面时是否关闭当前页面，默认不关闭
    var isCloseThisActivity: Boolean = false
)

/**
 * 控制UI的实体
 */
enum class AppLiveDataType {
    showDialog,
    dissmissDialog,
    finish,
    toNextPage,
    toNextPageResult,
}

fun String.d() = Log.d("离线鉴黄App", this)
fun String.e() = Log.e("离线鉴黄App", this)

/**
 * [path]文件路径
 * [nsfwBean]扫描结果
 * [type]文件类型 0 资源文件  1 相册文件
 * [times]扫描耗时
 */
data class ScoreBean(val path: String, val nsfwBean: MyNSFWBean, val type: Int,val times:Int)

data class MyNSFWBean(val nsfwScore: Float = 0.0f, val sfwScore: Float = 0.0f) {
    override fun toString(): String {
        return "NSFW score:${nsfwScore}\nSFW socre:${sfwScore}"
    }
}