package com.example.talkback.poc.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.talkback.poc.R
import com.example.talkback.poc.utils.DefaultImageOptimization
import com.example.talkback.poc.utils.ViewMeasuredListener

private fun isValidContextForGlide(context: Context?): Boolean {
    return when (context) {
        is Activity -> !context.isDestroyed && !context.isFinishing
        null -> false
        else -> true
    }
}

@Suppress("LongParameterList")
fun ImageView.setImage(
    imageUrl: String,
    @DrawableRes placeholderDrawable: Int? = R.drawable.image_placeholder,
    @DrawableRes errorDrawable: Int? = R.drawable.fallback_image_transparent,
    onImageLoadFail: (() -> Unit)? = null,
    onImageLoadSuccess: (() -> Unit)? = null,
    // Set this for recycled ImageViews (see http://bumptech.github.io/glide/doc/getting-started.html#listview-and-recyclerview)
    clearImage: Boolean = true,
    // Set this to PREFER_RGB_565 decoding to use non-alpha decoding for below Android O
    decodeImageFormat: DecodeFormat = DecodeFormat.DEFAULT
) {
    if (!isValidContextForGlide(this.context)) return

    if (clearImage) Glide.with(this).clear(this) // Cancel pending load and clear previous state

    val requestOptions = RequestOptions().apply {
        placeholderDrawable?.let { placeholder(placeholderDrawable) }
        errorDrawable?.let { error(errorDrawable) }
        format(decodeImageFormat)
    }

    ViewMeasuredListener(this).doOnMeasured {
        Glide.with(this)
            .load(optimizedUrl(setImageOptimisationType(this.context), imageUrl))
            .apply(requestOptions)
            .placeholder(R.drawable.evil_image)
            .listener(getRequestListener(onImageLoadFail, onImageLoadSuccess))
            .into(this)
    }
}

private fun ImageView.optimizedUrl(optimizer: ImageOptimizationType, imageUrl: String): String =
    optimizer.optimizeUrl(
        imageUrl,
        width,
        hasTransparency = true
    )

interface ImageOptimizationType {

    /**
     * Optimizes a source url call for a given image pixel width
     * @param imageUrl - the source url string.
     * @param pixelWidth - the desired pixel width of the downloaded image
     * @param hasTransparency - supply an image format that supports transparency, such as PNG
     * @return - the original [imageUrl] string with added optimization parameters
     */
    fun optimizeUrl(
        imageUrl: String,
        pixelWidth: Int,
        hasTransparency: Boolean = false
    ): String

    companion object {
        val DEFAULT_IMAGE_OPTIMIZATION = DefaultImageOptimization()
        val NONE = object : ImageOptimizationType {
            override fun optimizeUrl(
                imageUrl: String,
                pixelWidth: Int,
                hasTransparency: Boolean
            ): String = imageUrl
        }
    }
}
private const val TV_IMAGE_QUALITY = 60

private fun setImageOptimisationType(context: Context): ImageOptimizationType = DefaultImageOptimization(quality = TV_IMAGE_QUALITY)

private fun getRequestListener(
    onImageLoadFail: (() -> Unit)? = null,
    onImageLoadSuccess: (() -> Unit)? = null
): RequestListener<Drawable> {
    return object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onImageLoadFail?.invoke()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onImageLoadSuccess?.invoke()
            return false
        }
    }
}