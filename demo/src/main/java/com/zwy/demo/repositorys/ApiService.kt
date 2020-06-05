package com.zwy.demo.repositorys

import com.zwy.demo.dbbean.ImageBean
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * 网络请求类
 */
interface ApiService {

    @GET("/imageList.do")
    fun getImageList(): Observable<List<ImageBean>>


}