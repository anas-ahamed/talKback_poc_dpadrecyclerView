package com.example.talkback.poc.models

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

interface ItemModel {

    companion object {
        fun <T : ItemModel> buildDiffCallback(): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return oldItem.diffId == newItem.diffId
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
    val diffId: String
}