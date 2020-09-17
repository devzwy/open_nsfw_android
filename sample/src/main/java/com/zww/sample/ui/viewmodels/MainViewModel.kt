package com.zww.sample.ui.viewmodels

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zww.sample.R
import com.zww.sample.ScoreBean
import shop.gedian.www.base.BaseViewModel
import java.io.File
import java.text.DecimalFormat

class MainViewModel : BaseViewModel() {
    val bName = MutableLiveData<String>("扫描资源文件")
    val selectPhoto = MutableLiveData<Int>(0)
    val adapter = object : BaseQuickAdapter<ScoreBean, BaseViewHolder>(R.layout.item_main) {
        override fun convert(holder: BaseViewHolder, item: ScoreBean) {
            holder.getView<ImageView>(R.id.iv).also { iv ->
                if (item.type == 0x00) {
                    BitmapFactory.decodeStream(context.resources.assets.open(item.path)).apply {
                        Glide.with(context).load(this).into(iv)
                    }
                } else {
                    Glide.with(context).load(item.path).into(iv)
                }
            }

            holder.setText(
                R.id.tvContent,
                "涉黄程度：${(DecimalFormat("##0.00").format(item.nsfwBean.nsfwScore)).toFloat() * 100}%"
            )
        }
    }.also {
        it.setOnItemClickListener { adapter, view, position ->
            ToastUtils.showLong(
                "${it.getItem(position).nsfwBean.toString()}\n扫描耗时：${it.getItem(
                    position
                ).times} ms"
            )
        }
    }

    fun changeFrom() {
        if (!mAppRepository.isSDKInit) {
            ToastUtils.showShort("SDK未初始化完成，如果为第一次打开请耐心等待下载完成")
            return
        }
        when (bName.value) {
            "扫描资源文件" -> {
                adapter.setNewInstance(mAppRepository.getNSFWScoreDataList(null))
            }
            "扫描相册" -> {
                selectPhoto.value = 1
            }
        }
        changeText()
    }

    fun toScorePhoto(files: List<File>) {
        adapter.setNewInstance(mAppRepository.getNSFWScoreDataList(files))
    }

    private fun changeText() {
        bName.value = if (bName.value.equals("扫描相册")) "扫描资源文件" else "扫描相册"
    }
}