package com.zwy.demo.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zwy.demo.utils.AppLiveData
import com.zwy.demo.utils.TAG
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class BaseViewModel : ViewModel() {
    val appLiveData = MutableLiveData<AppLiveData>()

    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 将所有请求加入集合，页面推出后统一取消
     */
    fun addSubscribe(subscribe: Disposable) {
        if (mCompositeDisposable == null) mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable!!.add(subscribe)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "页面销毁，取消全部网络请求")
        if (mCompositeDisposable != null) mCompositeDisposable!!.clear()
    }


}