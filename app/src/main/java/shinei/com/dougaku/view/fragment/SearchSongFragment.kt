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
import shinei.com.dougaku.databinding.FragmentSearchSongBinding
import shinei.com.dougaku.view.adapter.SearchSongAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.SearchSongModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchSongFragment: PageFragment() {

    lateinit var fragmentSearchSongBinding: FragmentSearchSongBinding
    lateinit var searchSongModel: SearchSongModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchSongBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_song, container, false)
        fragmentSearchSongBinding.setLifecycleOwner(this)
        searchSongModel = ViewModelProviders.of(this, viewModelFactory).get(SearchSongModel::class.java)
        fragmentSearchSongBinding.searchSongModel = searchSongModel
        initialized = true
        load()
        return fragmentSearchSongBinding.root
    }

    override fun setUp() {
        fragmentSearchSongBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentSearchSongBinding.sharedViewModel = sharedViewModel
        val searchSongAdapter = SearchSongAdapter(emptyList(), searchSongModel, sharedViewModel)
        fragmentSearchSongBinding.songRecyclerView.adapter = searchSongAdapter

        sharedViewModel.enteredKeyword.observe(this, Observer {
            it?.run {
                searchSongModel.searchSongs(it)
            }
        })
        searchSongModel.songsLiveData.observe(this, Observer {
            it?.run {
                searchSongModel.refreshing.postValue(false)
                searchSongAdapter.refresh(it)
                if (it.isEmpty())
                    searchSongModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    searchSongModel.emptyImageVisibility.postValue(View.GONE)
            }
        })
    }
}