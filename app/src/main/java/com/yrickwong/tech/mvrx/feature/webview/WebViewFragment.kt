package com.yrickwong.tech.mvrx.feature.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.mvrx.args
import com.yrickwong.tech.mvrx.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_webview.*

@SuppressLint("ParcelCreator")
@Parcelize
data class WebViewDetailArgs(val url: String, val title: String) : Parcelable


class WebViewFragment : BaseWebMvRxFragment() {

    private lateinit var toolbar: Toolbar

    private val webViewArgs: WebViewDetailArgs by args()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false).apply {
            toolbar = findViewById(R.id.toolbar)
            toolbar.setupWithNavController(findNavController())//设置返回
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.apply {
            text = webViewArgs.title
            postDelayed({
                tv_title.isSelected = true
            }, 150)
        }
    }

    override fun getUrl(): String? = webViewArgs.url

    override fun invalidate() {

    }

    override fun getAgentWebParent(): ViewGroup = webview_container

}