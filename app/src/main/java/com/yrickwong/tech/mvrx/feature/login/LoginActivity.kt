package com.yrickwong.tech.mvrx.feature.login

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import com.yrickwong.tech.mvrx.R

class LoginActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

}