package com.zww.sample

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zwy.nsfw.NSFWScoreBean
import com.zwy.nsfw.getNSFWScore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.adapter =
            object : BaseQuickAdapter<MyNSFWBean, BaseViewHolder>(R.layout.item_main) {
                @SuppressLint("SetTextI18n")
                override fun convert(holder: BaseViewHolder, item: MyNSFWBean) {
                    Glide.with(this@MainActivity).load(item.bitmap).into(holder.getView(R.id.iv))
                    holder.getView<TextView>(R.id.tv).apply {
                        item.nsfwScoreBean.let {
                            this.text =
                                "nsfw:${it.nsfwScore}\nsfw:${it.sfwScore}\n扫描耗时：${it.timeConsumingToScanData} ms"
                            if (it.nsfwScore>0.7){
                                this.setBackgroundColor(Color.parseColor("#C1FF0000"))
                            }else if (it.nsfwScore>0.5){
                                this.setBackgroundColor(Color.parseColor("#C1FF9800"))
                            }else {
                                this.setBackgroundColor(Color.parseColor("#803700B3"))
                            }
                        }
                    }

                }
            }.also { adapter ->
                adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
                adapter.isAnimationFirstOnly = false

                resources.assets.also { asset ->
                    asset.list("img")?.forEach {
                        "img/$it".also { assetFilePath ->
                            BitmapFactory.decodeStream(asset.open(assetFilePath)).also {
                                adapter.addData(MyNSFWBean(it.getNSFWScore(), it))
                            }
                        }
                    }
                }

            }

    }
}

data class MyNSFWBean(val nsfwScoreBean: NSFWScoreBean, val bitmap: Bitmap)
