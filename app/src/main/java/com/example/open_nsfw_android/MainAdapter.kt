package com.example.open_nsfw_android

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class MainAdapter(nsfwList: List<MyNsfwBean>?) :
    BaseQuickAdapter<MyNsfwBean, BaseViewHolder>(R.layout.main_item, nsfwList) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: MyNsfwBean) {
        val textView = helper.getView<TextView>(R.id.tv_text)
        val imageView = helper.getView<ImageView>(R.id.iv)
        val view = helper.getView<RelativeLayout>(R.id.view)
        var nsfwStr = "色情图片"
        var color = ContextCompat.getColor(mContext, R.color.nsfw1)
        when (item.nsfw) {
            in 0.0..0.3 -> {
                nsfwStr = "正常图片"
                color = ContextCompat.getColor(mContext, R.color.nsfw3)
            }
            in 0.3..0.6 -> {
                nsfwStr = "👙比基尼"
                color = ContextCompat.getColor(mContext, R.color.nsfw2)
            }
        }
        textView.text =
            "path = ${"img/${item.path}"} \n\nSFW score: ${item.sfw}\nNSFW score: ${item.nsfw} \n\n 鉴定结果: ${nsfwStr}"
        imageView.setImageBitmap(item.bitmap)
        view.setBackgroundColor(color)
    }
}