package com.example.open_nsfw_android

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.zwy.nsfw.api.NsfwHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var nsfwHelper: NsfwHelper? = null
    var mainAdapter: MainAdapter? = null
    var index = 0
    var listData: ArrayList<MyNsfwBean> = ArrayList<MyNsfwBean>()
    var selectList: List<LocalMedia>? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNsfwHelper()
        initAdapter()
        initClickListener()
        tv_version.text = "当前版本：${this.packageManager.getPackageInfo(packageName, 0).versionName}"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_sc_assets -> {
                reScAssetsImgs()
            }
            R.id.bt_sc_from_other -> {
                PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())//全部.ofAll()、图片.、视频.ofVideo()、音频.ofAudio()
                    .maxSelectNum(20)// 最大图片选择数量 int
                    .minSelectNum(1)// 最小选择数量 int
                    .imageSpanCount(3)// 每行显示个数 int
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选  or PictureConfig.SINGLE
                    .previewImage(true)// 是否可预览图片 true or false
                    .isCamera(false)// 是否显示拍照按钮 true or false
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .selectionMedia(selectList)
                    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                    .forResult(0x01);//结果回调onActivityResult code
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x01 && resultCode == RESULT_OK) {
            selectList = PictureSelector.obtainMultipleResult(data)
            if (selectList != null && selectList?.size ?: 0 > 0)
                reScFromImgs(selectList!!)
        }
    }


    private fun initClickListener() {
        bt_sc_assets.setOnClickListener(this)
        bt_sc_from_other.setOnClickListener(this)
    }

    private fun initAdapter() {
        mainAdapter = MainAdapter(null)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mainAdapter
    }

    private fun initNsfwHelper() {
        nsfwHelper = NsfwHelper.getInstance(this, true, 4)
    }

    private fun reScFromImgs(list: List<LocalMedia>) {
        index = 0
        mainAdapter?.setNewData(null)
        listData = ArrayList<MyNsfwBean>()
        Thread(Runnable {
            for (lm in list) {
                val bitmap = BitmapFactory.decodeFile(lm.path)
                listData.add(MyNsfwBean(0.0f, 0.0f, lm.path, bitmap))
                nsfwHelper?.scanBitmap(bitmap) { sfw, nsfw ->
                    listData[index].sfw = sfw
                    listData[index].nsfw = nsfw
                    mainAdapter?.addData(listData[index])
                    mainAdapter?.notifyItemInserted(index)
                    rv.scrollToPosition(index)
                    index++
                }
            }
        }).start()
    }

    private fun reScAssetsImgs() {
        index = 0
        mainAdapter?.setNewData(null)
        listData = ArrayList<MyNsfwBean>()
        for (a in resources.assets.list("img")) {
            val path = "img/${a}"
            val b = BitmapFactory.decodeStream(resources.assets.open(path))
            listData.add(MyNsfwBean(0f, 0f, path, b))
            nsfwHelper?.scanBitmap(b) { sfw, nsfw ->
                listData[index].sfw = sfw
                listData[index].nsfw = nsfw
                mainAdapter?.addData(listData[index])
                mainAdapter?.notifyItemInserted(index)
                rv.scrollToPosition(index)
                index++
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        nsfwHelper?.destroy()
    }
}
