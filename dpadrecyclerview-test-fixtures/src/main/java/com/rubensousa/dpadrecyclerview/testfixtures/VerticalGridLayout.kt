package com.rubensousa.dpadrecyclerview.testfixtures

import com.rubensousa.dpadrecyclerview.DpadSpanSizeLookup
import com.rubensousa.dpadrecyclerview.layoutmanager.layout.ViewBounds
import kotlin.math.abs
import kotlin.math.max

class VerticalGridLayout(
    config: LayoutConfig,
    val spanCount: Int,
    val isRTL: Boolean = false
) : LayoutMatrix(config) {

    private var spanSizeLookup : DpadSpanSizeLookup = object : DpadSpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = 1
        override fun getSpanIndex(position: Int, spanCount: Int): Int {
            return position % spanCount
        }
    }
    private var layoutRow = GridRow(numberOfSpans = spanCount, width = config.parentWidth)
    private var pivotRow = GridRow(layoutRow)
    private val rowViews = Array<ViewItem?>(spanCount) { null }
    private val viewBounds = ViewBounds()

    fun setSpanSizeLookup(lookup: DpadSpanSizeLookup) {
        spanSizeLookup = lookup
    }

    override fun isVertical(): Boolean = true

    override fun scrollBy(offset: Int) {
        val scrollDistance = abs(offset)
        if (offset < 0) {
            val firstView = getFirstView() ?: return
            layoutRequest.prepend(firstView.position) {
                checkpoint = getDecoratedStart(firstView)
                val availableScrollSpace = max(0, -checkpoint)
                space = max(0, scrollDistance + getExtraLayoutSpaceStart() - availableScrollSpace)
            }
            fill(layoutRequest)
        } else {
            val lastView = getLastView() ?: return
            layoutRequest.append(lastView.position) {
                checkpoint = getDecoratedEnd(lastView)
                val availableScrollSpace = max(0, checkpoint - getSize())
                space = max(0, scrollDistance + getExtraLayoutSpaceEnd() - availableScrollSpace)
            }
            fill(layoutRequest)
        }
        offsetChildren(-offset)
        layoutRow.offsetBy(-offset)

        if (offset < 0) {
            recycleEnd()
        } else {
            recycleStart()
        }
    }

    fun scrollDown() {
        if (selectedPosition + spanCount >= getItemCount()) {
            return
        }
        updateSelectedPosition(selectedPosition + spanCount)
        scrollBy(config.viewHeight)
    }

    fun scrollUp() {
        if (selectedPosition < spanCount) {
            return
        }
        updateSelectedPosition(selectedPosition - spanCount)
        scrollBy(-config.viewHeight)
    }

    override fun initializeLayout(pivotPosition: Int) {
        // Reset row states
        layoutRow.reset(config.parentKeyline)
        pivotRow.reset(config.parentKeyline)

        // Start by rendering the row of the pivot
        layoutPivotRow(pivotPosition)
        pivotRow.copy(layoutRow)

        // Layout from the pivot's row until the start limit of the matrix
        layoutFromPivotToStart()

        // Layout from the pivot's row until the end limit of the matrix
        layoutFromPivotToEnd()
    }

    private fun layoutPivotRow(pivotPosition: Int) {
        val pivotSpanIndex = spanSizeLookup.getSpanIndex(pivotPosition, spanCount)
        val firstSpanPosition = max(0, pivotPosition - pivotSpanIndex)
        layoutRequest.append(firstSpanPosition) {
            position = firstSpanPosition
            checkpoint = config.parentKeyline
            space = 1
        }
        fill(layoutRequest)

        val pivotView = findViewFromLayoutPosition(pivotPosition)!!

        // Align the pivot using the alignment configuration
        val scrollOffset = getViewCenter(pivotView) - config.parentKeyline
        offsetChildren(-scrollOffset)
        layoutRow.offsetBy(-scrollOffset)
    }

    private fun layoutFromPivotToStart() {
        layoutRequest.prepend(pivotRow.getFirstPosition()) {
            checkpoint = pivotRow.startOffset
            space = checkpoint
        }
        fill(layoutRequest)
    }

    private fun layoutFromPivotToEnd() {
        layoutRequest.append(pivotRow.getLastPosition()) {
            checkpoint = pivotRow.endOffset
            space = max(0, getVisibleSpace() - checkpoint)
        }
        fill(layoutRequest)
    }

    override fun layoutExtraStart() {

    }

    override fun layoutExtraEnd() {

    }

    /**
     * This method will layout an entire row.
     * If the next item doesn't fit in the existing row, then we return early.
     *
     * Setup step:
     * - Calculate remaining spans for filling the current row
     * - Retrieve the views that will be used to fill the current row
     * - Assign a span index to each view
     *
     * Layout step:
     * - Loop through each view found in setup phase and add it
     * - Calculate the ItemDecorations and measure each child
     * - Keep track of the total row height and width
     * - Re-measure all views that didn't have the same height as the row
     * - Layout each view with decorations included
     */
    override fun layoutBlock(request: LayoutBlockRequest): LayoutBlockResult {
        val row = updateRow(request)
        val remainingSpans = calculateRemainingSpans(request)
        val viewCount = getViewsForRow(request, remainingSpans)
        assignSpans(viewCount, request)

        val rowHeight = fillRow(viewCount, row, request)
        reMeasureChildren(row, viewCount)
        layoutRow(viewCount, row, request)

        return LayoutBlockResult(rowViews.filterNotNull(), rowHeight, skipConsumption = false)
    }

    private fun updateRow(request: LayoutBlockRequest): GridRow {
        return if (request.isTowardsEnd()) {
            if (layoutRow.isEndComplete()) {
                layoutRow.moveToNext()
            }
            layoutRow
        } else {
            if (layoutRow.isStartComplete()) {
                layoutRow.moveToPrevious()
            }
            layoutRow
        }
    }

    /**
     * Add views from [rowViews] to the layout and keeps track of the row size
     */
    private fun fillRow(
        viewCount: Int,
        row: GridRow,
        request: LayoutBlockRequest
    ): Int {
        repeat(viewCount) { index ->
            val view = getRowViewAt(index)
            if (request.isTowardsEnd() && !row.fitsEnd(view.spanSize)) {
                return row.height
            }
            if (request.isTowardsStart() && !row.fitsStart(view.spanSize)) {
                return row.height
            }

            if (request.isTowardsEnd()) {
                row.append(getChildSize(), view.position, view.spanSize)
                append(view)
            } else {
                row.prepend(getChildSize(), view.position, view.spanSize)
                prepend(view)
            }
        }
        return row.height
    }


    private fun assignSpans(viewCount: Int, request: LayoutBlockRequest) {
        var span = 0
        var start = 0
        var end = 0
        var increment = 0
        if (request.currentItemDirection > 0) {
            start = 0
            end = viewCount
            increment = 1
        } else {
            start = viewCount - 1
            end = -1
            increment = -1
        }
        span = 0
        var i = start
        while (i != end) {
            val view = getRowViewAt(i)
            view.updateSpan(
                index = spanSizeLookup.getSpanIndex(view.position, spanCount),
                size = spanSizeLookup.getSpanSize(view.position)
            )
            span += view.spanSize
            i += increment
        }
    }

    private fun layoutRow(viewCount: Int, row: GridRow, request: LayoutBlockRequest) {
        updateLayoutBounds(request, row, viewBounds)

        for (i in 0 until viewCount) {
            val view = getRowViewAt(i)
            val perpendicularSize = getPerpendicularChildSize()
            if (isVertical()) {
                if (isRTL) {
                    viewBounds.right = row.getSpanBorder(row.numberOfSpans - view.spanIndex)
                    viewBounds.left = viewBounds.right - perpendicularSize
                } else {
                    viewBounds.left = row.getSpanBorder(view.spanIndex)
                    viewBounds.right = viewBounds.left + perpendicularSize
                }
            } else {
                viewBounds.top = row.getSpanBorder(view.spanIndex)
                viewBounds.bottom = viewBounds.top + perpendicularSize
            }

            view.layout(viewBounds)
        }
        viewBounds.setEmpty()
        // Clear view references since we no longer need them
        rowViews.fill(null)
    }

    private fun getViewsForRow(request: LayoutBlockRequest, spansToFill: Int): Int {
        var remainingSpans = spansToFill
        var viewCount = 0
        while (isRowIncomplete(viewCount, request, remainingSpans)) {
            val position = request.position
            val spanSize = spanSizeLookup.getSpanSize(position)
            if (spanSize > layoutRow.numberOfSpans) {
                throw IllegalArgumentException(
                    "Item at position $position requires $spanSize, " +
                            "but spanCount is ${layoutRow.numberOfSpans}"
                )
            }
            remainingSpans -= spanSize
            // If the current item doesn't fit into the current row, just exit
            if (remainingSpans < 0) {
                break
            }
            if (position == getItemCount()) {
                break
            }
            request.position += request.currentItemDirection
            rowViews[viewCount] = ViewItem(position, ViewBounds(), config.decorInsets)
            viewCount++
        }
        return viewCount
    }

    /**
     * Views that don't have the height of the row need to be re-measured
     */
    private fun reMeasureChildren(row: GridRow, viewCount: Int) {
        val rowHeight = row.height
        for (i in 0 until viewCount) {
            val view = getRowViewAt(i)
            if (view.getDecoratedHeight() != rowHeight) {
                if (isVertical()) {
                    view.bounds.bottom = view.bounds.top + rowHeight
                } else if (isRTL) {
                    view.bounds.left = view.bounds.right - rowHeight
                } else {
                    view.bounds.right = view.bounds.left + rowHeight
                }
            }
        }
    }

    private fun isRowIncomplete(
        viewCount: Int,
        layoutRequest: LayoutBlockRequest,
        remainingSpans: Int
    ): Boolean {
        return viewCount < layoutRow.numberOfSpans
                && layoutRequest.position < getItemCount() && layoutRequest.position >= 0
                && remainingSpans > 0
    }

    private fun calculateRemainingSpans(request: LayoutBlockRequest): Int {
        return if (request.isTowardsEnd()) {
            layoutRow.getAvailableAppendSpans()
        } else {
            layoutRow.getAvailablePrependSpans()
        }
    }

    private fun updateLayoutBounds(request: LayoutBlockRequest, row: GridRow, bounds: ViewBounds) {
        val rowHeight = row.height
        if (isVertical()) {
            if (request.isTowardsStart()) {
                bounds.bottom = request.checkpoint
                bounds.top = bounds.bottom - rowHeight
            } else {
                bounds.top = request.checkpoint
                bounds.bottom = bounds.top + rowHeight
            }
        } else {
            if (request.isTowardsStart()) {
                bounds.right = request.checkpoint
                bounds.left = bounds.right - rowHeight
            } else {
                bounds.left = request.checkpoint
                bounds.right = bounds.left + rowHeight
            }
        }
    }

    private fun getRowViewAt(index: Int): ViewItem {
        return rowViews[index]!!
    }

    private fun getChildSize(): Int {
        return if (isVertical()) {
            config.viewHeight + config.decorInsets.top + config.decorInsets.bottom
        } else {
            config.viewWidth + config.decorInsets.left + config.decorInsets.right
        }
    }

    private fun getPerpendicularChildSize(): Int {
        return if (isVertical()) {
            config.viewWidth + config.decorInsets.left + config.decorInsets.right
        } else {
            config.viewHeight + config.decorInsets.top + config.decorInsets.bottom
        }
    }

    override fun getLayoutStartOffset(): Int {
        val firstView = getFirstView() ?: return 0
        return getDecoratedStart(firstView)
    }

    override fun getLayoutEndOffset(): Int {
        val lastView = getLastView() ?: return 0
        return getDecoratedEnd(lastView)
    }

    override fun getViewCenter(view: ViewItem): Int {
        return view.getDecoratedTop() + (view.getDecoratedHeight() * config.childKeyline).toInt()
    }

}
