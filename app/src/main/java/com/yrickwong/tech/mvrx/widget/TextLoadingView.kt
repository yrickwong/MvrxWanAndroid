package com.yrickwong.tech.mvrx.widget

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main.view.*

const val TAG = "TextLoadingView"

class TextLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private val wasAnimatingWhenDetached: Boolean = false

    private var animator: ValueAnimator

    init {
        val spannableString = SpannableString(this.text)
        val transParentColor = ForegroundColorSpan(Color.TRANSPARENT)
        animator = ValueAnimator.ofInt(0, 4).apply {
            repeatCount = INFINITE
            duration = 1000
            addUpdateListener { valueAnimator ->
                val dotsCount = valueAnimator.animatedValue as Int

                if (dotsCount < 4) {
                    spannableString.setSpan(
                        transParentColor,
                        15 + dotsCount,
                        18,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    this@TextLoadingView.text = spannableString
                }
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == View.VISIBLE) {
            playAnimation()
        } else {
            cancelAnimation()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playAnimation()
    }

    override fun onDetachedFromWindow() {
        cancelAnimation()
        super.onDetachedFromWindow()
    }

    private fun playAnimation() {
        animator.start()
    }

    private fun cancelAnimation() {
        animator.cancel()
    }
}
