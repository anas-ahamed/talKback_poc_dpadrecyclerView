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

package com.rubensousa.dpadrecyclerview.sample.ui.screen.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.dpadrecyclerview.sample.R
import com.rubensousa.dpadrecyclerview.sample.databinding.HorizontalAdapterAnimatedItemBinding
import com.rubensousa.dpadrecyclerview.sample.databinding.ScreenLeanbackHorizontalBinding
import com.rubensousa.dpadrecyclerview.sample.ui.dpToPx
import com.rubensousa.dpadrecyclerview.sample.ui.widgets.RecyclerViewLogger
import com.rubensousa.dpadrecyclerview.sample.ui.widgets.item.ItemViewHolder
import com.rubensousa.dpadrecyclerview.spacing.DpadLinearSpacingDecoration

class SearchPivotFragment : Fragment(R.layout.screen_leanback_horizontal) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ScreenLeanbackHorizontalBinding.bind(view)
        binding.horizontalGridView.clipToPadding = false
        binding.horizontalGridView.updatePadding(left = dpToPx(24.dp))
        binding.horizontalGridView.setItemSpacing(dpToPx(24.dp))
        binding.horizontalGridView.adapter = Adapter()
        RecyclerViewLogger.logChildrenWhenIdle(binding.horizontalGridView)
        RecyclerViewLogger.logChildrenWhenIdle(binding.dpadRecyclerView)
        binding.dpadRecyclerView.addItemDecoration(
            DpadLinearSpacingDecoration.create(dpToPx(24.dp))
        )
        binding.dpadRecyclerView.adapter = Adapter()
    }

    class Adapter : RecyclerView.Adapter<ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val binding = HorizontalAdapterAnimatedItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ItemViewHolder(
                binding.root, binding.textView, animateFocusChanges = false
            )
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(position, null)
            holder.itemView.isFocusable = position % 10 == 0
            holder.itemView.isFocusableInTouchMode = position % 10 == 0
        }

        override fun getItemCount(): Int = 100

    }


}
