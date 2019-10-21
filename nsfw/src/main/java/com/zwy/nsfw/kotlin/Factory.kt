package com.zwy.nsfw.kotlin

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.zwy.nsfw.api.NSFWHelper
import com.zwy.nsfw.core.NSFWConfig
import com.zwy.nsfw.core.NsfwBean
import java.io.File


fun Bitmap.getNsfwScore(mAssetManager: AssetManager): NsfwBean {
    val nsfwBean = NSFWHelper.init(NSFWConfig(mAssetManager)).scanBitmap(this)
    NSFWHelper.destroyFactory()
    return nsfwBean
}

fun File.getNsfwScore(mAssetManager: AssetManager): NsfwBean {
    val bitmap = try {
        BitmapFactory.decodeFile(this.path)
    } catch (e: Exception) {
        return NsfwBean(0f, 0f)
    }

    val nsfwBean = NSFWHelper.init(NSFWConfig(mAssetManager)).scanBitmap(bitmap)
    NSFWHelper.destroyFactory()
    return nsfwBean
}