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
import shinei.com.dougaku.databinding.FragmentSearchAlbumBinding
import shinei.com.dougaku.view.adapter.SearchAlbumAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.SearchAlbumModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchAlbumFragment: PageFragment() {

    lateinit var fragmentSearchAlbumBinding: FragmentSearchAlbumBinding
    lateinit var searchAlbumModel: SearchAlbumModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchAlbumBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_album, container, false)
        fragmentSearchAlbumBinding.setLifecycleOwner(this)
        searchAlbumModel = ViewModelProviders.of(this, viewModelFactory).get(SearchAlbumModel::class.java)
        fragmentSearchAlbumBinding.searchAlbumModel = searchAlbumModel
        initialized = true
        load()
        return fragmentSearchAlbumBinding.root
    }

    override fun setUp() {
        fragmentSearchAlbumBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentSearchAlbumBinding.sharedViewModel = sharedViewModel
        val searchAlbumAdapter = SearchAlbumAdapter(emptyList(), searchAlbumModel, sharedViewModel)
        fragmentSearchAlbumBinding.albumRecyclerView.adapter = searchAlbumAdapter

        sharedViewModel.enteredKeyword.observe(this, Observer {
            it?.run {
                searchAlbumModel.searchAlbums(it)
            }
        })
        searchAlbumModel.albumsLiveData.observe(this, Observer {
            it?.run {
                searchAlbumModel.refreshing.postValue(false)
                searchAlbumAdapter.refresh(it)
                if (it.isEmpty())
                    searchAlbumModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    searchAlbumModel.emptyImageVisibility.postValue(View.GONE)
            }
        })
    }
}