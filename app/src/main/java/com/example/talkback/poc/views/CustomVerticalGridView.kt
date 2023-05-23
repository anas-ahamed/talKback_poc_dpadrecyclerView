package com.example.talkback.poc.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.leanback.widget.VerticalGridView

/*
TODO this shouldn't be necessary. Focus is managed via properties such as focusOutFront and focusOutEnd,
    descendantFocusability etc. in layouts. By changing those properties, we can change the behavior of views
    and child views and create a navigation path for the remote. See the sports-schedule feature module for an example.
    */
@Deprecated("Use layout focus parameters instead")
open class CustomVerticalGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VerticalGridView(context, attrs, defStyleAttr) {

    private var lastKeyDownPosition = 0
    private var scrollFocusedItem = true
    private var effectiveStartPosition = NO_POSITION

    fun setEffectiveStartPosition(value: Int) {
        effectiveStartPosition = value
    }

    fun scrollToFocusedChildPosition(scrollItem: Boolean) {
        scrollFocusedItem = scrollItem
    }

    fun findFirstFocusableView(): View? {
        return layoutManager?.findViewByPosition(effectiveStartPosition)
    }

    fun getEffectiveStartPosition(): Int {
        return effectiveStartPosition
    }

    /**
     * when scroll quickly for grid items, sometimes dpad up/down doesn't work.
     * scroll to position for focused rail will fix the issue.
     */
    /*override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return when (event?.keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (event.action == KeyEvent.ACTION_DOWN && scrollFocusedItem) {
                    if (scrollState == SCROLL_STATE_IDLE && selectedPosition != lastKeyDownPosition) {
                        lastKeyDownPosition = selectedPosition
                        scrollToPosition(selectedPosition)
                    }
                    false
                } else {
//                    (context as? Activity)?.root?.findViewById<LinearLayout>(R.id.stickyLayout)?.y = 0F
                    super.dispatchKeyEvent(event)
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    requestFocusUp()
                }
                false
            }
            else -> super.dispatchKeyEvent(event)
        }
    }*/

    /**
     * If some rails are empty and hidden at the top of the list, we request the focus up manually on key up
     * if the selected position is effectively the first position.
     */
    private fun requestFocusUp() {
        if (selectedPosition == effectiveStartPosition) {
            try {
                findFragment<Fragment>().view?.findViewById<View>(nextFocusUpId)?.requestFocus()
            } catch (ex: Exception) {
                println(ex)
            }
        }
    }
}
