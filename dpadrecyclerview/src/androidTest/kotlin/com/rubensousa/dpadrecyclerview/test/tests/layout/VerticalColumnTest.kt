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

package com.rubensousa.dpadrecyclerview.test.tests.layout

import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import com.rubensousa.dpadrecyclerview.ChildAlignment
import com.rubensousa.dpadrecyclerview.ExtraLayoutSpaceStrategy
import com.rubensousa.dpadrecyclerview.ParentAlignment
import com.rubensousa.dpadrecyclerview.test.TestLayoutConfiguration
import com.rubensousa.dpadrecyclerview.test.helpers.assertFocusAndSelection
import com.rubensousa.dpadrecyclerview.test.helpers.getRecyclerViewBounds
import com.rubensousa.dpadrecyclerview.test.helpers.getRelativeItemViewBounds
import com.rubensousa.dpadrecyclerview.test.helpers.onRecyclerView
import com.rubensousa.dpadrecyclerview.test.helpers.selectPosition
import com.rubensousa.dpadrecyclerview.test.tests.DpadRecyclerViewTest
import com.rubensousa.dpadrecyclerview.testfixtures.ColumnLayout
import com.rubensousa.dpadrecyclerview.testfixtures.LayoutConfig
import com.rubensousa.dpadrecyclerview.testing.KeyEvents
import com.rubensousa.dpadrecyclerview.testing.rules.DisableIdleTimeoutRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class VerticalColumnTest : DpadRecyclerViewTest() {

    @get:Rule
    val idleTimeoutRule = DisableIdleTimeoutRule()

    override fun getDefaultLayoutConfiguration(): TestLayoutConfiguration {
        return TestLayoutConfiguration(
            spans = 1,
            orientation = RecyclerView.VERTICAL,
            parentAlignment = ParentAlignment(
                edge = ParentAlignment.Edge.NONE,
                fraction = 0.0f
            ),
            childAlignment = ChildAlignment(
                fraction = 0.0f
            )
        )
    }

    private lateinit var column: ColumnLayout

    @Before
    fun setup() {
        launchFragment()
        val recyclerViewBounds = getRecyclerViewBounds()
        val itemViewBounds = getRelativeItemViewBounds(position = 0)
        column = ColumnLayout(
            LayoutConfig(
                parentWidth = recyclerViewBounds.width(),
                parentHeight = recyclerViewBounds.height(),
                viewWidth = itemViewBounds.width(),
                viewHeight = itemViewBounds.height(),
                defaultItemCount = 1000,
                parentKeyline = 0,
                childKeyline = 0.0f
            )
        )
        column.init(position = 0)
    }

    @Test
    fun testNewViewIsLaidOutInDirectionOfScroll() {
        val lastViewPosition = column.getChildCount() - 1

        scrollDown()
        val lastVisibleView = column.getLastView()!!
        val bounds = getRelativeItemViewBounds(position = lastViewPosition + 1)
        assertThat(bounds).isEqualTo(lastVisibleView.bounds.asRect())
    }

    @Test
    fun testExtraSpaceIsNotLaidOutAfterFirstLayout() {
        assertChildrenPositions(column)
    }

    @Test
    fun testNoExtraSpaceIsAddedWhenScrollingByDefault() {
        scrollDown()
        assertChildrenPositions(column)
        assertFocusAndSelection(1)

        scrollUp()
        assertChildrenPositions(column)
        assertFocusAndSelection(0)
    }

    @Test
    fun testExtraLayoutSpaceIsAddedAtEnd() {
        onRecyclerView("Change extra layout space") { recyclerView ->
            recyclerView.setExtraLayoutSpaceStrategy(object : ExtraLayoutSpaceStrategy {
                override fun calculateEndExtraLayoutSpace(state: RecyclerView.State): Int {
                    return column.getSize()
                }
            })
        }
        column.setExtraLayoutSpace(end = column.getSize())
        assertChildrenPositions(column)
    }

    @Test
    fun testExtraLayoutSpaceIsAddedAtTop() {
        repeat(10) {
            scrollDown()
        }

        assertChildrenPositions(column)

        onRecyclerView("Change extra layout space") { recyclerView ->
            recyclerView.setExtraLayoutSpaceStrategy(object : ExtraLayoutSpaceStrategy {
                override fun calculateStartExtraLayoutSpace(state: RecyclerView.State): Int {
                    return column.getSize()
                }
            })
        }
        column.setExtraLayoutSpace(start = column.getSize())
        assertChildrenPositions(column)
    }

    @Test
    fun testRequestLayoutDuringScrollStillAlignsViews() {
        repeat(5) {
            scrollDown()
            onRecyclerView("RequestLayout") { recyclerView ->
                recyclerView.requestLayout()
            }
        }
        assertChildrenPositions(column)
    }

    @Test
    fun testExtraLayoutSpaceIsAddedAtTopDuringScroll() {
        repeat(10) {
            scrollDown()
        }
        onRecyclerView("Change extra layout space") { recyclerView ->
            recyclerView.setExtraLayoutSpaceStrategy(object : ExtraLayoutSpaceStrategy {
                override fun calculateStartExtraLayoutSpace(state: RecyclerView.State): Int {
                    return column.getSize()
                }
            })
        }
        column.setExtraLayoutSpace(start = column.getSize())
        assertChildrenPositions(column)
    }

    @Test
    fun testLayoutListenerIsInvoked() {
        val childCount = column.getChildCount()
        val viewHolders = ArrayList<RecyclerView.ViewHolder>()
        executeOnFragment { fragment ->
            viewHolders.addAll(fragment.getLayoutEvents())
        }
        assertThat(viewHolders.size).isEqualTo(childCount)
        repeat(childCount) { index ->
            assertThat(viewHolders[index].absoluteAdapterPosition).isEqualTo(index)
        }
    }

    @Test
    fun testDisablingLayoutRemovesAllViews() {
        onRecyclerView("Disable layout") { recyclerView ->
            recyclerView.setLayoutEnabled(false)
        }
        assertThat(getChildrenBounds()).isEmpty()
    }

    @Test
    fun testEnablingLayoutRestoresViews() {
        val viewBounds = getChildrenBounds()
        onRecyclerView("Disable layout") { recyclerView ->
            recyclerView.setLayoutEnabled(false)
        }
        onRecyclerView("Enable layout") { recyclerView ->
            recyclerView.setLayoutEnabled(true)
        }
        assertChildrenPositions(viewBounds)
        assertFocusAndSelection(position = 0)
    }

    @Test
    fun testSelectionIsUpdatedWhenLayoutIsDisabled() {
        // Clear past events
        executeOnFragment { fragment ->
            fragment.clearEvents()
        }
        onRecyclerView("Disable layout") { recyclerView ->
            recyclerView.setLayoutEnabled(false)
        }
        selectPosition(position = 1)

        var position = RecyclerView.NO_POSITION
        onRecyclerView("Get selected position") { recyclerView ->
            position = recyclerView.getSelectedPosition()
        }

        assertThat(position).isEqualTo(1)

        // No selection events should be dispatched
        assertThat(getSelectionEvents()).isEmpty()
        assertThat(getSelectionAndAlignedEvents()).isEmpty()
    }

    private fun scrollUp() {
        KeyEvents.pressUp()
        column.scrollUp()
    }

    private fun scrollDown() {
        KeyEvents.pressDown()
        column.scrollDown()
    }

}
