package com.zww.sample

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import io.github.devzwy.nsfw.NSFWHelper
import io.github.devzwy.nsfw.NSFWScoreBean
import io.github.devzwy.nsfw.getNsfwScore
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : BaseActivity() {
    override fun initData() {

        if (resources.assets.list(
                "img"
            ).isNullOrEmpty()
        ) {
            Toast.makeText(this, "在assets下放置图片后再运行demo", Toast.LENGTH_SHORT).show()
            return
        }


        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.adapter =
            object : BaseQuickAdapter<MyNSFWBean, BaseViewHolder>(R.layout.item_main) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseViewHolder, item: MyNSFWBean) {

                    item.apply {
                        Glide.with(this@MainActivity).load("file:///android_asset/${path}")
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.getView(R.id.iv))

                        holder.getView<TextView>(R.id.tv).let { iv ->
                            iv.text =
                                "nsfw:${nsfwScoreBean.nsfwScore}\nsfw:${nsfwScoreBean.sfwScore}\n扫描耗时：${nsfwScoreBean.timeConsumingToScanData} ms"
                            if (nsfwScoreBean.nsfwScore > 0.7) {
                                iv.setBackgroundColor(Color.parseColor("#C1FF0000"))
                            } else if (nsfwScoreBean.nsfwScore > 0.5) {
                                iv.setBackgroundColor(Color.parseColor("#C1FF9800"))
                            } else {
                                iv.setBackgroundColor(Color.parseColor("#803700B3"))
                            }
                        }
                    }
                }
            }.also { adapter ->
                adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
                adapter.isAnimationFirstOnly = false
                resources.assets.list(
                    "img"
                )?.forEach {

                    adapter.addData(
                        MyNSFWBean(
                            "img/${it}",
                            BitmapFactory.decodeStream(resources.assets.open("img/${it}"))
                                .getNsfwScore()
                        )
                    )

                }
            }
    }


    override fun getView(): Int {
        return R.layout.activity_main
    }
}

data class MyNSFWBean(val path: String, val nsfwScoreBean: NSFWScoreBean)
