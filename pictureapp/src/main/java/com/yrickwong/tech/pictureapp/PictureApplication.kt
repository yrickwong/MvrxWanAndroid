package com.yrickwong.tech.pictureapp

import android.app.Application
import android.content.Context
import com.airbnb.mvrx.Mavericks
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


const val BASE_URL = "https://image.so.com"

class PictureApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(this)
        startKoin {
            androidContext(androidContext = this@PictureApplication)
            modules(API_SERVICE_MODULE)
        }
    }
}

//依赖注入
private val API_SERVICE_MODULE: Module = module {
    //每次都给它个新的
    factory {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    }


    factory {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    factory {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(get<OkHttpClient>())
            .build()
    }
    //单例
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}