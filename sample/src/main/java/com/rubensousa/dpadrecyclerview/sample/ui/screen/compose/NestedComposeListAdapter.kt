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

package com.rubensousa.dpadrecyclerview.sample.ui.screen.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rubensousa.dpadrecyclerview.UnboundViewPool
import com.rubensousa.dpadrecyclerview.sample.databinding.AdapterListComposeBinding
import com.rubensousa.dpadrecyclerview.sample.ui.model.ItemModel
import com.rubensousa.dpadrecyclerview.sample.ui.model.ListModel
import com.rubensousa.dpadrecyclerview.sample.ui.model.ListTypes
import com.rubensousa.dpadrecyclerview.sample.ui.widgets.common.MutableListAdapter
import com.rubensousa.dpadrecyclerview.sample.ui.widgets.list.DpadStateHolder

class NestedComposeListAdapter(
    private val stateHolder: DpadStateHolder
) : MutableListAdapter<ListModel, NestedComposeListViewHolder>(ItemModel.buildDiffCallback()) {

    private val viewPool = UnboundViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedComposeListViewHolder {
        return NestedComposeListViewHolder(
            AdapterListComposeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            viewPool
        )
    }

    override fun getItemViewType(position: Int): Int {
        return ListTypes.LIST_START
    }

    override fun onBindViewHolder(holder: NestedComposeListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        stateHolder.restore(holder.getRecyclerView(), item.title, holder.getAdapter())
    }

    override fun onViewRecycled(holder: NestedComposeListViewHolder) {
        val item = holder.item ?: return
        holder.cancelAnimations()
        stateHolder.save(holder.getRecyclerView(), item.title)
    }

}
