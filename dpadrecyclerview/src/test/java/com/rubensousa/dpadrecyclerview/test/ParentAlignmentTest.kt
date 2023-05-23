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

package com.rubensousa.dpadrecyclerview.test

import com.google.common.truth.Truth.assertThat
import com.rubensousa.dpadrecyclerview.ParentAlignment
import org.junit.Test

class ParentAlignmentTest {

    @Test
    fun `align to parent center by default`() {
        val parentAlignment = ParentAlignment()
        assertThat(parentAlignment.edge).isEqualTo(ParentAlignment.Edge.MIN_MAX)
        assertThat(parentAlignment.fraction).isEqualTo(0.5f)
        assertThat(parentAlignment.offset).isEqualTo(0)
        assertThat(parentAlignment.isFractionEnabled).isTrue()
    }

}