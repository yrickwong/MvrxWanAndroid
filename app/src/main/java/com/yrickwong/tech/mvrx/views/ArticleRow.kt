package com.yrickwong.tech.mvrx.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.yrickwong.tech.mvrx.R
import com.yrickwong.tech.mvrx.bean.Article
import kotlinx.android.synthetic.main.article_row.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ArticleRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.article_row, this, true)
    }


    @ModelProp
    fun setArticle(article: Article) {
        title.text = article.title
        chapterName.text = "${article.superChapterName}/${article.chapterName}"
    }

    @CallbackProp
    fun setClickListener(listener: OnClickListener?) {
        setOnClickListener(listener)
    }
}
