package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentProducerBinding
import shinei.com.dougaku.view.adapter.ProducerAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.ProducerPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ProducerFragment: PageFragment() {

    lateinit var fragmentProducerBinding: FragmentProducerBinding
    lateinit var producerPageModel: ProducerPageModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentProducerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_producer, container, false)
        fragmentProducerBinding.setLifecycleOwner(this)
        producerPageModel = ViewModelProviders.of(this, viewModelFactory).get(ProducerPageModel::class.java)
        fragmentProducerBinding.producerPageModel = producerPageModel
        initialized = true
        load()
        return fragmentProducerBinding.root
    }

    override fun setUp() {
        fragmentProducerBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val producerAdapter = ProducerAdapter(emptyList(), producerPageModel, sharedViewModel)
        fragmentProducerBinding.producerGridView.adapter = producerAdapter

        producerPageModel.producersLiveData.observe(this, Observer {
            it?.run {
                producerPageModel.refreshing.postValue(false)
                producerAdapter.refresh(it)
            }
        })

        producerPageModel.loadProducers()
    }
}