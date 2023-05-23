package com.example.talkback.poc

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.talkback.poc.databinding.LunaSimplePageDpadBinding
import com.example.talkback.poc.models.Card
import com.example.talkback.poc.models.ComponentRenderer
import com.example.talkback.poc.presenter.ContentGridListRowPresenter
import com.example.talkback.poc.presenter.CustomListRowHeaderPresenter
import com.example.talkback.poc.views.DpadStateHolder
import com.example.talkback.poc.views.adapter.PageComponentAdapter
import com.example.talkback.poc.views.layoutmanager.LunaPageRecyclerLayoutManager
import com.example.talkback.poc.views.scroll.PageScrollListenerTv
import com.rubensousa.dpadrecyclerview.UnboundViewPool
import com.rubensousa.dpadrecyclerview.spacing.DpadLinearSpacingDecoration


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: LunaSimplePageDpadBinding? = null


    private val binding get() = _binding!!
    private var adapter: PageComponentAdapter? = null
    private val stateHolder = DpadStateHolder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LunaSimplePageDpadBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val rowPresenter = ContentGridListRowPresenter(
        useGridNavigation = false,
        horizontalSpacing = context?.resources?.getDimensionPixelSize(R.dimen.poster_primary_card_horizontal_spacing) ?: 0,
        railWindowAlignmentOffset = context?.resources?.getDimensionPixelSize(R.dimen.card_poster_alignment_offset) ?: 0
    ).apply {
        shadowEnabled = false
        headerPresenter = CustomListRowHeaderPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cards = listOf(
            Card("https://us1-prod-images.disco-api.com/2020/06/13/0d72a5f5-8110-3eb8-a83f-b863e680ed17.jpeg", 0),
            Card("https://us1-prod-images.disco-api.com/2023/02/25/ff1c59d8-658b-3d86-b0e1-1754c266da08.jpeg", 1),
            Card("https://us1-prod-images.disco-api.com/2023/03/20/1ffaa1d8-ca75-3010-8f5a-eb8f90890e58.jpeg", 2),
            Card("https://us1-prod-images.disco-api.com/2023/01/27/01e944fe-114c-34e5-b19c-9df1a37fad2c.jpeg", 3),
            Card("https://us1-prod-images.disco-api.com/2022/02/18/69b61539-8c62-3406-8a80-5c29e3b3c385.jpeg", 4),
            Card("https://us1-prod-images.disco-api.com/2023/03/11/da012800-2c0c-3b1c-9bed-bd016adb894e.jpeg", 5),
            Card("https://us1-prod-images.disco-api.com/2022/09/18/546ac442-7554-390c-8013-ca03ea570eec.jpeg", 6),
            Card("https://us1-prod-images.disco-api.com/2020/07/15/4896e80a-4831-3c78-a7e8-043466501053.jpeg", 7),
            Card("https://us1-prod-images.disco-api.com/2020/08/03/28fda903-39e9-3225-89e4-6c39de141bad.jpeg", 8),
            Card("https://us1-prod-images.disco-api.com/2022/06/07/ce628b1f-caaa-39e7-8ca7-8d1920f114ab.jpeg", 9),
            Card("https://us1-prod-images.disco-api.com/2020/08/03/bbb708dd-5247-304d-b0f4-d38d025e580d.jpeg", 10),
            Card("https://us1-prod-images.disco-api.com/2020/07/22/f4dc45d1-041b-32ad-a3f9-a6c0f5014b41.jpeg", 11),
            Card("https://us1-prod-images.disco-api.com/2023/01/24/a48e46c9-e9a7-31ae-b7b0-fe7122f6bf19.jpeg", 12)
        )

        val componentRenederers = listOf<ComponentRenderer>(
            ComponentRenderer(cards, 0, 0),
            ComponentRenderer(cards, 1, 0),
            ComponentRenderer(cards, 2, 0),
            ComponentRenderer(cards, 3, 0),
            ComponentRenderer(cards, 4, 0),
            ComponentRenderer(cards, 5, 0),
            ComponentRenderer(cards, 6, 0),
            ComponentRenderer(cards, 7, 0),
            ComponentRenderer(cards, 8, 0),
            ComponentRenderer(cards, 9, 0),
        )


        context?.let {
            adapter = PageComponentAdapter(
                context = it,
                stateHolder = stateHolder,
                recycledViewPool = UnboundViewPool(),
                componentRenderers = componentRenederers
            )
            _binding?.pageRecycler?.adapter = adapter
            _binding?.pageRecycler?.requestFocus()
            _binding?.pageRecycler?.addItemDecoration(
                DpadLinearSpacingDecoration.create(
                    itemSpacing = resources.getDimensionPixelOffset(R.dimen.vertical_item_spacing)
                )
            )
            /*pageScrollListener.addListener(_binding?.pageRecycler)
            activity?.lifecycleScope?.launchWhenStarted {
                pageScrollListener.run {
                    pageScrollStoppedObservable.collect { isScrollingStopped ->
                        if (isScrollingStopped) {
                            _binding?.pageRecycler?.let {
                                println("talkback scrolling stopped "+it)
                                findAndEmitVisibleViews(it)
                            }
                        }
                    }
                }
            }
            activity?.lifecycleScope?.launchWhenStarted {
                pageScrollListener.visibleViewDataObservable.collect {
                    println("talkback scrolling visibleviewdata ")
                }
            }

            if (_binding?.pageRecycler?.layoutManager == null) {
                _binding?.pageRecycler?.run {
                    layoutManager = pageRecyclerLayoutManager
                }
            }*/
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}