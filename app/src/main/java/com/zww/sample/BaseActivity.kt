package com.zww.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getView())
        initData()
    }


    /**
     * 初始化数据
     */
    abstract fun initData()


    /**
     * 返回视图ID
     */
    abstract fun getView(): Int
}