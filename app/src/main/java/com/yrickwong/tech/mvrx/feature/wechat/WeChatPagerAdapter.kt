package com.yrickwong.tech.mvrx.feature.wechat

import android.text.Html
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.yrickwong.tech.mvrx.bean.WXChapterBean

/**
 * @author chenxz
 * @date 2018/10/28
 * @desc
 */
class WeChatPagerAdapter(fm: FragmentManager?) :
    FragmentStatePagerAdapter(fm) {

    val fragments = mutableListOf<Fragment>()

    private lateinit var list: List<WXChapterBean>

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = list.size

    override fun getPageTitle(position: Int): CharSequence? = Html.fromHtml(list[position].name)

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE


    fun setData(wxChapters: List<WXChapterBean>) {
        fragments.clear()
        list = wxChapters
        list.forEach {
            fragments.add(KnowledgeFragment.getInstance(it.id))
        }
    }
}