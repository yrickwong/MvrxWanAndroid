package com.yrickwong.tech.mvrx.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.squareup.picasso.Picasso
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Banner
import kotlinx.android.synthetic.main.banner_row.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class BannerRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.banner_row, this, true)
    }

    @ModelProp
    fun setBanner(banner: Banner) {
        Picasso.with(context)
            .load(banner.imagePath)
            .into(image)
        nameView.text = banner.title
    }

    @CallbackProp
    fun setClickListener(listener: OnClickListener?) {
        setOnClickListener(listener)
    }
}
