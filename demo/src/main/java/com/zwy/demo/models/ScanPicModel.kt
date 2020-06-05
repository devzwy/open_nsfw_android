package com.zwy.demo.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.zwy.demo.NSFWApplication
import com.zwy.demo.base.BaseViewModel
import com.zwy.demo.dbbean.ImageBean
import com.zwy.demo.repositorys.AppRepository
import com.zwy.demo.utils.AppLiveData
import getNsfwScore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.java.KoinJavaComponent.inject

class ScanPicViewModel constructor(val mAppRepository: AppRepository) :
    BaseViewModel() {


    var imgData = MutableLiveData<List<ImageBean>>()

    val context: Context by inject(Application::class.java)


    @SuppressLint("CheckResult")
    fun startScann(pageIndex: Int) {
        mAppRepository.startScann(pageIndex)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                addSubscribe(it)
                appLiveData.value =
                    AppLiveData(0, if (pageIndex == 0x03) "多图下载较慢，请耐心等待......." else "描中请稍后.......")
            }
            .map {
                return@map when (pageIndex) {
                    0x01 -> {
                        it.filter {
                            val bitmap =
                                BitmapFactory.decodeStream(context.resources.assets.open(it.imgUrl))
                            val nsfwScore = bitmap.getNsfwScore()
                            it.nsfw = nsfwScore.nsfw
                            it.sfw = nsfwScore.sfw
                            bitmap.recycle()
                            true
                        }
                    }
                    0x02 -> {
                        it
                    }
                    0x03 -> {
                        it.filter {
                            val nsfwBean =
                                Glide.with(NSFWApplication.context).asBitmap().load(it.imgUrl)
                                    .submit().get().getNsfwScore()
                            it.sfw = nsfwBean.sfw
                            it.nsfw = nsfwBean.nsfw
                            true
                        }
                        if (it.size > 10) it.subList(it.size - 10, it.size)
                        else it

                    }
                    else -> {
                        it
                    }

                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                appLiveData.value = AppLiveData(1, "")
                imgData.value = it
            }, {
                appLiveData.value = AppLiveData(1, "")
                print(it)
            })

    }


}


