package com.example.open_nsfw_android.view

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val BaseUrl = "http://114.116.213.202:7777/"
val GetImageListURL = "${BaseUrl}imageList.do"
val UploadImageURL = "${BaseUrl}imageList.do"


val mModules = module {
    single<OkHttpClient> {
        OkHttpClient.Builder().build()
    }
    single<Gson> {
        Gson()
    }
}


data class NSFWImage(
    val createTime: String,
    val id: Int,
    val imgUrl: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
