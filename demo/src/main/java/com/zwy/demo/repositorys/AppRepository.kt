package com.zwy.demo.repositorys

import android.annotation.SuppressLint
import android.app.Application
import com.google.gson.Gson
import com.zwy.demo.dbbean.HomeTitle
import com.zwy.demo.dbbean.ImageBean
import com.zwy.demo.utils.DBHelper
import io.reactivex.Observable
import org.koin.java.KoinJavaComponent.inject

/**
 * 数据源 这里决定是从缓存获取还是从网络获取
 */
class AppRepository constructor(
    private val mApiService: ApiService,
    private val mDBHelper: DBHelper,
    val mGson: Gson
) :
    Repository {

    val context: Application by inject(Application::class.java)

    /**
     * 获取首页list标题
     */
    override fun getHomeTitles(): List<HomeTitle> = mDBHelper.getTitles()

    /**
     * 扫描图片
     */
    @SuppressLint("CheckResult")
    override fun startScann(index: Int): Observable<List<ImageBean>> {

        return when (index) {
            0x01 -> {
                val list = arrayListOf<ImageBean>()
                context.resources.assets.list("img")!!.forEach {
                    list.add(ImageBean("img/$it", 0.0f, 0.0f))
                }
                Observable.just(list)
            }
            0x02 -> {
                Observable.just(emptyList())
            }
            0x03 -> {
                mApiService.getImageList()
            }
            else -> {
                Observable.just(emptyList())
            }
        }
    }

}

