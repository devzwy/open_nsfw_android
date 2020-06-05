package com.example.open_nsfw_android.util

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.open_nsfw_android.R
import getNsfwScore
import kotlinx.android.synthetic.main.activity_main2.*
import java.math.RoundingMode
import java.text.DecimalFormat

class MainAdapter(nsfwList: List<MyNsfwBean>?) :
    BaseQuickAdapter<MyNsfwBean, BaseViewHolder>(R.layout.main_item, nsfwList) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item2: MyNsfwBean) {

        val textView = helper.getView<TextView>(R.id.tv_text)
        val imageView = helper.getView<ImageView>(R.id.iv)
        val view = helper.getView<RelativeLayout>(R.id.view)

        var color = ContextCompat.getColor(mContext, R.color.nsfw1)
        when (item2.nsfw) {
            in 0.0..0.2 -> {
                color = ContextCompat.getColor(mContext, R.color.nsfw3)
            }
            in 0.2..0.8 -> {
                color = ContextCompat.getColor(mContext, R.color.nsfw2)
            }
        }

        val floatFormat = DecimalFormat("0.0000000")
        floatFormat.roundingMode = RoundingMode.HALF_UP

        textView.text =
            "\n适宜度(非色情程度):${floatFormat.format(item2.sfw * 100)}%\n\n不适宜度(涉黄程度)：${floatFormat.format(
                item2.nsfw * 100
            )}%"
        if (item2.imgUrl != null && item2.imgUrl.isNotBlank()) {
            Glide.with(imageView).load(item2.imgUrl).into(imageView)
        } else {
            imageView.setImageBitmap(item2.bitmap)
        }
        view.setBackgroundColor(color)
    }
}