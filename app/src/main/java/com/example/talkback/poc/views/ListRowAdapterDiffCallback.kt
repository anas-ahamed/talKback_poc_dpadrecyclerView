package com.example.talkback.poc.views

import androidx.leanback.widget.DiffCallback
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ObjectAdapter

internal val listRowAdapterDiffCallback = object : DiffCallback<ListRow>() {
    override fun areItemsTheSame(oldItem: ListRow, newItem: ListRow) = checkItems(oldItem, newItem)

    override fun areContentsTheSame(oldItem: ListRow, newItem: ListRow) = checkItems(oldItem, newItem)

    private fun checkItems(oldItem: ListRow, newItem: ListRow) =
        newItem.adapter.size() == oldItem.adapter.size() && adapterItemsTheSame(newItem.adapter, oldItem.adapter)

    private fun adapterItemsTheSame(adapter1: ObjectAdapter, adapter2: ObjectAdapter): Boolean {
        for (i in 0 until adapter1.size()) {
            if (adapter1[i] != adapter2[i]) return false
        }
        return true
    }
}
