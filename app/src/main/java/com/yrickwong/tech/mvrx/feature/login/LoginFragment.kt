package com.yrickwong.tech.mvrx.feature.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Account
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.BaseEpoxyFragment
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.core.simpleController
import com.yrickwong.tech.mvrx.feature.banner.BannerState
import com.yrickwong.tech.mvrx.feature.banner.BannerViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject


data class LoginState(val loginRequest: Async<HttpResult<Account>> = Uninitialized) : MvRxState

class LoginViewModel(loginState: LoginState, private val apiService: ApiService) :
    MvRxViewModel<LoginState>(loginState) {

    fun login(username: String, password: String) = withState { state ->
        if (state.loginRequest is Loading) return@withState //避免重复请求
        Log.d("wangyi", "login: ")
        apiService.signIn(username, password).execute {
            copy(loginRequest = it)
        }
    }

    companion object : MvRxViewModelFactory<LoginViewModel, LoginState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: LoginState
        ): LoginViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return LoginViewModel(state, service)
        }
    }
}

class LoginFragment : BaseMvRxFragment() {

    private val loginViewModel: LoginViewModel by fragmentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_login.setOnClickListener {
            if (validate()) {
                loginViewModel.login(et_username.text.toString(), et_password.text.toString())
            }
        }
        loginViewModel.asyncSubscribe(LoginState::loginRequest,
            onSuccess = {
                Log.d("wangyi", "onSuccess: ")
            },
            onFail = {
                Log.d("wangyi", "error=${it.printStackTrace()} ")
            }
        )
    }

    override fun invalidate() = withState(loginViewModel) { state ->
        loadingAnimation.isVisible = state.loginRequest is Loading
    }

    /**
     * Check UserName and PassWord
     */
    private fun validate(): Boolean {
        var valid = true
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()

        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            valid = false
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            valid = false
        }
        return valid

    }
}