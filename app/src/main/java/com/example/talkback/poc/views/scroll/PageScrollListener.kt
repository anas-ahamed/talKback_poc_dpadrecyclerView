package com.example.talkback.poc.views.scroll

import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow

interface PageScrollListener {

    val edgeReachedObservable: LiveData<PageEdge>
    val scrollObservable: LiveData<ScrolledData>
    var isScrolling: Boolean
    var enabled: Boolean
    var totalScrollY: Int

    fun addListener(recyclerView: RecyclerView?)
    fun removeListener(recyclerView: RecyclerView?)
    fun resetPosition(positionY: Int)

    val pageScrollStoppedObservable: Flow<Boolean>
    val visibleViewDataObservable: Flow<VisibleViewData>
    fun findAndEmitVisibleViews(recyclerView: RecyclerView, parentRect: Rect? = null, childRect: Rect? = null)
}
