/*
 * Copyright © 2020, Discovery Networks International
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.example.talkback.poc.models

import androidx.annotation.DrawableRes
import com.bumptech.glide.Priority
import com.example.talkback.poc.R

data class ImageAtomModel(
    val imageUrl: String,
    val contentDescription: String?,
    var onClicked: (() -> Unit)? = null,
    val cornerRadius: Int? = null,
    @DrawableRes val placeHolder: Int? = R.drawable.image_placeholder,
    @DrawableRes val errorDrawable: Int? = R.drawable.fallback_image_transparent,
    val onImageLoadFail: (() -> Unit)? = null,
    val onImageLoadSuccess: (() -> Unit)? = null,
    val priority: Priority = Priority.NORMAL
)
