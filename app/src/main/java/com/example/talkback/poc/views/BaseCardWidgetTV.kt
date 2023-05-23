package com.example.talkback.poc.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.leanback.widget.BaseCardView
import com.example.talkback.poc.models.Card

abstract class BaseCardWidgetTV @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    hideMeta: Boolean = false,
    templateId: String? = null
) : BaseCardView(context, attrs, defStyleAttr) {

    init {
        addView(getBindingView(hideMeta, templateId))
    }

    abstract fun getBindingView(hideMeta: Boolean, templateId: String?): View

    abstract fun bindData(card: Card)
}
