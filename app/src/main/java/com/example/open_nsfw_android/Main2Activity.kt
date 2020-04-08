package com.example.open_nsfw_android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.zwy.nsfw.kotlin.getNsfwScore

import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        bt.setOnClickListener {
            if (et.text.toString().isNotBlank()) {
                Thread(Runnable {
                    try {
                        val file = Glide.with(this)
                            .load(et.text.toString())
                            .downloadOnly(120, 120).get()
                        val nsfwBean = file.getNsfwScore(assets)
                        runOnUiThread {
                            iv.setImageBitmap(BitmapFactory.decodeFile(file.path))
                            tv.text = "nsfw:${nsfwBean.nsfw} \n sfw:${nsfwBean.sfw} "
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this, "图片载入失败，请重试", 0).show()
                        }
                    }

                }).start()
            }
        }

    }
}
