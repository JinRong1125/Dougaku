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
import shinei.com.dougaku.databinding.FragmentSearchProducerBinding
import shinei.com.dougaku.view.adapter.SearchProducerAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.SearchProducerModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchProducerFragment: PageFragment() {

    lateinit var fragmentSearchProducerBinding: FragmentSearchProducerBinding
    lateinit var searchProducerModel: SearchProducerModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchProducerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_producer, container, false)
        fragmentSearchProducerBinding.setLifecycleOwner(this)
        searchProducerModel = ViewModelProviders.of(this, viewModelFactory).get(SearchProducerModel::class.java)
        fragmentSearchProducerBinding.searchProducerModel = searchProducerModel
        initialized = true
        load()
        return fragmentSearchProducerBinding.root
    }

    override fun setUp() {
        fragmentSearchProducerBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentSearchProducerBinding.sharedViewModel = sharedViewModel
        val searchProducerAdapter = SearchProducerAdapter(emptyList(), searchProducerModel, sharedViewModel)
        fragmentSearchProducerBinding.producerRecyclerView.adapter = searchProducerAdapter

        sharedViewModel.enteredKeyword.observe(this, Observer {
            it?.run {
                searchProducerModel.searchProducers(it)
            }
        })
        searchProducerModel.producersLiveData.observe(this, Observer {
            it?.run {
                searchProducerModel.refreshing.postValue(false)
                searchProducerAdapter.refresh(it)
                if (it.isEmpty())
                    searchProducerModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    searchProducerModel.emptyImageVisibility.postValue(View.GONE)
            }
        })
    }
}