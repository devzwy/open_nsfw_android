package com.zww.sample.ui

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission.Group.*
import com.yanzhenjie.permission.runtime.Permission.READ_PHONE_STATE
import com.zww.sample.R
import com.zww.sample.databinding.SplashLayoutBinding
import com.zww.sample.ui.viewmodels.SplashViewModel
import shop.gedian.www.base.BaseActivity


/**
 * =====================================================
 * 创建时间：2020/6/22 13:28
 * 作者：赵文文
 * 说明：App闪屏页面
 * =====================================================
 */
class SplashActivity : BaseActivity<SplashLayoutBinding, SplashViewModel>() {

    /**
     *
     * 父类onCreate执行完成后调用
     */
    override fun initData() {
        getPermission()
    }

    private fun getPermission() {
        val isHavePermission = AndPermission.hasPermissions(
            this,
            STORAGE,
            CAMERA
        )
        if (isHavePermission) {
            mViewModel.doSomeThing()
            return
        }

        //需要获取权限
        AndPermission.with(this)
            .runtime()
            .permission(STORAGE, CAMERA)
            .onGranted {
                //同意
                mViewModel.doSomeThing()
            }
            .onDenied {
                //拒绝
                ToastUtils.showShort("为了您更好的使用软件，请开启权限后再试")
            }
            .start()
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_splash
}