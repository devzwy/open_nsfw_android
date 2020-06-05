package com.zwy.demo.models

import androidx.lifecycle.MutableLiveData
import com.zwy.demo.base.BaseViewModel
import com.zwy.demo.dbbean.HomeTitle
import com.zwy.demo.repositorys.AppRepository

class MainViewModel constructor(val mAppRepository: AppRepository) :
    BaseViewModel() {

    var titles = MutableLiveData<List<HomeTitle>>()


    fun getTitles() {
        this.titles.value = mAppRepository.getHomeTitles()
    }
}


