package com.example.talkback.poc.views.scroll

import androidx.recyclerview.widget.RecyclerView.NO_POSITION

data class ScrolledData(
    val yOffset: Int,
    val firstPartiallyVisible: Int = NO_POSITION,
    val firstFullyVisible: Int = NO_POSITION
)
