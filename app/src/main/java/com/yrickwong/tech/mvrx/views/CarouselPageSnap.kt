package com.yrickwong.tech.mvrx.views

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelView.Size

@ModelView(saveViewState = true, autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
class CarouselPageSnap(context: Context) : Carousel(context) {

    init {
        setDefaultItemSpacingDp(0)
    }

    override fun getSnapHelperFactory(): SnapHelperFactory = CarouselPageSnap

    companion object : SnapHelperFactory() {
        override fun buildSnapHelper(context: Context?): SnapHelper = BannerPageSnapHelper()
    }

}
