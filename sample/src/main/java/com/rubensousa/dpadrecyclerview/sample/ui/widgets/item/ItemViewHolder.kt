/*
 * Copyright 2022 Rúben Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rubensousa.dpadrecyclerview.sample.ui.widgets.item

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.dpadrecyclerview.DpadViewHolder
import com.rubensousa.dpadrecyclerview.sample.ui.widgets.common.ItemAnimator

class ItemViewHolder(
    root: View,
    private val textView: TextView,
    private val animateFocusChanges: Boolean = true
) : RecyclerView.ViewHolder(root), DpadViewHolder {

    private var clickListener: ItemClickListener? = null
    private val animator = ItemAnimator(root)

    init {
        itemView.setOnClickListener {
            clickListener?.onViewHolderClicked()
        }
        root.setOnFocusChangeListener { _, hasFocus ->
            if (!animateFocusChanges) {
                return@setOnFocusChangeListener
            }
            if (hasFocus) {
                animator.startFocusGainAnimation()
            } else {
                animator.startFocusLossAnimation()
            }
        }
    }

    fun bind(item: Int, listener: ItemClickListener?) {
        textView.text = item.toString()
        clickListener = listener
    }

    fun recycle() {
        animator.cancel()
        clickListener = null
    }

    interface ItemClickListener {
        fun onViewHolderClicked()
    }

}
