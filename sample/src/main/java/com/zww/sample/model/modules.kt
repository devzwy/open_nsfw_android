package com.zww.sample.model

import com.google.gson.Gson
import com.zww.sample.BuildConfig
import com.zww.sample.interceptors.Level
import com.zww.sample.interceptors.LoggingInterceptor
import com.zww.sample.ui.viewmodels.MainViewModel
import com.zww.sample.ui.viewmodels.SplashViewModel
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    //OkHttpClient
    single {
        OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .tag("离线鉴黄App")
                    .build()
            )
            .build()
    }
    //Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //ApiService
    single {
        get<Retrofit>().create(ApiService::class.java)
    }

    //Gson
    single {
        Gson()
    }
}


val repositoryModule = module {

    single {
        AppRepository(get(), get())
    }
}
val viewModelModules = module {
    viewModel {
        SplashViewModel()
    }
    viewModel {
        MainViewModel()
    }


}
