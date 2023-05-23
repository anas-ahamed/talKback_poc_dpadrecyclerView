package com.example.talkback.poc.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.example.talkback.poc.databinding.ItemPosterBinding
import com.example.talkback.poc.databinding.RoundedCornerImageViewBinding
import com.example.talkback.poc.models.Card
import com.example.talkback.poc.ui.setRoundedImage

@Suppress("ViewConstructor", "LongParameterList")
class PosterCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseCardWidgetTV(context, attrs, defStyleAttr, false, null) {

    protected lateinit var binding: ViewBinding
    protected lateinit var roundedImage: RoundedCornerImageViewBinding

    override fun getBindingView(hideMeta: Boolean, templateId: String?): View {

          binding =  ItemPosterBinding.inflate(LayoutInflater.from(context), this, false)

        roundedImage = RoundedCornerImageViewBinding.bind(binding.root)
        return binding.root
    }

    override fun bindData(card: Card) {
        roundedImage.cardImage.setRoundedImage(imageUrl = card.imageUrl)
    }

}
