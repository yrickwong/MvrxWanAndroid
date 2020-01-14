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
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Account
import com.yrickwong.tech.mvrx.bean.HttpResult
import com.yrickwong.tech.mvrx.core.MvRxViewModel
import com.yrickwong.tech.mvrx.network.ApiService
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

private const val REGISTER_STRING = "Don\\'t have an account? Sign up"
private const val REGISTER_ONE = "Sign up"

data class LoginState(val loginRequest: Async<HttpResult<Account>> = Uninitialized) : MvRxState

class LoginViewModel(loginState: LoginState, private val apiService: ApiService) :
    MvRxViewModel<LoginState>(loginState) {

    fun signIn(username: String, password: String) = withState { state ->
        if (state.loginRequest is Loading) return@withState //避免重复请求

        //添加subscribeOn为了有loading效果
        apiService.signIn(username, password).subscribeOn(Schedulers.io()).execute {
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
                loginViewModel.signIn(et_username.text.toString(), et_password.text.toString())
            }
        }
        loginViewModel.asyncSubscribe(LoginState::loginRequest,

            onSuccess = { result ->
                if (result.data == null) {
                    this@LoginFragment.showToast(result.errorMsg)
                } else {
                    showToast("login success!")
                    requireActivity().finish()
                }
            },
            onFail = {
                Log.d("wangyi", "error=${it.printStackTrace()} ")
            }
        )
        tv_sign_up.text = getClickableSpan()
        tv_sign_up.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getClickableSpan(): SpannableString {
        val spanStr = SpannableString(REGISTER_STRING)
        //设置文字的单击事件
        spanStr.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_to_register_fragment)
            }
        }, REGISTER_STRING.length-REGISTER_ONE.length, REGISTER_STRING.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //设置文字的前景色
        spanStr.setSpan(ForegroundColorSpan(Color.RED), REGISTER_STRING.length-REGISTER_ONE.length, REGISTER_STRING.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanStr
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

//扩展函数
fun Fragment.showToast(text: CharSequence) =
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()