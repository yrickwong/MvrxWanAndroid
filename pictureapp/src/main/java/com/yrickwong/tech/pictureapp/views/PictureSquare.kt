package com.yrickwong.tech.pictureapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.squareup.picasso.Picasso
import com.yrickwong.tech.pictureapp.bean.Picture
import com.yrickwong.tech.pictureapp.R
import kotlinx.android.synthetic.main.picture_square.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class PictureSquare @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.picture_square, this, true)
    }

    @ModelProp
    fun setPicture(picture: Picture) {
        Picasso.with(context)
            .load(picture.thumb)
            .into(thumb)
        title.text = picture.title
    }

    @CallbackProp
    fun setClickListener(listener: OnClickListener?) {
        setOnClickListener(listener)
    }
}