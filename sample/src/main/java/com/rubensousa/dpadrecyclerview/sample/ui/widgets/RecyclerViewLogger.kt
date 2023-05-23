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

package com.rubensousa.dpadrecyclerview.sample.ui.widgets

import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

object RecyclerViewLogger {

    fun logChildrenWhenIdle(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    logChildren(recyclerView)
                }
            }
        })
    }

    fun logChildren(recyclerView: RecyclerView) {
        recyclerView.layoutManager?.let { layout ->
            Timber.i("Children laid out: ${layout.childCount}:")
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)!!
                val position = layout.getPosition(child)
                val left = layout.getDecoratedLeft(child)
                val top = layout.getDecoratedTop(child)
                val right = layout.getDecoratedLeft(child)
                val bottom = layout.getDecoratedBottom(child)
                Timber.i("View $position: [$left, $top, $right, $bottom]")
            }
        }

    }

}