package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentProducerDetailBinding
import shinei.com.dougaku.view.adapter.ProducerAlbumAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.ProducerDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ProducerDetailFragment: FrameFragment() {

    lateinit var fragmentProducerDetailBinding: FragmentProducerDetailBinding
    lateinit var producerDetailModel: ProducerDetailModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentProducerDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_producer_detail, container, false)
        fragmentProducerDetailBinding.setLifecycleOwner(this)
        producerDetailModel = ViewModelProviders.of(this, viewModelFactory).get(ProducerDetailModel::class.java)
        fragmentProducerDetailBinding.producerDetailModel = producerDetailModel
        return fragmentProducerDetailBinding.root
    }

    override fun setUp() {
        fragmentProducerDetailBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))
        fragmentProducerDetailBinding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            producerDetailModel.onOffsetChanged(appBarLayout, verticalOffset)
        }

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val producerAlbumAdapter = ProducerAlbumAdapter(emptyList(), producerDetailModel, sharedViewModel)
        fragmentProducerDetailBinding.albumGridView.adapter = producerAlbumAdapter

        sharedViewModel.selectedProducer.observe(this, Observer {
            it?.run {
                if (producerDetailModel.producerLiveData.value == null)
                    producerDetailModel.producerLiveData.postValue(it)
            }
        })
        producerDetailModel.producerLiveData.observe(this, Observer {
            it?.run {
                producerDetailModel.loadProducerAlbums(it)
            }
        })
        producerDetailModel.albumsLiveData.observe(this, Observer {
            it?.run {
                producerDetailModel.loadCompleted.postValue(true)
                producerDetailModel.refreshing.postValue(false)
                producerDetailModel.toolbarNameVisibility.postValue(View.VISIBLE)
                val producerDetailDisplayLayout = fragmentProducerDetailBinding.producerDetailDisplayLayout!!.producerDetailDisplayLayout
                (producerDetailDisplayLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        (AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
                producerDetailDisplayLayout.requestLayout()
                producerAlbumAdapter.refresh(it)
            }
        })
    }
}