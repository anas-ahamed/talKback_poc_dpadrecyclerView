package com.example.talkback.poc.presenter

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.Presenter
import com.example.talkback.poc.R
import com.example.talkback.poc.models.Card
import com.example.talkback.poc.ui.animateOnFocus
import com.example.talkback.poc.views.BaseCardWidgetTV
import com.example.talkback.poc.views.PosterCardView
import com.example.talkback.poc.views.atom.AtomRoundedImage
import kotlinx.coroutines.Job

internal class ContentGridRowPresenter(
    val mContext: Context,
    val id: Int
) : Presenter() {


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view: View = posterCardView()
        return ViewHolder(view.apply {
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
        })
    }

    private fun posterCardView() = PosterCardView(
        context = mContext
    ).apply {
        infoVisibility = BaseCardView.CARD_REGION_VISIBLE_ALWAYS
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        bindCardViewData(viewHolder, item)
        if (((item as? Card)?.id ?: 0) == 0) {
            viewHolder.view.id = id
        }
        viewHolder.view.setOnFocusChangeListener { view, hasFocus ->
            view.findViewById<ConstraintLayout>(R.id.showCardContainer).animateOnFocus(hasFocus)
        }
        setViewFocusListener(viewHolder)
    }

    private fun setViewFocusListener(viewHolder: ViewHolder) {
        viewHolder.view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                println("talback focus down")
            }
            false
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        when (val view = viewHolder?.view) {
            is BaseCardWidgetTV -> {
                view.findViewById<AtomRoundedImage>(R.id.cardImage)?.setImageDrawable(null)
            }
        }
    }

    private fun bindCardViewData(viewHolder: ViewHolder, item: Any) {
        when (viewHolder.view) {
            is PosterCardView -> (item as? Card)?.let { bindPosterCard(viewHolder, it) }
        }
    }

    private fun bindPosterCard(
        viewHolder: ViewHolder,
        card: Card
    ) {
        (viewHolder.view as PosterCardView).run {
            bindData(card)
        }
    }
}
