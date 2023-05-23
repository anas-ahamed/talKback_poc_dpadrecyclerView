/*
 * Copyright 2023 Rúben Sousa
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
import com.rubensousa.dpadrecyclerview.ChildAlignment
import com.rubensousa.dpadrecyclerview.ParentAlignment
import com.rubensousa.dpadrecyclerview.test.TestAdapterConfiguration
import com.rubensousa.dpadrecyclerview.test.TestLayoutConfiguration
import com.rubensousa.dpadrecyclerview.test.helpers.*
import com.rubensousa.dpadrecyclerview.test.tests.DpadRecyclerViewTest
import com.rubensousa.dpadrecyclerview.testfixtures.ColumnLayout
import com.rubensousa.dpadrecyclerview.testfixtures.LayoutConfig
import com.rubensousa.dpadrecyclerview.testing.KeyEvents
import org.junit.Before
import org.junit.Test

class ReverseVerticalTest : DpadRecyclerViewTest() {

    private val numberOfItems = 200

    override fun getDefaultAdapterConfiguration(): TestAdapterConfiguration {
        return super.getDefaultAdapterConfiguration()
            .copy(
                numberOfItems = numberOfItems
            )
    }

    override fun getDefaultLayoutConfiguration(): TestLayoutConfiguration {
        return TestLayoutConfiguration(
            spans = 1,
            orientation = RecyclerView.VERTICAL,
            parentAlignment = ParentAlignment(
                edge = ParentAlignment.Edge.NONE,
                fraction = 0.0f
            ),
            reverseLayout = true,
            childAlignment = ChildAlignment(
                fraction = 0.0f
            )
        )
    }

    private var itemWidth: Int = 0
    private var itemHeight: Int = 0
    private lateinit var column: ColumnLayout

    @Before
    fun setup() {
        launchFragment()
        val recyclerViewBounds = getRecyclerViewBounds()
        val itemViewBounds = getRelativeItemViewBounds(position = 0)
        itemWidth = itemViewBounds.width()
        itemHeight = itemViewBounds.height()
        column = ColumnLayout(
            LayoutConfig(
                parentWidth = recyclerViewBounds.width(),
                parentHeight = recyclerViewBounds.height(),
                viewWidth = itemViewBounds.width(),
                viewHeight = itemViewBounds.height(),
                defaultItemCount = numberOfItems,
                parentKeyline = recyclerViewBounds.height(),
                childKeyline = 0.0f,
                reversed = true
            )
        )
    }

    @Test
    fun testReverseVerticalLayoutItems() {
        column.init(position = 0)
        assertChildrenPositions(column)

        column.init(position = 5)
        selectPosition(position = 5)
        assertChildrenPositions(column)

        column.init(position = numberOfItems - 1)
        selectPosition(position = numberOfItems - 1)
        assertChildrenPositions(column)
    }

    @Test
    fun testReverseVerticalLayoutScrolling() {
        column.init(position = 0)

        repeat(5) {
            KeyEvents.pressDown()
            column.scrollDown()
            assertChildrenPositions(column)
        }

        repeat(5) {
            KeyEvents.pressUp()
            column.scrollUp()
            assertChildrenPositions(column)
        }
    }

}
