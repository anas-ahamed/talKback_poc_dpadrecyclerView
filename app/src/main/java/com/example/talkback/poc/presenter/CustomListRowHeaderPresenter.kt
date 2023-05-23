package com.example.talkback.poc.presenter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowHeaderPresenter
import com.example.talkback.poc.R

@SuppressLint("RestrictedApi")
class CustomListRowHeaderPresenter : RowHeaderPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_content_rail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val headerItem = (item as? Row)?.headerItem
        val textHeading = viewHolder.view?.findViewById<TextView>(R.id.listRowTitle)
        textHeading?.text = headerItem?.name
        textHeading?.isVisible = headerItem?.name?.isNotBlank() == true
    }

    override fun onSelectLevelChanged(holder: ViewHolder?) {
        // This method is overridden so that header colour will not be changed based on row focus
    }
}
