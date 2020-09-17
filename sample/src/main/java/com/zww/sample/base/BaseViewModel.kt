package shop.gedian.www.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zww.sample.AppLiveData
import com.zww.sample.model.AppRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.koin.java.KoinJavaComponent.inject


open class BaseViewModel : ViewModel() {
    val mUILiveData = MutableLiveData<AppLiveData>()
    val mAppRepository by inject(AppRepository::class.java)
    val mContext by inject(Application::class.java)
    private var mCompositeDisposable: CompositeDisposable? = null

    /**
     * 将所有请求加入集合，页面推出后统一取消
     */
    fun addSubscribe(subscribe: Disposable) {
        if (mCompositeDisposable == null) mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable!!.add(subscribe)
    }

    public override fun onCleared() {
        super.onCleared()
        if (mCompositeDisposable != null) mCompositeDisposable!!.clear()
    }


}