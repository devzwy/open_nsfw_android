package shop.gedian.www.base

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.KeyboardUtils
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialog.interfaces.OnBackClickListener
import com.kongzue.dialog.v3.TipDialog
import com.kongzue.dialog.v3.WaitDialog
import com.zww.sample.R
import com.zww.sample.AppLiveDataType
import org.koin.java.KoinJavaComponent.get
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    OnBackClickListener {

    lateinit var mBinding: V
    lateinit var mViewModel: VM
    val dialog: TipDialog by lazy {

        WaitDialog.build(this).also {
            it.message = "努力加载中..."
            it.cancelable = true
            it.cancelable = false
            it.onBackClickListener = this
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewDataBinding(savedInstanceState)
        registorUIChangeLiveDataCallBack()
        if (isInitImmersionBar()) initTitleBar()
        initData()

    }

    /**
     * 是否需要初始化沉浸式状态栏 默认初始化，如果不需要初始化时子类复写该函数
     */
    open fun isInitImmersionBar() = true


    private fun initTitleBar() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.colorPrimary).init()
    }

    /**
     * 点击空白区域关闭输入框
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 点击空白区域关闭输入框
     */
    private fun isShouldHideKeyboard(
        v: View?,
        event: MotionEvent
    ): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationOnScreen(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom)
        }
        return false
    }

    /**
     * 隐藏虚拟按键
     */
    fun hideNavtionBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
    }


    /**
     * 注入绑定
     */
    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
        val type: Type = javaClass.genericSuperclass!!
        mViewModel = get((type as ParameterizedType).actualTypeArguments[1] as Class<VM>)
        mBinding.lifecycleOwner = this
    }

    /**
     * 注册ViewModel与View的契约UI回调事件
     */
    protected open fun registorUIChangeLiveDataCallBack() {

        mViewModel.mUILiveData.observe(this, Observer {

            when (it.type) {
                AppLiveDataType.showDialog -> {
                    //加载进度条
                    dialog.show()
                }
                AppLiveDataType.dissmissDialog -> {
                    //取消进度条
                    if (dialog.isShow) dialog.doDismiss()
                }
                AppLiveDataType.finish -> {
                    //关闭页面
                    finish()
                }
                //跳转页面
                AppLiveDataType.toNextPage -> {
                    startActivity(it.intent)
                    if (it.isCloseThisActivity) finish()
                }
                //跳转页面
                AppLiveDataType.toNextPageResult -> {
                    startActivityForResult(it.intent, it.requestCode!!)
                    if (it.isCloseThisActivity) finish()
                }
            }

        })
    }


    /**
     *
     * 父类onCreate执行完成后调用
     */
    abstract fun initData()

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(savedInstanceState: Bundle?): Int

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.onCleared()
        mBinding.unbind()
    }

    /**
     * 页面进度条对话框被取消后的回掉
     */
    override fun onBackClick(): Boolean {
        TODO("Not yet implemented")
    }


}

