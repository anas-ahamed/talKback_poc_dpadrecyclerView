package com.example.talkback.poc.views.scroll

import android.graphics.Rect
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.talkback.poc.SingleLiveEvent
import com.example.talkback.poc.ui.DoNothing
import com.example.talkback.poc.ui.findVisibleViews
import kotlinx.coroutines.flow.*

class PageScrollListenerTv : PageScrollListener {

    private val _edgeReachedObservable = SingleLiveEvent<PageEdge>()
    override val edgeReachedObservable: LiveData<PageEdge>
        get() = _edgeReachedObservable

    private val _scrollObservable = MutableLiveData<ScrolledData>()
    override val scrollObservable: LiveData<ScrolledData>
        get() = _scrollObservable
    override var isScrolling: Boolean = false
    override var enabled: Boolean = true
    override var totalScrollY: Int = 0

    private val _visibleViewDataObservable = MutableSharedFlow<VisibleViewData>()
    override val visibleViewDataObservable: Flow<VisibleViewData> get() = _visibleViewDataObservable

    private val _pageScrollStoppedObservable = MutableStateFlow(false)
    override val pageScrollStoppedObservable: StateFlow<Boolean> get() = _pageScrollStoppedObservable

    override fun addListener(recyclerView: RecyclerView?) {
        val verticalGridView = recyclerView as VerticalGridView
        verticalGridView.run {
            setOnChildSelectedListener { _, _, position, _ ->
                val count = verticalGridView.adapter?.itemCount ?: 0
                _edgeReachedObservable.value = when (position) {
                    0 -> PageEdge.Top
                    count - 1 -> PageEdge.Bottom
                    else -> when (position) {
                        count - PENULTIMATE -> PageEdge.BottomPenultimate
                        count - ANTE_PENULTIMATE -> PageEdge.BottomAntePenultimate
                        else -> PageEdge.Unknown
                    }
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    _scrollObservable.value = ScrolledData(dy)
                    _pageScrollStoppedObservable.value = !isScrolling
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
                    super.onScrollStateChanged(recyclerView, newState)
                    _pageScrollStoppedObservable.value = !isScrolling
                }
            })
        }
    }

    override fun removeListener(recyclerView: RecyclerView?) = DoNothing

    override fun resetPosition(positionY: Int) = DoNothing

    override fun findAndEmitVisibleViews(recyclerView: RecyclerView, parentRect: Rect?, childRect: Rect?) {
        val lifecycleOwner = recyclerView.findViewTreeLifecycleOwner()
        recyclerView.findVisibleViews (
            isMainRecyclerView = true,
            parentRect = parentRect,
            childRect = childRect
        ) { visibleViewHolder ->
            lifecycleOwner?.lifecycleScope?.launchWhenStarted {
                _visibleViewDataObservable.emit(
                    VisibleViewData(
                        itemView = visibleViewHolder.itemView,
                        layoutPosition = visibleViewHolder.layoutPosition
                    )
                )
            }
        }
    }

    companion object {
        const val PENULTIMATE = 2 // distance from the position before the last position, from count
        const val ANTE_PENULTIMATE = 3 // distance from the second position before the last position, from count
    }
}
