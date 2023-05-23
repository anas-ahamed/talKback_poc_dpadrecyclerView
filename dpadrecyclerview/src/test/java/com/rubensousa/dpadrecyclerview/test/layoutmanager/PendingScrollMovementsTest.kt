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

package com.rubensousa.dpadrecyclerview.test.layoutmanager

import androidx.recyclerview.widget.RecyclerView
import com.google.common.truth.Truth.assertThat
import com.rubensousa.dpadrecyclerview.layoutmanager.LayoutConfiguration
import com.rubensousa.dpadrecyclerview.layoutmanager.scroll.PendingScrollMovements
import com.rubensousa.dpadrecyclerview.test.layoutmanager.mock.LayoutInfoMock
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class PendingScrollMovementsTest {

    private val configuration = LayoutConfiguration(RecyclerView.LayoutManager.Properties())
    private val mockLayoutInfo = LayoutInfoMock(mockk(), configuration)
    private lateinit var scrollMovements: PendingScrollMovements

    @Before
    fun setup() {
        scrollMovements = PendingScrollMovements(maxPendingMoves = 2, mockLayoutInfo.get())
        mockLayoutInfo.hasCreatedFirstItem = false
        mockLayoutInfo.hasCreatedLastItem = false
    }

    @Test
    fun `increasing pending moves does not go over the limit`() {
        scrollMovements.setMaxPendingMoves(5)

        repeat(10) {
            scrollMovements.add(forward = true)
        }

        assertThat(scrollMovements.pendingMoves).isEqualTo(5)
    }

    @Test
    fun `decreasing pending moves does not go under the limit`() {
        scrollMovements.setMaxPendingMoves(5)

        repeat(10) {
            scrollMovements.add(forward = false)
        }

        assertThat(scrollMovements.pendingMoves).isEqualTo(-5)
    }

    @Test
    fun `shouldStopScrolling is true if we don't have pending moves`() {
        scrollMovements.setMaxPendingMoves(5)
        repeat(10) {
            scrollMovements.add(forward = true)
        }
        // Consume all events
        while (scrollMovements.consume()) {
        }

        assertThat(scrollMovements.shouldStopScrolling()).isTrue()
    }

    @Test
    fun `shouldStopScrolling is true if layout is complete in scroll direction`() {
        scrollMovements.setMaxPendingMoves(5)

        repeat(10) {
            scrollMovements.add(forward = true)
        }

        mockLayoutInfo.hasCreatedLastItem = true

        assertThat(scrollMovements.shouldStopScrolling()).isTrue()

        repeat(20) {
            scrollMovements.add(forward = false)
        }

        mockLayoutInfo.hasCreatedFirstItem = true

        assertThat(scrollMovements.shouldStopScrolling()).isTrue()
    }

    @Test
    fun `consume only consumes one event`() {
        assertThat(scrollMovements.consume()).isFalse()

        scrollMovements.add(forward = true)

        assertThat(scrollMovements.consume()).isTrue()
        assertThat(scrollMovements.consume()).isFalse()

        scrollMovements.add(forward = false)
        assertThat(scrollMovements.consume()).isTrue()
        assertThat(scrollMovements.consume()).isFalse()
    }

    @Test
    fun `shouldScrollToView returns true if view is in scrolling direction or is already the pivot`() {
        scrollMovements.add(forward = true)
        assertThat(
            scrollMovements.shouldScrollToView(
                viewPosition = 5,
                pivotPosition = 6
            )
        ).isFalse()
        assertThat(scrollMovements.shouldScrollToView(viewPosition = 7, pivotPosition = 6)).isTrue()
        assertThat(scrollMovements.shouldScrollToView(viewPosition = 6, pivotPosition = 6)).isTrue()

        scrollMovements.add(forward = false)
        scrollMovements.add(forward = false)
        assertThat(
            scrollMovements.shouldScrollToView(
                viewPosition = 7,
                pivotPosition = 6
            )
        ).isFalse()
        assertThat(scrollMovements.shouldScrollToView(viewPosition = 5, pivotPosition = 6)).isTrue()
        assertThat(scrollMovements.shouldScrollToView(viewPosition = 6, pivotPosition = 6)).isTrue()
    }

    @Test
    fun `scroll movement in reverse layout is correct`() {
        mockLayoutInfo.reverseLayout = true

        scrollMovements.add(forward = true)

        assertThat(scrollMovements.pendingMoves).isEqualTo(-1)
        
        scrollMovements.add(forward = false)

        assertThat(scrollMovements.pendingMoves).isEqualTo(0)
    }

    @Test
    fun `should stop scrolling if edge views are created in reverse layout`() {
        mockLayoutInfo.reverseLayout = true

        scrollMovements.setMaxPendingMoves(5)

        repeat(5) {
            scrollMovements.add(forward = true)
        }

        mockLayoutInfo.hasCreatedLastItem = true

        assertThat(scrollMovements.shouldStopScrolling()).isTrue()

        repeat(10) {
            scrollMovements.add(forward = false)
        }

        mockLayoutInfo.hasCreatedFirstItem = true

        assertThat(scrollMovements.shouldStopScrolling()).isTrue()
    }


}