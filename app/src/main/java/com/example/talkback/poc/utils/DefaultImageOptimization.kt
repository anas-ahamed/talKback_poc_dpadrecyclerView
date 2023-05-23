package com.example.talkback.poc.utils

import androidx.annotation.IntRange
import com.example.talkback.poc.ui.ImageOptimizationType

open class DefaultImageOptimization(
    @IntRange(from = 0, to = 100) private val quality: Int = 85,
    private val bucketList: List<Int> = BUCKET_LIST
) : ImageOptimizationType {

    override fun optimizeUrl(
        imageUrl: String,
        pixelWidth: Int,
        hasTransparency: Boolean
    ): String = "$imageUrl?w=${bucketWidthFrom(pixelWidth)}&f=${imageTypeFrom(hasTransparency)}"

    private fun imageTypeFrom(hasTransparency: Boolean): String =
        if (hasTransparency) "png"
        else "jpg&p=true&q=$quality"

    private fun bucketWidthFrom(pixelWidth: Int) = when (pixelWidth) {
        in 0..LOWER_LIMIT -> pixelWidth
        in LOWER_LIMIT..bucketList.last() -> bucketList.first { it >= pixelWidth }
        else -> bucketList.last()
    }

    companion object {
        private const val LOWER_LIMIT = 100
        private val BUCKET_LIST = listOf(
            200, 300, 400, 500, 600, 700, 1000, 1300, 1700, 1800, 2800, 3300
        )
    }
}
