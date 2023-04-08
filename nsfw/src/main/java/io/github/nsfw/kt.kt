package io.github.nsfw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.InputStream

fun File.decodeNsfwScore() = Nsfw.decode(BitmapFactory.decodeFile(this.absolutePath))

fun String.decodeNsfwScore() = Nsfw.decode(BitmapFactory.decodeFile(this))

fun Bitmap.decodeNsfwScore() = Nsfw.decode(this)

fun ByteArray.decodeNsfwScore() = Nsfw.decode(BitmapFactory.decodeByteArray(this,0,this.size))

fun InputStream.decodeNsfwScore() = Nsfw.decode(BitmapFactory.decodeStream(this,))
