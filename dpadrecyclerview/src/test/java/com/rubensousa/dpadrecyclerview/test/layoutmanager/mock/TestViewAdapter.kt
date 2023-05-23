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

package com.rubensousa.dpadrecyclerview.test.layoutmanager.mock

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TestViewAdapter(
    private val viewWidth: Int,
    private val viewHeight: Int,
    private val numberOfItems: Int = 100,
) {

    fun getItemCount() = numberOfItems

    fun getViewAt(position: Int): View? {
        if (position < 0) {
            return null
        }
        if (position >= numberOfItems) {
            return null
        }
        val viewMock = ViewMock(viewWidth, viewHeight)
        viewMock.layoutPosition = position
        return viewMock.get()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
