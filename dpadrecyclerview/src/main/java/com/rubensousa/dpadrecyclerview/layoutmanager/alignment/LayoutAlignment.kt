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

package com.rubensousa.dpadrecyclerview.layoutmanager.alignment

import android.view.View
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.rubensousa.dpadrecyclerview.ChildAlignment
import com.rubensousa.dpadrecyclerview.DpadViewHolder
import com.rubensousa.dpadrecyclerview.ParentAlignment
import com.rubensousa.dpadrecyclerview.layoutmanager.DpadLayoutParams
import com.rubensousa.dpadrecyclerview.layoutmanager.layout.LayoutInfo
import kotlin.math.sign

internal class LayoutAlignment(
    private val layoutManager: LayoutManager,
    private val layoutInfo: LayoutInfo
) {

    companion object {
        const val TAG = "LayoutAlignment"
    }

    private var parentAlignment = ParentAlignment()
    private val parentAlignmentCalculator = ParentAlignmentCalculator()
    private val childAlignment = ChildScrollAlignment()
    private val viewHolderAlignment = SubPositionScrollAlignment()

    // Also saved here so that they're kept for the same layout pass
    private var isVertical: Boolean = true
    private var reverseLayout: Boolean = false

    fun setLayoutProperties(isVertical: Boolean, reverseLayout: Boolean) {
        this.isVertical = isVertical
        this.reverseLayout = reverseLayout
        parentAlignmentCalculator.updateLayoutInfo(layoutManager, isVertical, reverseLayout)
    }

    fun setParentAlignment(alignment: ParentAlignment) {
        parentAlignment = alignment
    }

    fun getParentAlignment(): ParentAlignment = parentAlignment

    fun setChildAlignment(config: ChildAlignment) {
        childAlignment.setAlignment(config)
    }

    fun getChildAlignment() = childAlignment.getAlignment()

    fun getChildStart(view: View): Int {
        updateChildAlignments(view)
        val layoutParams = view.layoutParams as DpadLayoutParams
        val anchor = layoutParams.alignmentAnchor
        return getParentKeyline() - anchor
    }

    fun getParentKeyline(): Int {
        return parentAlignmentCalculator.calculateKeyline(parentAlignment)
    }

    fun getViewAtSubPosition(view: View, subPosition: Int): View? {
        val viewHolder = layoutInfo.getChildViewHolder(view)
        val childAlignments =
            (viewHolder as? DpadViewHolder)?.getSubPositionAlignments() ?: return null
        if (subPosition >= childAlignments.size) {
            return null
        }
        val subPositionViewId = childAlignments[subPosition].getFocusViewId()
        return view.findViewById(subPositionViewId)
    }

    fun getSubPositionOfView(view: View?, childView: View?): Int {
        if (view == null || childView == null) {
            return 0
        }
        val viewHolder = layoutInfo.getChildViewHolder(view)
        if (viewHolder !is DpadViewHolder) {
            return 0
        }
        val alignments = viewHolder.getSubPositionAlignments()
        if (alignments.isEmpty()) {
            return 0
        }
        var currentChildView = childView
        while (currentChildView !== view && currentChildView != null) {
            if (currentChildView.id != View.NO_ID) {
                alignments.forEachIndexed { index, alignment ->
                    val id = currentChildView?.id
                    if (id != null && id != View.NO_ID) {
                        if (alignment.getFocusViewId() == id) {
                            return index
                        }
                    }
                }
            }
            currentChildView = currentChildView.parent as? View?
        }
        return 0
    }

    fun getCappedScroll(scrollOffset: Int): Int {
        val endScrollLimit = parentAlignmentCalculator.endScrollLimit
        val startScrollLimit = parentAlignmentCalculator.startScrollLimit
        return if (scrollOffset > 0) {
            if (parentAlignmentCalculator.isScrollLimitInvalid(endScrollLimit)) {
                scrollOffset
            } else if (scrollOffset.sign != endScrollLimit.sign) {
                0
            } else if (scrollOffset > endScrollLimit) {
                endScrollLimit
            } else {
                scrollOffset
            }
        } else if (scrollOffset.sign != startScrollLimit.sign) {
            0
        } else if (parentAlignmentCalculator.isScrollLimitInvalid(startScrollLimit)) {
            scrollOffset
        } else if (scrollOffset < startScrollLimit) {
            startScrollLimit
        } else {
            scrollOffset
        }
    }

    fun calculateScrollForAlignment(view: View): Int {
        updateChildAlignments(view)
        updateScrollLimits()
        return calculateScrollToTarget(view)
    }

    fun calculateScrollOffset(view: View, subPosition: Int): Int {
        val viewAtSubPosition = getViewAtSubPosition(view, subPosition)
        return calculateScrollOffset(view, viewAtSubPosition)
    }

    fun calculateScrollOffset(view: View, childView: View?): Int {
        var scrollOffset = calculateScrollForAlignment(view)
        if (childView != null) {
            scrollOffset = calculateAdjustedAlignedScrollDistance(scrollOffset, view, childView)
        }
        return scrollOffset
    }

    private fun updateChildAlignments(view: View) {
        val layoutParams = view.layoutParams as DpadLayoutParams
        val viewHolder = layoutInfo.getChildViewHolder(view) ?: return
        val alignments = if (viewHolder is DpadViewHolder) {
            viewHolder.getSubPositionAlignments()
        } else {
            null
        }
        if (alignments.isNullOrEmpty()) {
            // Use the default child alignment strategy
            // if this ViewHolder didn't request a custom alignment strategy
            childAlignment.updateAlignments(
                view,
                layoutParams,
                isVertical,
                reverseLayout
            )
        } else {
            viewHolderAlignment.updateAlignments(
                view,
                layoutParams,
                alignments,
                isVertical,
                reverseLayout
            )
        }
    }

    fun updateScrollLimits() {
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return
        }
        val endAdapterPos: Int
        val startAdapterPos: Int
        val endLayoutPos: Int
        val startLayoutPos: Int
        if (!reverseLayout) {
            endAdapterPos = layoutInfo.findLastAddedPosition()
            endLayoutPos = itemCount - 1
            startAdapterPos = layoutInfo.findFirstAddedPosition()
            startLayoutPos = 0
        } else {
            endAdapterPos = layoutInfo.findFirstAddedPosition()
            endLayoutPos = 0
            startAdapterPos = layoutInfo.findLastAddedPosition()
            startLayoutPos = itemCount - 1
        }
        if (endAdapterPos < 0 || startAdapterPos < 0) {
            parentAlignmentCalculator.invalidateScrollLimits()
            return
        }
        val isEndAvailable = isEndAvailable(endAdapterPos, endLayoutPos, startLayoutPos)
        val isStartAvailable = isStartAvailable(startAdapterPos, endLayoutPos, startLayoutPos)
        if (!isEndAvailable && parentAlignmentCalculator.isEndUnknown
            && !isStartAvailable && parentAlignmentCalculator.isStartUnknown
        ) {
            return
        }
        val endEdge: Int
        var endViewAnchor = 0
        if (isEndAvailable) {
            endEdge = getEndEdge(endAdapterPos) ?: Int.MAX_VALUE
            layoutManager.findViewByPosition(endAdapterPos)?.let { maxChild ->
                endViewAnchor = getAnchor(maxChild)
                val layoutParams = maxChild.layoutParams as DpadLayoutParams
                val multipleAlignments = layoutParams.getSubPositionAnchors()
                if (multipleAlignments != null && multipleAlignments.isNotEmpty()) {
                    endViewAnchor += multipleAlignments.last() - multipleAlignments.first()
                }
            }
        } else {
            endEdge = Int.MAX_VALUE
            endViewAnchor = Int.MAX_VALUE
        }
        val startEdge: Int
        var startViewAnchor = 0
        if (isStartAvailable) {
            startEdge = getStartEdge(startAdapterPos) ?: Int.MIN_VALUE
            layoutManager.findViewByPosition(startAdapterPos)?.let { minChild ->
                startViewAnchor = getAnchor(minChild)
            }
        } else {
            startEdge = Int.MIN_VALUE
            startViewAnchor = Int.MIN_VALUE
        }
        if (!reverseLayout) {
            parentAlignmentCalculator.updateEndLimit(endEdge, endViewAnchor, parentAlignment)
            parentAlignmentCalculator.updateStartLimit(startEdge, startViewAnchor, parentAlignment)
        } else {
            parentAlignmentCalculator.updateStartLimit(endEdge, endViewAnchor, parentAlignment)
            parentAlignmentCalculator.updateEndLimit(startEdge, startViewAnchor, parentAlignment)
        }
    }

    private fun isEndAvailable(
        adapterPosition: Int,
        maxLayoutPosition: Int,
        minLayoutPosition: Int
    ): Boolean {
        return if (!reverseLayout) {
            adapterPosition == maxLayoutPosition
        } else {
            adapterPosition == minLayoutPosition
        }
    }

    private fun isStartAvailable(
        adapterPosition: Int,
        maxLayoutPosition: Int,
        minLayoutPosition: Int
    ): Boolean {
        return if (!reverseLayout) {
            adapterPosition == minLayoutPosition
        } else {
            adapterPosition == maxLayoutPosition
        }
    }

    private fun getEndEdge(index: Int): Int? {
        val view = layoutManager.findViewByPosition(index) ?: return null
        return if (!reverseLayout) {
            layoutInfo.getDecoratedEnd(view)
        } else {
            layoutInfo.getDecoratedStart(view)
        }
    }

    private fun getStartEdge(index: Int): Int? {
        val view = layoutManager.findViewByPosition(index) ?: return null
        return if (!reverseLayout) {
            layoutInfo.getDecoratedStart(view)
        } else {
            layoutInfo.getDecoratedEnd(view)
        }
    }

    private fun getAnchor(view: View): Int {
        return if (isVertical) {
            getVerticalAnchor(view)
        } else {
            getHorizontalAnchor(view)
        }
    }

    private fun getHorizontalAnchor(view: View): Int {
        val layoutParams = view.layoutParams as DpadLayoutParams
        return view.left + layoutParams.alignmentAnchor
    }

    private fun getVerticalAnchor(view: View): Int {
        val layoutParams = view.layoutParams as DpadLayoutParams
        return view.top + layoutParams.alignmentAnchor
    }

    /**
     * Return the scroll delta required to make the view selected and aligned.
     * If the returned value is 0, there is no need to scroll.
     */
    private fun calculateScrollToTarget(view: View): Int {
        return parentAlignmentCalculator.calculateScrollOffset(getAnchor(view), parentAlignment)
    }

    private fun calculateAdjustedAlignedScrollDistance(
        offset: Int,
        view: View,
        childView: View
    ): Int {
        var scrollValue = offset
        val subPosition = getSubPositionOfView(view, childView)
        if (subPosition != 0) {
            val layoutParams = view.layoutParams as DpadLayoutParams
            val alignments = layoutParams.getSubPositionAnchors()
            if (alignments != null && alignments.isNotEmpty()) {
                scrollValue += alignments[subPosition] - alignments[0]
            }
        }
        return scrollValue
    }

}
