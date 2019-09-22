package com.yrickwong.tech.mvrx.rxjava

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rxjava.*
import org.koin.android.ext.android.inject

const val TAG = "RxjavaTestActivity"

class RxjavaTestActivity : AppCompatActivity() {

    private val apiSevice by inject<ApiService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rxjava)
        val rxService = RxService(apiSevice).also {
            it.fetchBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Banner>> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: List<Banner>) {

                    }

                    override fun onError(e: Throwable) {
                    }

                })
        }
        ObjectAnimator.ofFloat(circleView, "radius", circleView.radius, 50.0f.dp2px, 150f.dp2px)
            .apply {
                duration = 1000
                startDelay = 500
            }.start()
    }
}