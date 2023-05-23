package com.example.talkback.poc.presenter

import android.view.KeyEvent
import android.view.ViewGroup.FOCUS_BEFORE_DESCENDANTS
import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView

class ContentGridListRowPresenter(
    private val useGridNavigation: Boolean,
    private val horizontalSpacing: Int,
    private val railWindowAlignmentOffset: Int
) : ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE) {

    private var listRowView: ListRowView? = null
    private var horizontalGridView: HorizontalGridView? = null
    val listView: RecyclerView? get() = horizontalGridView

    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder?, item: Any?) {
        super.onBindRowViewHolder(holder, item)
        listRowView = holder?.view as? ListRowView
        horizontalGridView = listRowView?.gridView
        horizontalGridView?.horizontalSpacing = horizontalSpacing
        if (useGridNavigation) {
            initGridNavigation(holder)
        } else {
            listRowView?.gridView?.run {
                windowAlignmentOffsetPercent = 0f
                windowAlignmentOffset = railWindowAlignmentOffset
                itemAlignmentOffsetPercent = 0f
            }
        }
    }

    private fun initGridNavigation(holder: RowPresenter.ViewHolder?) {
        holder?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                keyCode == KeyEvent.KEYCODE_DPAD_RIGHT &&
                event.action == KeyEvent.ACTION_UP
            ) {
//                horizontalGridView?.selectedPosition?.let {
//                    focusListener.selectedColumn = it
//                }
            }
            false
        }
        listRowView?.run {
            isFocusable = true
            descendantFocusability = FOCUS_BEFORE_DESCENDANTS
            setOnFocusChangeListener { _, hasFocus ->
                horizontalGridView?.run {
                    if (hasFocus) {
                        stopScroll()
                        Presenter.cancelAnimationsRecursive(this)

                        // On changing the query in search page the focus should reset to first tile
//                        if (SearchDboardFragment.queryChanged) {
//                            focusListener.selectedColumn = 0
//                            SearchDboardFragment.queryChanged = false
//                        }
//                        horizontalGridView?.adapter?.itemCount?.let { itemCount ->
//                            selectedPosition = if (focusListener.selectedColumn < itemCount) {
//                                focusListener.selectedColumn
//                            } else {
//                                itemCount - 1
//                            }
//                        }
                        requestFocus()
                    }
                }
            }
        }
    }
}
