package com.yrickwong.tech.mvrx.feature.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Account
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.ext.android.inject

private const val REGISTER_STRING = "Already have an account? Sign in"
private const val LINK_STRING = "Sign in"
private const val TAG = "RegisterFragment"


data class RegisterState(val registerRequest: Async<HttpResult<Account>> = Uninitialized) :
    MvRxState

class RegisterViewModel(registerState: RegisterState, private val apiService: ApiService) :
    MvRxViewModel<RegisterState>(registerState) {

    fun createAccount(username: String, password: String, repassword: String) = withState { state ->
        if (state.registerRequest is Loading) return@withState //避免重复请求

        //添加subscribeOn为了有loading效果
        apiService.registerWanAndroid(username, password, repassword).subscribeOn(Schedulers.io())
            .execute {
                copy(registerRequest = it)
            }
    }

    companion object : MvRxViewModelFactory<RegisterViewModel, RegisterState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: RegisterState
        ): RegisterViewModel {
            val service: ApiService by viewModelContext.activity.inject()
            return RegisterViewModel(state, service)
        }
    }
}

class RegisterFragment : BaseMvRxFragment() {

    private val registerViewModel: RegisterViewModel by fragmentViewModel()

    override fun invalidate() = withState(registerViewModel) { state ->
        loadingAnimation.isVisible = state.registerRequest is Loading
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_create_account.setOnClickListener {
            if (validate()) {
                registerViewModel.createAccount(
                    et_username.text.toString(),
                    et_password.text.toString(),
                    et_password_confirm.text.toString()
                )
            }
        }
        registerViewModel.asyncSubscribe(
            RegisterState::registerRequest,
            onSuccess = {
                showToast("register success!")
                findNavController().popBackStack()
            },
            onFail = {
                Log.d("wangyi", "error=${it.printStackTrace()} ")
            }
        )
        tv_sign_in.text = getClickableSpan()
        tv_sign_in.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getClickableSpan(): SpannableString {
        val spanStr = SpannableString(REGISTER_STRING)
        //设置文字的单击事件
        spanStr.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().popBackStack()
                }
            },
            REGISTER_STRING.length - LINK_STRING.length,
            REGISTER_STRING.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置文字的前景色
        spanStr.setSpan(
            ForegroundColorSpan(Color.RED),
            REGISTER_STRING.length - LINK_STRING.length,
            REGISTER_STRING.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spanStr
    }

    /**
     * Check UserName and PassWord
     */
    private fun validate(): Boolean {
        var valid = true
        val username: String = et_username.text.toString()
        val password: String = et_password.text.toString()
        val confirmPassword: String = et_password_confirm.text.toString()

        if (username.isEmpty()) {
            et_username.error = getString(R.string.username_not_empty)
            valid = false
        }
        if (password.isEmpty()) {
            et_password.error = getString(R.string.password_not_empty)
            valid = false
        }
        if (confirmPassword.isEmpty()) {
            et_password_confirm.error = getString(R.string.password_not_empty)
            valid = false
        }
        return valid

    }
}