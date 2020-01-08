package com.yrickwong.tech.mvrx.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Banner
import com.yrickwong.tech.mvrx.widget.viewpager.BannerAdapter
import kotlinx.android.synthetic.main.banner_item_view.view.*
import java.lang.ref.WeakReference


private const val AUTO_PLAY_INTERVAL = 3000L

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class BannerItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mAutoPlayAble = true
    private val bannerAdapter = BannerAdapter()
    private val mAutoPlayTask = AutoPlayTask(this)

    init {
        LayoutInflater.from(context).inflate(R.layout.banner_item_view, this, true)
        bannerViewPager.run {
            setPageChangeDuration(800)
            addOnPageChangeListener(object : SimpleOnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager.SCROLL_STATE_DRAGGING -> {
                            stopAutoPlay()
                        }
                        ViewPager.SCROLL_STATE_SETTLING -> {
                            stopAutoPlay()
                        }
                        ViewPager.SCROLL_STATE_IDLE -> {
                            startAutoPlay()
                        }
                    }

                }


                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (bannerViewPager.adapter == null || bannerViewPager.adapter!!.count <= 0) {
                        return
                    }
                    this@BannerItemView.nameView.text = bannerAdapter.getBanner(position).title
                }
            })
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onInvisibleToUser()
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            startAutoPlay()
        } else if (visibility == View.INVISIBLE || visibility == View.GONE) {
            onInvisibleToUser()
        }
    }

    private fun onInvisibleToUser() {
        stopAutoPlay()
    }

    fun startAutoPlay() {
        stopAutoPlay()
        if (mAutoPlayAble) {
            postDelayed(mAutoPlayTask, AUTO_PLAY_INTERVAL)
        }
    }

    fun stopAutoPlay() {
        removeCallbacks(mAutoPlayTask)
    }

    /**
     * 切换到下一页
     */
    fun switchToNextPage() {
        bannerViewPager.currentItem = (bannerViewPager.currentItem + 1)
    }


    @ModelProp
    fun setBanners(bannerList: List<Banner>) {
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

    inner class AutoPlayTask constructor(viewPager: BannerItemView) : Runnable {

        private val mViewPager: WeakReference<BannerItemView> = WeakReference(viewPager)

        override fun run() {
            val banner = mViewPager.get()
            if (banner != null) {
                banner.switchToNextPage()
                banner.startAutoPlay()
            }
        }

    }
}


interface SimpleOnPageChangeListener : ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

}