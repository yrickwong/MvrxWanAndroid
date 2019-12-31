package com.yrickwong.tech.mvrx.widget.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Banner
import kotlinx.android.synthetic.main.banner_row.view.*

class BannerAdapter : PagerAdapter() {

    var callback: ((Banner) -> Unit)? = null

    var list: List<Banner> = mutableListOf()

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) {
        view.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val context = view.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemLayout = inflater.inflate(R.layout.banner_row, view, false)
        val banner = getBanner(position)
        Picasso.with(context)
            .load(banner.imagePath)
            .into(itemLayout.image)
        itemLayout.setOnClickListener {
            callback?.invoke(banner)
        }
        view.addView(itemLayout, 0)
        return itemLayout
    }

    public fun getBanner(position: Int): Banner {
        return list[position]
    }

}