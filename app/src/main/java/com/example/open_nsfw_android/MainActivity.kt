package com.example.open_nsfw_android

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.zwy.nsfw.api.NsfwHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var nsfwHelper: NsfwHelper? = null
    var mainAdapter: MainAdapter? = null
    var index = 0
    val listData: ArrayList<MyNsfwBean> = ArrayList<MyNsfwBean>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //assets 目录下的timg-10.jpeg为正常静态图片  ccc.gif 为动态正常图片 可用作测试
//        val b = BitmapFactory.decodeStream(resources.assets.open("img/06 (1).jpg"))
//        iv.setImageBitmap(b)
        nsfwHelper = NsfwHelper.getInstance(this, true, 1)
        mainAdapter = MainAdapter(null)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mainAdapter
        tv_start.setOnClickListener {
            for (a in resources.assets.list("img")) {
                val path = "img/${a}"
                val b = BitmapFactory.decodeStream(resources.assets.open(path))
                listData.add(MyNsfwBean(0f, 0f, path, b))
                nsfwHelper?.scanBitmap(b) { sfw, nsfw ->
                    listData[index].sfw = sfw
                    listData[index].nsfw = nsfw
                    mainAdapter?.addData(listData[index])
                    mainAdapter?.notifyItemInserted(index)
                    index++
                }
            }

        }

    }
}
