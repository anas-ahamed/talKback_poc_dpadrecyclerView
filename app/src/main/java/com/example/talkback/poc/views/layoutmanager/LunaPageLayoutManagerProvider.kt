package com.example.talkback.poc.views.layoutmanager

import androidx.recyclerview.widget.StaggeredGridLayoutManager

interface LunaPageLayoutManagerProvider {
    fun provideStaggeredGridLayoutManager(
        spanCount: Int,
        orientation: Int
    ): LunaPageRecyclerLayoutManager
}

open class LunaPageRecyclerLayoutManager(spanCount: Int, orientation: Int) :
    StaggeredGridLayoutManager(spanCount, orientation) {
    var isVerticalScrollEnabled = true

    override fun canScrollVertically(): Boolean {
        return isVerticalScrollEnabled
    }
}
