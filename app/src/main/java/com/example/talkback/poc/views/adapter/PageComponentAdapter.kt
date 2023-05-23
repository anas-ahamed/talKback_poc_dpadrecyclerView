package com.example.talkback.poc.views.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.talkback.poc.R
import com.example.talkback.poc.databinding.TvRowBinding
import com.example.talkback.poc.databinding.TvRowDpadBinding
import com.example.talkback.poc.models.ComponentRenderer
import com.example.talkback.poc.models.ItemModel
import com.example.talkback.poc.presenter.RowAdapter
import com.example.talkback.poc.views.ContentGridRowBinder
import com.example.talkback.poc.views.DpadStateHolder
import com.example.talkback.poc.views.ListAnimator
import com.rubensousa.dpadrecyclerview.DpadViewHolder
import com.rubensousa.dpadrecyclerview.UnboundViewPool
import com.rubensousa.dpadrecyclerview.spacing.DpadLinearSpacingDecoration

// TODO move those parameters in one class
internal class PageComponentAdapter(
    private val context: Context,
    private val stateHolder: DpadStateHolder,
    private val recycledViewPool: UnboundViewPool,
    private val componentRenderers: List<ComponentRenderer>
) : ListAdapter<ComponentRenderer, PageComponentAdapter.ViewHolder>(ItemModel.buildDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(componentRenderers[position])
        stateHolder.restore(holder.recyclerView, componentRenderers[position].diffId, holder.adapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(context, recycledViewPool,
           TvRowDpadBinding.inflate(
               LayoutInflater.from(parent.context), parent, false
           ))
    }

    override fun getItemCount(): Int = componentRenderers.size

    override fun getItemViewType(position: Int): Int = componentRenderers[position].viewType

    override fun getItemId(position: Int): Long = componentRenderers[position].id.toLong()

    override fun onViewRecycled(holder: ViewHolder) {
        holder.item?.let { item ->
            stateHolder.save(holder.recyclerView, item.diffId)
        }
        holder.onRecycled()
    }

    class ViewHolder(
        context: Context,
        recycledViewPool: UnboundViewPool,
        private val binding: TvRowDpadBinding
    ) : RecyclerView.ViewHolder(binding.root), DpadViewHolder {

        val recyclerView = binding.rowRecycler
        val adapter = RowAdapter(context)

        var item: ComponentRenderer? = null
            private set

        private val animator = ListAnimator(recyclerView, binding.rowTitle)

        init {
            recyclerView.setHasFixedSize(false)
            recyclerView.addItemDecoration(
                DpadLinearSpacingDecoration.create(
                    itemSpacing = itemView.resources.getDimensionPixelOffset(
                        R.dimen.feature_item_spacing
                    ),
                    edgeSpacing = itemView.resources.getDimensionPixelOffset(
                        R.dimen.feature_edge_spacing
                    ),
                )
            )
            recyclerView.setRecycledViewPool(recycledViewPool)
            recyclerView.setRecycleChildrenOnDetach(true)
            recyclerView.adapter = adapter
        }

        fun bind(item: ComponentRenderer) {
            this.item = item
            adapter.replaceItems(item.cards)
            binding.rowTitle.text = "Title "+item.id
        }

        fun onRecycled() {
            animator.cancel()
            item = null
        }

        override fun onViewHolderSelected() {
            animator.startSelectionAnimation()
        }

        override fun onViewHolderDeselected() {
            animator.startDeselectionAnimation()
        }
    }

}
