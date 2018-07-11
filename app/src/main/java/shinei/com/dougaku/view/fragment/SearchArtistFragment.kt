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
import shinei.com.dougaku.databinding.FragmentSearchArtistBinding
import shinei.com.dougaku.view.adapter.SearchArtistAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.SearchArtistModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchArtistFragment: PageFragment() {

    lateinit var fragmentSearchArtistBinding: FragmentSearchArtistBinding
    lateinit var searchArtistModel: SearchArtistModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchArtistBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_artist, container, false)
        fragmentSearchArtistBinding.setLifecycleOwner(this)
        searchArtistModel = ViewModelProviders.of(this, viewModelFactory).get(SearchArtistModel::class.java)
        fragmentSearchArtistBinding.searchArtistModel = searchArtistModel
        initialized = true
        load()
        return fragmentSearchArtistBinding.root
    }

    override fun setUp() {
        fragmentSearchArtistBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentSearchArtistBinding.sharedViewModel = sharedViewModel
        val searchArtistAdapter = SearchArtistAdapter(emptyList(), searchArtistModel, sharedViewModel)
        fragmentSearchArtistBinding.artistRecyclerView.adapter = searchArtistAdapter

        sharedViewModel.enteredKeyword.observe(this, Observer {
            it?.run {
                searchArtistModel.searchArtists(it)
            }
        })
        searchArtistModel.artistsLiveData.observe(this, Observer {
            it?.run {
                searchArtistModel.refreshing.postValue(false)
                searchArtistAdapter.refresh(it)
                if (it.isEmpty())
                    searchArtistModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    searchArtistModel.emptyImageVisibility.postValue(View.GONE)
            }
        })
    }
}