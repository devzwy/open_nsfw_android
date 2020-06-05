package com.zwy.demo.utils

import android.app.Activity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType

/**
 * 相册选择
 */
fun Activity.selectImgFromD(requestCode: Int) {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())//全部.ofAll()、图片.、视频.ofVideo()、音频.ofAudio()
        .maxSelectNum(if (requestCode == 0) 20 else 1)// 最大图片选择数量 int
        .minSelectNum(1)// 最小选择数量 int
        .imageSpanCount(3)// 每行显示个数 int
        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选  or PictureConfig.SINGLE
        .previewImage(true)// 是否可预览图片 true or false
        .isCamera(false)// 是否显示拍照按钮 true or false
        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//        .selectionMedia(selectList)
        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
        .forResult(requestCode);//结果回调onActivityResult code }
}