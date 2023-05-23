package com.example.talkback.poc.utils

import android.view.View
import android.view.ViewTreeObserver

/**
 * View lifecycle aware listener that will run **once** when the view has been measured (width or height > 0).
 */
class ViewMeasuredListener(
    private val view: View
) : View.OnLayoutChangeListener, ViewTreeObserver.OnGlobalLayoutListener {
    private val hasDimensions: Boolean get() = view.width + view.height > 0
    private val onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) = checkIfMeasured()
        override fun onViewDetachedFromWindow(view: View) = removeListeners()
    }
    private var onMeasured: () -> Unit = { }

    fun doOnMeasured(onMeasured: () -> Unit) {
        if (hasDimensions && view.isLaidOut && !view.isLayoutRequested) {
            // view has been measured already, is laid out and not expecting a layout pass
            onMeasured()
        } else {
            attachListeners(onMeasured)
        }
    }

    private fun attachListeners(onMeasured: () -> Unit) {
        this.onMeasured = onMeasured
        view.addOnLayoutChangeListener(this)
        view.viewTreeObserver.addOnGlobalLayoutListener(this)
        view.addOnAttachStateChangeListener(onAttachStateChangeListener)
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) = checkIfMeasured()

    override fun onGlobalLayout() = checkIfMeasured()

    private fun checkIfMeasured() {
        if (hasDimensions) {
            onMeasured()
            removeListeners()
        }
    }

    private fun removeListeners() {
        view.removeOnLayoutChangeListener(this)
        view.removeOnAttachStateChangeListener(onAttachStateChangeListener)
        view.viewTreeObserver.takeIf { it.isAlive }?.removeOnGlobalLayoutListener(this)
        onMeasured = { }
    }
}
