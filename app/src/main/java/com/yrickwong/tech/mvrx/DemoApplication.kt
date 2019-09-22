package com.yrickwong.tech.mvrx

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yrickwong.tech.mvrx.network.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class DemoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(androidContext = this@DemoApplication)
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
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
    //单例
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}