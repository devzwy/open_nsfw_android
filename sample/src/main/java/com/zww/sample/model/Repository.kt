package com.zww.sample.model

import com.zww.sample.ScoreBean
import io.reactivex.Observable
import okhttp3.ResponseBody
import java.io.File


interface Repository {
    fun dowloadNSFWModelFile(): Observable<ResponseBody>

    /**
     * 保存模型文件
     */
    fun saveNSFWModel(body: ResponseBody)

    /**
     * 模型是否已下载
     */
    fun isFileDownloaded():Boolean

    /**
     * 初始化模型
     */
    fun initNSFW()

    /**
     * 扫描文件并返回扫描结果,传入参数为空时扫描资源文件
     */
    fun getNSFWScoreDataList(files:List<File>?):ArrayList<ScoreBean>
}