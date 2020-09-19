package com.zww.sample.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.zww.sample.MainActivity
import com.zww.sample.AppLiveData
import com.zww.sample.AppLiveDataType
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import shop.gedian.www.base.BaseViewModel


/**
 * =====================================================
 * 创建时间：2020/6/22 13:30
 * 作者：赵文文
 * 说明：
 * =====================================================
 */
class SplashViewModel : BaseViewModel() {

    /**
     * 休眠后跳转进其他页面
     */
    @SuppressLint("CheckResult")
    fun doSomeThing() {
        if (mAppRepository.isFileDownloaded()) {
            Observable.create<Long> {
                mAppRepository.initNSFW()
            }.subscribeOn(Schedulers.io()).subscribe()
        } else {
            downloadModelFile()
        }
        toMainPage()
    }

    private fun downloadModelFile() {
        ToastUtils.showLong("开始后台下载模型文件")
        mAppRepository.dowloadNSFWModelFile()
            .subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onComplete() {
                    mAppRepository.initNSFW()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(body: ResponseBody) {
                    mAppRepository.saveNSFWModel(body)
                }

                override fun onError(e: Throwable) {
                    ToastUtils.showLong("模型下载失败,$e")
                }
            })
    }

    private fun toMainPage() {
        mUILiveData.value =
            AppLiveData(
                type = AppLiveDataType.toNextPage,
                intent = Intent(mContext, MainActivity::class.java),
                isCloseThisActivity = true
            )
    }
}