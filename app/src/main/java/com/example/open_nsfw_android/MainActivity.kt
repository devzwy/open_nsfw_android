package com.example.open_nsfw_android

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zwy.nsfw.api.NsfwHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var nsfwHelper: NsfwHelper? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //assets 目录下的timg-10.jpeg为正常静态图片  ccc.gif 为动态正常图片 可用作测试
        val b = BitmapFactory.decodeStream(resources.assets.open("aaa.png"))
        iv.setImageBitmap(b)
        nsfwHelper = NsfwHelper.getInstance(this, true, 1)


        bt_.setOnClickListener {
            //同步识别
            val nsfwBean = nsfwHelper?.scanBitmapSyn(b)
            Log.d("demo", nsfwBean.toString())
            tvv.text = "识别成功：\n\tSFW score : ${nsfwBean?.sfw}\n\tNSFW score : ${nsfwBean?.nsfw}"
            if (nsfwBean?.nsfw ?: 0f > 0.7) {
                tvv.text = "${tvv.text} \n \t - 色情图片"
            } else {
                tvv.text = "${tvv.text} \n \t - 正常图片"
            }
//            //异步识别，接口回调识别结果
//            nsfwHelper?.scanBitmap(b) { sfw, nsfw ->
//                Log.d("demo", "sfw:$sfw,nsfw:$nsfw")
//            }
        }
    }

}
