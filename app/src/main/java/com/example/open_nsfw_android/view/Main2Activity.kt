package com.example.open_nsfw_android.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.open_nsfw_android.R
import getNsfwScore

import kotlinx.android.synthetic.main.activity_main2.*
import java.math.RoundingMode
import java.text.DecimalFormat

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
                        val nsfwBean = file.getNsfwScore()
                        runOnUiThread {
                            iv.setImageBitmap(BitmapFactory.decodeFile(file.path))
                            val floatFormat = DecimalFormat("0.0000000")
                            floatFormat.roundingMode = RoundingMode.HALF_UP

                            tv.text =
                                "\n适宜度(非色情程度):${floatFormat.format(nsfwBean.sfw * 100)}%\n\n不适宜度(涉黄程度)：${floatFormat.format(
                                    nsfwBean.nsfw * 100
                                )}%"
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
