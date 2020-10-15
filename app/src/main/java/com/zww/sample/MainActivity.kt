package com.zww.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zwy.nsfw.getNSFWScore
import java.util.*
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resources.assets.also { asset ->
            asset.list("img")?.forEach {
                "img/$it".also { assetFilePath ->
                    val startTime = Date().time
                    BitmapFactory.decodeStream(asset.open(assetFilePath)).also {
                        it.getNSFWScore()
                        it.recycle()
                    }
                }
            }
        }
    }
}
