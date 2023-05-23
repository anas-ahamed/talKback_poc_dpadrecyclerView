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

package com.rubensousa.dpadrecyclerview.test.tests.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.rubensousa.dpadrecyclerview.ChildAlignment
import com.rubensousa.dpadrecyclerview.DpadRecyclerView
import com.rubensousa.dpadrecyclerview.DpadViewHolder
import com.rubensousa.dpadrecyclerview.ParentAlignment
import com.rubensousa.dpadrecyclerview.SubPositionAlignment
import com.rubensousa.dpadrecyclerview.test.TestAdapterConfiguration
import com.rubensousa.dpadrecyclerview.test.TestGridFragment
import com.rubensousa.dpadrecyclerview.test.TestLayoutConfiguration
import com.rubensousa.dpadrecyclerview.test.TestViewHolder
import com.rubensousa.dpadrecyclerview.test.assertions.ViewHolderAlignmentCountAssertion
import com.rubensousa.dpadrecyclerview.test.assertions.ViewHolderSelectionCountAssertion
import com.rubensousa.dpadrecyclerview.test.helpers.assertFocusAndSelection
import com.rubensousa.dpadrecyclerview.test.helpers.selectPosition
import com.rubensousa.dpadrecyclerview.test.helpers.selectSubPosition
import com.rubensousa.dpadrecyclerview.test.tests.DpadRecyclerViewTest
import com.rubensousa.dpadrecyclerview.testing.KeyEvents
import com.rubensousa.dpadrecyclerview.testing.R
import org.junit.Test

class SubSelectionTest : DpadRecyclerViewTest() {

    override fun getDefaultLayoutConfiguration(): TestLayoutConfiguration {
        return TestLayoutConfiguration(
            spans = 1,
            orientation = RecyclerView.VERTICAL,
            parentAlignment = ParentAlignment(
                edge = ParentAlignment.Edge.NONE,
                offset = 0,
                fraction = 0.5f
            ),
            childAlignment = ChildAlignment(
                offset = 0,
                fraction = 0.5f
            )
        )
    }

    override fun getDefaultAdapterConfiguration(): TestAdapterConfiguration {
        return super.getDefaultAdapterConfiguration()
            .copy(itemLayoutId = R.layout.dpadrecyclerview_test_item_subposition)
    }

    private lateinit var fragmentScenario: FragmentScenario<TestSubPositionFragment>

    @Test
    fun testSubPositionsAreFocused() {
        launchSubPositionFragment()

        repeat(2) { index ->
            assertFocusAndSelection(position = index, subPosition = 0)
            KeyEvents.pressDown(times = 1)

            assertFocusAndSelection(position = index, subPosition = 1)
            KeyEvents.pressDown(times = 1)

            assertFocusAndSelection(position = index, subPosition = 2)
            KeyEvents.pressDown(times = 1)
        }

    }

    @Test
    fun testSettingSubPositionUpdatesSelection() {
        launchSubPositionFragment()

        selectSubPosition(1, smooth = true)

        assertFocusAndSelection(position = 0, subPosition = 1)

        selectSubPosition(2, smooth = true)

        assertFocusAndSelection(position = 0, subPosition = 2)

        selectPosition(position = 5, subPosition = 1, smooth = true)

        assertFocusAndSelection(position = 5, subPosition = 1)
    }

    @Test
    fun testViewHolderDoesNotReceiveMultipleSelectionCallbacks() {
        launchSubPositionFragment()

        selectSubPosition(1, smooth = true)
        selectSubPosition(2, smooth = true)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(
                ViewHolderSelectionCountAssertion(
                    selectionCount = 1,
                    deselectionCount = 0,
                    position = 0
                )
            )
    }

    @Test
    fun testViewHolderReceivesAlignmentChangeForEachSubPosition() {
        launchSubPositionFragment()

        selectSubPosition(1, smooth = true)
        selectSubPosition(2, smooth = true)
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(
                ViewHolderAlignmentCountAssertion(
                    count = 3,
                    position = 0
                )
            )
    }

    private fun launchSubPositionFragment() {
        launchSubPositionFragment(
            getDefaultLayoutConfiguration(),
            getDefaultAdapterConfiguration()
        )
    }

    private fun launchSubPositionFragment(
        layoutConfiguration: TestLayoutConfiguration,
        adapterConfiguration: TestAdapterConfiguration
    ): FragmentScenario<TestSubPositionFragment> {
        return launchFragmentInContainer<TestSubPositionFragment>(
            fragmentArgs = TestGridFragment.getArgs(
                layoutConfiguration,
                adapterConfiguration
            ),
            themeResId = R.style.DpadRecyclerViewTestTheme
        ).also {
            fragmentScenario = it
        }
    }

    class TestSubPositionFragment : TestGridFragment() {

        override fun createAdapter(
            recyclerView: DpadRecyclerView,
            adapterConfig: TestAdapterConfiguration
        ): RecyclerView.Adapter<*> {
            val adapter = SubPositionAdapter(adapterConfig.itemLayoutId)
            adapter.submitList(ArrayList<Int>().apply {
                repeat(adapterConfig.numberOfItems) {
                    add(it)
                }
            })
            return adapter
        }

    }

    class SubPositionAdapter(private val layoutId: Int) :
        ListAdapter<Int, SubPositionAdapter.VH>(DIFF_CALLBACK) {

        companion object {

            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Int>() {
                override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                    return oldItem == newItem
                }
            }

        }

        private val subPositionAlignments = ArrayList<SubPositionAlignment>()

        init {
            subPositionAlignments.apply {
                add(
                    SubPositionAlignment(
                        offset = 0,
                        fraction = 0.5f,
                        alignmentViewId = R.id.subPosition0TextView,
                        focusViewId = R.id.subPosition0TextView
                    )
                )
                add(
                    SubPositionAlignment(
                        offset = 0,
                        fraction = 0.5f,
                        alignmentViewId = R.id.subPosition1TextView,
                        focusViewId = R.id.subPosition1TextView
                    )
                )
                add(
                    SubPositionAlignment(
                        offset = 0,
                        fraction = 0.5f,
                        alignmentViewId = R.id.subPosition2TextView,
                        focusViewId = R.id.subPosition2TextView
                    )
                )
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val viewHolder = VH(
                view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
                alignments = subPositionAlignments
            )
            viewHolder.itemView.isFocusable = true
            viewHolder.itemView.isFocusableInTouchMode = true
            return viewHolder
        }

        override fun onBindViewHolder(holder: VH, position: Int) {}

        class VH(
            view: View,
            private val alignments: List<SubPositionAlignment>
        ) : TestViewHolder(view), DpadViewHolder {

            override fun getSubPositionAlignments(): List<SubPositionAlignment> {
                return alignments
            }

        }

    }


}