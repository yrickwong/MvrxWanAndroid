package com.yrickwong.tech.mvrx.tab

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.*
import com.google.android.material.tabs.TabLayout
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.core.BaseFragment
import com.yrickwong.tech.mvrx.feature.wechat.WeChatPagerAdapter
import com.yrickwong.tech.mvrx.feature.wechat.WeChatViewModel
import kotlinx.android.synthetic.main.fragment_wechat.*

private const val TAG = "WeChatFragment"

class WeChatFragment : BaseMvRxFragment() {


    /**
     * ViewPagerAdapter
     */
    private val viewPagerAdapter: WeChatPagerAdapter by lazy {
        WeChatPagerAdapter(childFragmentManager)
    }

    private val wechatViewModel: WeChatViewModel by fragmentViewModel()//定义成Activity说明可以再fragment中间进行数据传递复用

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wechat, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.run {
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }

        tabLayout.run {
            setupWithViewPager(viewPager)
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    // 默认切换的时候，会有一个过渡动画，设为false后，取消动画，直接显示
                    tab?.let {
                        viewPager.setCurrentItem(it.position, false)
                    }
                }

            })
        }

    }

    override fun invalidate() {
        withState(wechatViewModel) { state ->
            when {
                state.request is Loading -> {
                    Log.w(TAG, "wechatChapter request Loading")
                }
                state.request is Success -> {
                    Log.w(TAG, "wechatChapter request Success")
                    viewPager.run {
                        viewPagerAdapter.setData(state.wxChapters)
                        adapter = viewPagerAdapter
                        offscreenPageLimit = state.wxChapters.size
                    }
                }
                state.request is Fail -> {
                    Log.w(TAG, "wechatChapter request failed", state.request.error)
                }
                else -> {

                }
            }
        }
    }

}