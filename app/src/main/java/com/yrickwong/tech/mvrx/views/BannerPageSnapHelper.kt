package com.yrickwong.tech.mvrx.views

import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class BannerPageSnapHelper : PagerSnapHelper() {

    private var isInfinite = true
    private var horizontalHelper: OrientationHelper? = null

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager, velocityX: Int,
        velocityY: Int
    ): Int {
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }

        val mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager))
            ?: return RecyclerView.NO_POSITION

        val centerPosition = layoutManager.getPosition(mStartMostChildView)
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }

        val forwardDirection: Boolean
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0
        } else {
            forwardDirection = velocityY > 0
        }

        return if (forwardDirection) {
            if (centerPosition == layoutManager.itemCount - 1) {
                if (isInfinite) 0 else layoutManager.itemCount - 1
            } else {
                centerPosition + 1
            }
        } else {
            centerPosition
        }
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }

        var closestChild: View? = null
        var start = Integer.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)

            /** if child is more to start than previous closest, set it as closest   */
            if (childStart < start) {
                start = childStart
                closestChild = child
            }
        }
        return closestChild
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (horizontalHelper == null) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper!!
    }
}