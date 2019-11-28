package com.yrickwong.tech.mvrx.views

import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HorizontalLayoutManager : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider {

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0)!!)
        val direction = if (targetPosition < firstChildPos) -1 else 1
        return PointF(direction.toFloat(), 0f)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        //分离并且回收当前附加的所有View
        detachAndScrapAttachedViews(recycler)

        if (itemCount == 0) {
            return
        }
        //横向绘制子View,则需要知道 X轴的偏移量
        var offsetX = 0

        //绘制并添加view
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)

            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecorated(view, offsetX, 0, offsetX + viewWidth, viewHeight)
            offsetX += viewWidth

            if (offsetX > width) {
                break
            }
        }
    }

    //是否可横向滑动
    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State
    ): Int {
        recycleViews(dx, recycler)
        fill(dx, recycler)
        offsetChildrenHorizontal(dx * -1)
        return dx
    }

    private fun fill(dx: Int, recycler: RecyclerView.Recycler) {
        //左滑
        if (dx > 0) {

            while (true) {
                //得到当前已添加（可见）的最后一个子View
                val lastVisibleView = getChildAt(childCount - 1) ?: break

                //如果滑动过后，View还是未完全显示出来就 不进行绘制下一个View
                if (lastVisibleView.right - dx > width)
                    break

                //得到View对应的位置
                val layoutPosition = getPosition(lastVisibleView)
                /**
                 * 例如要显示20个View，当前可见的最后一个View就是第20个，那么下一个要显示的就是第一个
                 * 如果当前显示的View不是第20个，那么就显示下一个，如当前显示的是第15个View，那么下一个显示第16个
                 * 注意区分 childCount 与 itemCount
                 */
                val nextView: View = if (layoutPosition == itemCount - 1) {
                    recycler.getViewForPosition(0)
                } else {
                    recycler.getViewForPosition(layoutPosition + 1)
                }

                addView(nextView)
                measureChildWithMargins(nextView, 0, 0)
                val viewWidth = getDecoratedMeasuredWidth(nextView)
                val viewHeight = getDecoratedMeasuredHeight(nextView)
                val offsetX = lastVisibleView.right
                layoutDecorated(nextView, offsetX, 0, offsetX + viewWidth, viewHeight)
            }
        } else { //右滑
            while (true) {
                val firstVisibleView = getChildAt(0) ?: break

                if (firstVisibleView.left - dx < 0) break

                val layoutPosition = getPosition(firstVisibleView)
                /**
                 * 如果当前第一个可见View为第0个，则左侧显示第20个View 如果不是，下一个就显示前一个
                 */
                val nextView = if (layoutPosition == 0) {
                    recycler.getViewForPosition(itemCount - 1)
                } else {
                    recycler.getViewForPosition(layoutPosition - 1)
                }

                addView(nextView, 0)
                measureChildWithMargins(nextView, 0, 0)
                val viewWidth = getDecoratedMeasuredWidth(nextView)
                val viewHeight = getDecoratedMeasuredHeight(nextView)
                val offsetX = firstVisibleView.left
                layoutDecorated(nextView, offsetX - viewWidth, 0, offsetX, viewHeight)
            }
        }
    }

    private fun recycleViews(dx: Int, recycler: RecyclerView.Recycler) {
        for (i in 0 until itemCount) {
            val childView = getChildAt(i) ?: return
            //左滑
            if (dx > 0) {
                //移除并回收 原点 左侧的子View
                if (childView.right - dx < 0) {
                    removeAndRecycleViewAt(i, recycler)
                }
            } else { //右滑
                //移除并回收 右侧即RecyclerView宽度之以外的子View
                if (childView.left - dx > width) {
                    removeAndRecycleViewAt(i, recycler)
                }
            }
        }
    }
}