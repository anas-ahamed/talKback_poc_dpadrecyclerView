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

package com.rubensousa.dpadrecyclerview.layoutmanager.focus

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rubensousa.dpadrecyclerview.FocusableDirection
import com.rubensousa.dpadrecyclerview.layoutmanager.layout.LayoutInfo

/**
 * Implementation for [FocusableDirection.CIRCULAR]
 * TODO: Add tests
 */
internal class CircularFocusInterceptor(
    private val layoutInfo: LayoutInfo
) : FocusInterceptor {

    override fun findFocus(
        recyclerView: RecyclerView,
        focusedView: View,
        position: Int,
        direction: Int
    ): View? {
        val focusDirection = FocusDirection.from(
            isVertical = layoutInfo.isVertical(),
            reverseLayout = layoutInfo.shouldReverseLayout(),
            direction = direction
        ) ?: return null
        return findFocus(position, focusDirection)
    }

    private fun findFocus(position: Int, direction: FocusDirection): View? {
        if (direction != FocusDirection.PREVIOUS_COLUMN && direction != FocusDirection.NEXT_COLUMN) {
            return null
        }
        val firstColumnIndex = layoutInfo.getStartSpanIndex(position)
        val configuration = layoutInfo.getConfiguration()

        // Do nothing for the items that take the entire span count
        if (configuration.spanSizeLookup.getSpanSize(position) == configuration.spanCount) {
            return null
        }

        val startRow = layoutInfo.getSpanGroupIndex(position)

        var currentPosition = if (direction == FocusDirection.NEXT_COLUMN) {
            position + 1
        } else {
            position - 1
        }

        var currentRow = layoutInfo.getSpanGroupIndex(currentPosition)
        val lastColumnIndex = layoutInfo.getEndSpanIndex(position)

        // If we still have focusable views in the movement direction, bail out
        while (currentRow == startRow && currentPosition >= 0) {
            val currentView = layoutInfo.findViewByPosition(currentPosition)
            if (currentView != null && layoutInfo.isViewFocusable(currentView)) {
                return null
            }
            if (direction == FocusDirection.NEXT_COLUMN) {
                currentPosition++
            } else {
                currentPosition--
            }
            currentRow = layoutInfo.getSpanGroupIndex(currentPosition)
        }

        var circularPosition: Int
        if (direction == FocusDirection.NEXT_COLUMN) {
            circularPosition = position - configuration.spanCount + 1
            while (circularPosition <= position - 1) {
                currentRow = layoutInfo.getSpanGroupIndex(circularPosition)
                val currentColumn = layoutInfo.getStartSpanIndex(circularPosition)
                val view = layoutInfo.findViewByPosition(circularPosition)
                if (currentRow == startRow
                    && currentColumn != firstColumnIndex
                    && view != null
                    && layoutInfo.isViewFocusable(view)
                ) {
                    break
                }
                circularPosition++
            }
        } else {
            circularPosition = position + configuration.spanCount - 1
            while (circularPosition >= position + 1) {
                val lastColumn = layoutInfo.getEndSpanIndex(circularPosition)
                currentRow = layoutInfo.getSpanGroupIndex(circularPosition)
                val view = layoutInfo.findViewByPosition(circularPosition)
                if (currentRow == startRow
                    && lastColumn != lastColumnIndex
                    && view != null
                    && layoutInfo.isViewFocusable(view)
                ) {
                    break
                }
                circularPosition--
            }
        }
        val view = layoutInfo.findViewByPosition(circularPosition)
        if (view != null && !layoutInfo.isViewFocusable(view)) {
            return null
        }
        return view
    }

}
