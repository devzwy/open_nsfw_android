package com.zww.sample.model

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File

/**
 * 网络请求类
 */
interface ApiService {

    @Streaming
//    @GET("/nsfw.tflite")
    @GET("/nsfw.tflite")
    fun dowloadNSFWModelFile(): Observable<ResponseBody>
}