package com.example.talkback.poc.ui

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.example.talkback.poc.R
import com.example.talkback.poc.models.ImageAtomModel
import com.example.talkback.poc.utils.Constants
import com.example.talkback.poc.views.atom.AtomRoundedImage

fun AtomRoundedImage.setRoundedImage(imageUrl: String, contentDescription: String? = "", priority: Priority = Priority.NORMAL) {
    this.bindData(
        ImageAtomModel(
            imageUrl = imageUrl,
            contentDescription = contentDescription,
            cornerRadius = resources.getDimension(R.dimen.cards_corner_radius).toInt(),
            priority = priority
        )
    )
    this.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    this.contentDescription = contentDescription
}

fun View.globalLayoutListener(action: () -> Unit) {
    val globalLayoutListener = object: ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}

fun View.animateOnFocus(
    hasFocus: Boolean,
    focusScaleX: Float = Constants.VIEW_FOCUS_SCALE,
    focusScaleY: Float = Constants.VIEW_FOCUS_SCALE
) {
    if (hasFocus) {
        this.animate().scaleX(focusScaleX).scaleY(focusScaleY)
            .duration = Constants.ANIMATE_FOCUS_DURATION_MS
        if (context.isAccessibilityEnabled()) {
            requestFocus()
            performAccessibilityAction(
                AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
                null
            )
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    } else {
        this.animate().scaleX(1f).scaleY(1f)
            .duration = Constants.ANIMATE_FOCUS_DURATION_MS
    }
}

fun RecyclerView.findVisibleViews(
    isMainRecyclerView: Boolean = false,
    parentRect: Rect? = null, // Added for tests
    childRect: Rect? = null,
    onVisibleItemRect: (visibleViewHolder: RecyclerView.ViewHolder) -> Unit
) {
    val isHorizontalOrientation = (layoutManager as? LinearLayoutManager)?.orientation == LinearLayoutManager.HORIZONTAL
    val childCount = layoutManager?.childCount ?: 0
    repeat(childCount) { pos ->
        getChildAt(pos)?.let { view ->
            getChildViewHolder(view)?.let { viewHolder ->
                val isRectVisible =
                    isViewInForeground(
                        isRecyclerView = isMainRecyclerView,
                        parentViewBounds = parentRect ?: Rect(),
                        itemRect = childRect ?: Rect(),
                        parentView = this,
                        childView = viewHolder.itemView,
                        isHorizontalOrientation = isHorizontalOrientation
                    )
                if (isRectVisible) {
                    onVisibleItemRect(viewHolder)
                }
            }
        }
    }
}

@Suppress("LongParameterList")
fun isViewInForeground(
    isRecyclerView: Boolean,
    parentViewBounds: Rect,
    itemRect: Rect,
    parentView: View,
    childView: View,
    isHorizontalOrientation: Boolean
): Boolean {
    var isViewVisibleRect = false
    parentView.getHitRect(parentViewBounds)
    childView.getLocalVisibleRect(itemRect)

    val visibleWidth = itemRect.width()
    val visibleHeight = itemRect.height()

    val width = childView.measuredWidth
    val height = childView.measuredHeight

    val visibleWidthPercent = visibleWidth.toDouble() / width.toDouble() * MAX_PERCENTAGE
    val visibleHeightPercent = visibleHeight.toDouble() / height.toDouble() * MAX_PERCENTAGE

    if (childView.getGlobalVisibleRect(parentViewBounds)) {
        if (isHorizontalOrientation) {
            if (visibleWidthPercent >= MIN_VISIBLE_PERCENT && visibleHeightPercent >= MIN_VISIBLE_PERCENT) {
                isViewVisibleRect = true
            }
        } else if (visibleHeightPercent >= MIN_VISIBLE_PERCENT) {
            isViewVisibleRect = true
        } else if (isRecyclerView && height > parentViewBounds.height()) {
            isViewVisibleRect = true
        }
    }
    return isViewVisibleRect
}

private const val MIN_VISIBLE_PERCENT = 50
private const val MAX_PERCENTAGE = 100

typealias DoNothing = Unit