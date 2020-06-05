package com.zwy.demo.views

import android.os.Bundle
import com.zwy.demo.R
import com.zwy.demo.base.BaseActivity
import com.zwy.demo.databinding.MainLayoutBinding
import com.zwy.demo.models.MainViewModel
import org.jetbrains.anko.toast

class MainActivity : BaseActivity<MainLayoutBinding, MainViewModel>() {
    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_main


    override fun initData() {
        binding.titles = viewModel.titles
        viewModel.getTitles()
    }

    private var mExitTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            super.onBackPressed();
        } else {
            mExitTime = System.currentTimeMillis();
            toast("再按一次返回键退出应用")
        }
    }


}
