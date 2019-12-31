package com.yrickwong.tech.mvrx.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.widget.viewpager.BannerAdapter
import kotlinx.android.synthetic.main.banner_item_view.view.*


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class BannerItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var bannerAdapter = BannerAdapter()

    init {
        LayoutInflater.from(context).inflate(R.layout.banner_item_view, this, true)
        bannerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (bannerViewPager.adapter == null || bannerViewPager.adapter!!.count <= 0) {
                    return
                }
                nameView.text = bannerAdapter.getBanner(position).title
            }

        })
    }


    @ModelProp
    fun setBanners(bannerList: List<Banner>) {
        Log.d("wangyi", "setBanners: size=${bannerList.size}")
        if (bannerList.isNotEmpty()) {
            bannerAdapter.list = bannerList
            bannerViewPager.adapter = bannerAdapter
            bannerIndicator.setViewPager(bannerViewPager)
        }
    }

    @CallbackProp
    fun setClickListener(block: ((Banner) -> Unit)?) {
        bannerAdapter.callback = block
    }
}