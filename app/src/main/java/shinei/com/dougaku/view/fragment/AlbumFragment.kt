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
import shinei.com.dougaku.databinding.FragmentAlbumBinding
import shinei.com.dougaku.view.adapter.AlbumAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.AlbumPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class AlbumFragment: PageFragment() {

    lateinit var fragmentAlbumBinding: FragmentAlbumBinding
    lateinit var albumPageModel: AlbumPageModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentAlbumBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        fragmentAlbumBinding.setLifecycleOwner(this)
        albumPageModel = ViewModelProviders.of(this, viewModelFactory).get(AlbumPageModel::class.java)
        fragmentAlbumBinding.albumPageModel = albumPageModel
        initialized = true
        load()
        return fragmentAlbumBinding.root
    }

    override fun setUp() {
        fragmentAlbumBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val albumAdapter = AlbumAdapter(emptyList(), albumPageModel, sharedViewModel)
        fragmentAlbumBinding.albumGridView.adapter = albumAdapter

        albumPageModel.albumsLiveData.observe(this, Observer {
            it?.run {
                albumPageModel.refreshing.postValue(false)
                albumAdapter.refresh(it)
            }
        })

        albumPageModel.loadAlbums()
    }
}