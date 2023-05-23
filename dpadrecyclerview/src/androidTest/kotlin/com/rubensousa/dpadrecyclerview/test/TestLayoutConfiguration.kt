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

package com.rubensousa.dpadrecyclerview.test

import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import com.rubensousa.dpadrecyclerview.ChildAlignment
import com.rubensousa.dpadrecyclerview.FocusableDirection
import com.rubensousa.dpadrecyclerview.ParentAlignment

data class TestLayoutConfiguration(
    val spans: Int,
    val orientation: Int,
    val parentAlignment: ParentAlignment,
    val childAlignment: ChildAlignment,
    val gravity: Int = Gravity.START,
    val reverseLayout: Boolean = false,
    val useCustomViewPool: Boolean = false,
    val recycleChildrenOnDetach: Boolean = false,
    val focusSearchEnabledDuringAnimations: Boolean = false,
    val focusableDirection: FocusableDirection = FocusableDirection.STANDARD
) : Parcelable {


    constructor(parcel: Parcel) : this(
        spans = parcel.readInt(),
        orientation = parcel.readInt(),
        parentAlignment = requireNotNull(
            parcel.readParcelable(
                ParentAlignment::class.java.classLoader, ParentAlignment::class.java
            )
        ),
        childAlignment = requireNotNull(
            parcel.readParcelable(
                ChildAlignment::class.java.classLoader, ChildAlignment::class.java
            )
        ),
        gravity = parcel.readInt(),
        reverseLayout = parcel.readByte() != 0.toByte(),
        useCustomViewPool = parcel.readByte() != 0.toByte(),
        recycleChildrenOnDetach = parcel.readByte() != 0.toByte(),
        focusSearchEnabledDuringAnimations = parcel.readByte() != 0.toByte(),
        focusableDirection = FocusableDirection.values()[parcel.readInt()]
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(spans)
        parcel.writeInt(orientation)
        parcel.writeParcelable(parentAlignment, flags)
        parcel.writeParcelable(childAlignment, flags)
        parcel.writeInt(gravity)
        parcel.writeByte(if (reverseLayout) 1 else 0)
        parcel.writeByte(if (useCustomViewPool) 1 else 0)
        parcel.writeByte(if (recycleChildrenOnDetach) 1 else 0)
        parcel.writeByte(if (focusSearchEnabledDuringAnimations) 1 else 0)
        parcel.writeInt(focusableDirection.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TestLayoutConfiguration> {
        override fun createFromParcel(parcel: Parcel): TestLayoutConfiguration {
            return TestLayoutConfiguration(parcel)
        }

        override fun newArray(size: Int): Array<TestLayoutConfiguration?> {
            return arrayOfNulls(size)
        }
    }
}