package com.example.talkback.poc.views.atom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.talkback.poc.models.ImageAtomModel
import com.example.talkback.poc.ui.setImage
import com.example.talkback.poc.views.BaseView

class AtomRoundedImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), BaseView<ImageAtomModel> {

    override fun bindData(data: ImageAtomModel) {
        with(data) {
            placeHolder?.let { placeholder ->
                data.errorDrawable?.let { errorDrawable ->
                    setImage(
                        data.imageUrl,
                        placeholderDrawable = placeholder,
                        errorDrawable = errorDrawable,
                        onImageLoadSuccess = {
                            isFocusable = true
                            isFocusableInTouchMode = true
                            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                        }
                    )
                }
            }
            onClicked?.let {
                setOnClickListener { onClicked?.invoke() }
            }
        }
    }
}
