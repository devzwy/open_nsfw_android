package com.zwy.demo.repositorys

import com.zwy.demo.dbbean.HomeTitle
import com.zwy.demo.dbbean.ImageBean
import io.reactivex.Observable

interface Repository {
    /**
     * 获取首页list标题
     */
    fun getHomeTitles(): List<HomeTitle>

    /**
     * 扫描图片
     */
    fun startScann(index: Int): Observable<List<ImageBean>>
}