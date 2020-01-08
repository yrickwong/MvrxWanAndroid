package com.yrickwong.tech.mvrx.widget.viewpager

import android.content.Context
import android.widget.Scroller

class BannerScroller(context: Context, private val mDuration: Int) : Scroller(context) {
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, mDuration)
    }
}