package com.example.talkback.poc.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.updateMargins
import androidx.leanback.widget.*
import com.example.talkback.poc.R
import com.example.talkback.poc.models.Card
import com.example.talkback.poc.models.ComponentRenderer
import com.example.talkback.poc.presenter.ContentGridListRowPresenter
import com.example.talkback.poc.presenter.ContentGridRowPresenter
import com.example.talkback.poc.presenter.CustomListRowHeaderPresenter
import com.example.talkback.poc.ui.globalLayoutListener
import com.example.talkback.poc.ui.isAccessibilityEnabled

class ContentGridRowBinder(val context: Context, val view: View, val parent: ViewGroup) {

    private val rowPresenter = ContentGridListRowPresenter(
        useGridNavigation = false,
        horizontalSpacing = context.resources.getDimensionPixelSize(R.dimen.poster_primary_card_horizontal_spacing),
        railWindowAlignmentOffset = context.resources.getDimensionPixelSize(R.dimen.card_poster_alignment_offset)
    ).apply {
        shadowEnabled = false
        headerPresenter = CustomListRowHeaderPresenter()
    }

    private val itemAdapter = ArrayObjectAdapter(rowPresenter)
    private val bridgeAdapter = ItemBridgeAdapter(itemAdapter)
    private var itemPresenter: ContentGridRowPresenter? = null
    private var arrayObjectAdapter: ArrayObjectAdapter? = null
    private lateinit var gridView: VerticalGridView
    private var previousView: View? = null

    fun bind(componentRenderer: ComponentRenderer) {
        gridView = view.findViewById(R.id.rowRecycler)
        gridView.adapter = bridgeAdapter
        itemPresenter = ContentGridRowPresenter(context, componentRenderer.id)
        arrayObjectAdapter = ArrayObjectAdapter(itemPresenter)
        arrayObjectAdapter?.setItems(componentRenderer.cards, diffCallback)
        setListRow(
            HeaderItem("Title "+componentRenderer.id),
            arrayObjectAdapter!!,
            context.resources.getDimensionPixelSize(R.dimen.card_poster_height_home_shows)
        )
    }

    private fun setListRow(
        header: HeaderItem,
        adapter: ArrayObjectAdapter,
        itemHeight: Int,
        addHeader: Boolean = true
    ) {
        val listRow = ListRow(header, adapter)
        itemAdapter.setItems(listOf(listRow), listRowAdapterDiffCallback)
        val headerHeight = if (header.name.isEmpty()) 0 else HEADER_HEIGHT
        var totalHeight = itemHeight
        if (addHeader) {
            totalHeight += headerHeight
        }
        if (itemHeight == 0) {
            gridView.run {
                isFocusable = false
                isClickable = false
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            }
            setFocusToNextRail()
        } else {
            gridView.run {
                if(gridView.context.isAccessibilityEnabled()) {
                    isFocusable = false
                    isClickable = false
                    isFocusableInTouchMode = false
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                } else {
                    isFocusable = true
                    isClickable = true
                    isFocusableInTouchMode = true
                }
            }
        }
        println("talkback gridview height "+totalHeight)
        gridView.layoutParams?.height = totalHeight
        (gridView.layoutParams as? FrameLayout.LayoutParams)?.updateMargins(
            bottom = if (totalHeight == 0) 0 else context.resources.getDimensionPixelSize(R.dimen.grid_16)
        )
    }

    private fun setInitialViewFocus(horizontalGridView: HorizontalGridView?) {
        Handler(Looper.getMainLooper()).postDelayed({
            val firstView = (horizontalGridView?.findViewHolderForAdapterPosition(0) as? ItemBridgeAdapter.ViewHolder)?.viewHolder?.view
                firstView?.requestFocus()
                firstView?.performAccessibilityAction(
                    AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
                    null
                )
                firstView?.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
                horizontalGridView?.selectedPosition = 0
                horizontalGridView?.smoothScrollToPosition(0)
                firstView?.id?.let {
                    gridView.nextFocusDownId = it
                    horizontalGridView.nextFocusDownId = it
                    println("talkback firstView id " + it)
                }
            println("talkback firstView "+firstView)
        }, 500L)
    }

    /**
     * sometimes next rail after the empty rail will not get focussed hence setting next selection
     */
    private fun setFocusToNextRail() {
        val pageRecycler = view.parent as? VerticalGridView
        val focussedChildPosition = pageRecycler?.selectedPosition ?: 0
        if ((pageRecycler?.hasFocus() == false) &&
            (focussedChildPosition > FOCUS_NEXT_INDEX) &&
            (focussedChildPosition < (pageRecycler.adapter?.itemCount ?: 0))
        ) {
            pageRecycler.selectedPosition += FOCUS_NEXT_INDEX
        }
    }

    private val diffCallback = object : DiffCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }
    }

    companion object {
        const val HEADER_HEIGHT = 60
        private const val FOCUS_NEXT_INDEX = 1
    }
}