package com.zww.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kongzue.dialog.v3.MessageDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.zww.sample.databinding.MainLayoutBinding
import com.zww.sample.ui.viewmodels.MainViewModel
import shop.gedian.www.base.BaseActivity
import java.io.File
import java.util.*

class MainActivity : BaseActivity<MainLayoutBinding, MainViewModel>() {
    /**
     *
     * 父类onCreate执行完成后调用
     */
    override fun initData() {
        mBinding.model = mViewModel
        mViewModel.selectPhoto.observe(this, androidx.lifecycle.Observer<Int>
        {
            if (it == 0x01) {
                c()
            }
        })
        mViewModel.dialogShow.observe(this, androidx.lifecycle.Observer {
            if (it){
                MessageDialog.build(this).also {
                    it.message = "后台正在下载模型文件，大约耗时1分钟，请等待完成。(简化demo未做进度条处理，完成后会有toast提示)"
                    it.title = "提示"
                    it.cancelable = false
                    it.okButton = "好的"
                    it.show()
                }
            }else{
                MessageDialog.build(this).also {
                    it.message = "模型已下载，等待模型初始化完成的toast后可点击右上角识别按钮"
                    it.title = "提示"
                    it.cancelable = false
                    it.okButton = "好的"
                    it.show()
                }
            }
        })
        mViewModel.isShowDialog()
    }

    override fun isInitImmersionBar() = true

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    fun c() {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())//全部.ofAll()、图片.、视频.ofVideo()、音频.ofAudio()
            .maxSelectNum(20)// 最大图片选择数量 int
            .minSelectNum(1)// 最小选择数量 int
            .imageSpanCount(3)// 每行显示个数 int
            .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选  or PictureConfig.SINGLE
            .previewImage(true)// 是否可预览图片 true or false
            .isCamera(false)// 是否显示拍照按钮 true or false
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//        .selectionMedia(selectList)
            .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .forResult(0x00);//结果回调onActivityResult code }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            mViewModel.toScorePhoto(arrayListOf<File>().also {
                PictureSelector.obtainMultipleResult(data).forEach { localMedia ->
                    it.add(File(localMedia.path))
                }
            })
        }
    }
}
