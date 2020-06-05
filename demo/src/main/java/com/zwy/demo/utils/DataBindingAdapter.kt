package com.zwy.demo.utils

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.ScaleInAnimation
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zwy.demo.R
import com.zwy.demo.dbbean.HomeTitle
import com.zwy.demo.dbbean.ImageBean
import com.zwy.demo.views.ScanPicActivity
import org.jetbrains.anko.startActivity


/**
 * 首页列表适配器填充
 */
@BindingAdapter("bindHomeData")
fun RecyclerView.bindData(data: List<HomeTitle>?) {

    val mContext = this.context

    val homeAdapter =
        object :
            BaseQuickAdapter<HomeTitle, BaseViewHolder>(R.layout.tem_home, data?.toMutableList()) {
            override fun convert(holder: BaseViewHolder, item: HomeTitle) {
                holder.setText(R.id.tv_title, item.title)
            }
        }
    homeAdapter.animationEnable = true
    homeAdapter.isAnimationFirstOnly = false
    homeAdapter.adapterAnimation = ScaleInAnimation()

    homeAdapter.addHeaderView(LayoutInflater.from(mContext)
        .inflate(R.layout.item_home_head, LinearLayout(mContext)).apply {
            findViewById<TextView>(R.id.tv_info).text =
                mContext.getString(R.string.versionstr, PackageUtils.getVersionName(mContext))
        })

    homeAdapter.addFooterView(LayoutInflater.from(mContext)
        .inflate(R.layout.item_home_head, LinearLayout(mContext)).apply {
            this.setPadding(20, 0, 20, 0)
            findViewById<TextView>(R.id.tv_info).apply {
                setTextColor(mContext.resources.getColor(R.color.red))
                text = context.getString(R.string.nsfw_info)
            }
        })

    this.layoutManager = LinearLayoutManager(mContext)
    this.adapter = homeAdapter

    homeAdapter.setOnItemClickListener { adapter, view, position ->
        when (position) {
            0 -> {
                //识别Assets目录
                mContext.startActivity<ScanPicActivity>(ParmKey2ScanPicAty to 0x01)
            }
            1 -> {
                //识别相册
                mContext.startActivity<ScanPicActivity>(ParmKey2ScanPicAty to 0x02)
            }
            2 -> {
                //实时扫描
            }
            3 -> {
                //Api获取图片测试
                mContext.startActivity<ScanPicActivity>(ParmKey2ScanPicAty to 0x03)
            }
            4 -> {
                //上传文件
            }
            5 -> {
                //跳转GitHub首页
                mContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/devzwy/open_nsfw_android")
                    )
                )
            }
            6 -> {
                //跳转GitHub首页
                mContext.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/devzwy/open_nsfw_android")
                    )
                )
            }

        }
    }
}


/**
 * 首页列表适配器填充
 */
@BindingAdapter("bindScanPicListData")
fun RecyclerView.bindData2(data: List<ImageBean>?) {

    val mContext = this.context

    val pathBase = "file:///android_asset/"
    val scannAdapter =
        object :
            BaseQuickAdapter<ImageBean, BaseViewHolder>(
                R.layout.item_scann,
                data?.toMutableList()
            ) {
            override fun convert(holder: BaseViewHolder, item: ImageBean) {
                holder.getView<TextView>(R.id.tv_sss).apply {
                    text = "NSFW 分值:${item.nsfw} \nSFW 分值:${item.sfw}"
//                    textColorResource = if (item.nsfw > 0.5) R.color.white else R.color.colorPrimary
                }
                holder.getView<CardView>(R.id.cardview)
                    .setBackgroundResource(if (item.nsfw > 0.5) R.color.nsfw else R.color.colorPrimary)
                val imageView = holder.getView<ImageView>(R.id.iv_item)
                if (item.imgUrl.contains("http")) {
                    Glide.with(imageView).load(item.imgUrl).into(imageView)
                } else {
                    Glide.with(imageView).load("${pathBase}${item.imgUrl}").into(imageView)
                }
            }
        }
    scannAdapter.animationEnable = true
    scannAdapter.isAnimationFirstOnly = false
    scannAdapter.adapterAnimation = ScaleInAnimation()


    this.layoutManager = LinearLayoutManager(mContext)
    this.adapter = scannAdapter

}

