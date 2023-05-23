/*
 * Copyright 2023 Rúben Sousa
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

package com.example.talkback.poc.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.BaseCardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.talkback.poc.databinding.ItemPosterBinding
import com.example.talkback.poc.databinding.ItemPosterDpadBinding
import com.example.talkback.poc.databinding.MainAdapterItemFeatureBinding
import com.example.talkback.poc.models.Card
import com.example.talkback.poc.ui.setRoundedImage
import com.example.talkback.poc.views.ItemAnimator
import com.example.talkback.poc.views.PosterCardView
import com.rubensousa.dpadrecyclerview.DpadViewHolder

class RowAdapter(private val context: Context) : RecyclerView.Adapter<RowAdapter.ViewHolder>() {

    private var items = emptyList<Card>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPosterDpadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    fun replaceItems(newItems: List<Card>) {
        items = newItems
    }

    class ViewHolder(val view: ItemPosterDpadBinding) : RecyclerView.ViewHolder(view.root), DpadViewHolder {

        private val animator = ItemAnimator(view.root)
        private var item: Card? = null

        init {
            itemView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    animator.startFocusGainAnimation()
                } else {
                    animator.startFocusLossAnimation()
                }
            }
        }

        fun bind(item: Card) {
            this.item = item
//            view.textView.text = "Card "+item.id
            view.cardImage.setRoundedImage(imageUrl = item.imageUrl)
        }

        fun recycle() {
            animator.cancel()
        }

    }

}
