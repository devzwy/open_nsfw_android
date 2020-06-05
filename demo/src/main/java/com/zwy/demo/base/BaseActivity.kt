package com.zwy.demo.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.zwy.demo.R
import org.jetbrains.anko.indeterminateProgressDialog
import org.koin.java.KoinJavaComponent.get
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    DialogInterface.OnCancelListener {

    lateinit var binding: V
    lateinit var viewModel: VM
    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.colorPrimary).init()
        initViewDataBinding(savedInstanceState)
        registorUIChangeLiveDataCallBack()
        initData()
    }


    /**
     * 注入绑定
     */
    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
        val type: Type = javaClass.genericSuperclass!!
        viewModel = get((type as ParameterizedType).actualTypeArguments[1] as Class<VM>)
        binding.lifecycleOwner = this
    }

    /**
     * 注册ViewModel与View的契约UI回调事件
     */
    protected open fun registorUIChangeLiveDataCallBack() {

        viewModel.appLiveData.observe(this, Observer {

            when (it.type) {
                0 -> {
                    //加载进度条
                    if (it.text.isNotBlank()) initDialog(it.text)
                    dialog?.show()
                }
                1 -> {
                    //取消进度条
                    if (dialog != null && dialog?.isShowing == true) {
                        dialog?.dismiss()
                    }
                }
                2 -> {
                    //关闭页面
                    finish()
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


    /**
     * 初始化进度条
     */
    private fun initDialog(msg: String) {
        dialog = indeterminateProgressDialog(msg, "提示")
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnCancelListener(this)
    }

    /**
     * 页面进度条对话框被取消后的回掉
     */
    override fun onCancel(dialog: DialogInterface?) {
        Log.d(com.zwy.demo.utils.TAG, "进度条销毁回掉")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }


}

