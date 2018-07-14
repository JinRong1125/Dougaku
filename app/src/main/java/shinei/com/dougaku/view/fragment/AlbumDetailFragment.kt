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
import shinei.com.dougaku.databinding.FragmentAlbumDetailBinding
import shinei.com.dougaku.view.adapter.AlbumDetailAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.AlbumDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class AlbumDetailFragment: FrameFragment() {

    lateinit var fragmentAlbumDetailBinding: FragmentAlbumDetailBinding
    lateinit var albumDetailModel: AlbumDetailModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentAlbumDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail, container, false)
        fragmentAlbumDetailBinding.setLifecycleOwner(this)
        albumDetailModel = ViewModelProviders.of(this, viewModelFactory).get(AlbumDetailModel::class.java)
        fragmentAlbumDetailBinding.albumDetailModel = albumDetailModel
        return fragmentAlbumDetailBinding.root
    }

    override fun setUp() {
        fragmentAlbumDetailBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))
        fragmentAlbumDetailBinding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            albumDetailModel.onOffsetChanged(appBarLayout, verticalOffset)
        }

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentAlbumDetailBinding.sharedViewModel = sharedViewModel
        val albumDetailAdapter = AlbumDetailAdapter(emptyList(), albumDetailModel, sharedViewModel)
        fragmentAlbumDetailBinding.trackRecyclerView.adapter = albumDetailAdapter

        sharedViewModel.selectedAlbum.observe(this, Observer {
            it?.run {
                if (albumDetailModel.albumLiveData.value == null)
                    albumDetailModel.albumLiveData.postValue(it)
            }
        })
        albumDetailModel.albumLiveData.observe(this, Observer {
            it?.run {
                albumDetailModel.getAlbumSongs(it)
            }
        })
        albumDetailModel.songsLiveData.observe(this, Observer {
            it?.run {
                albumDetailModel.loadCompleted.postValue(true)
                albumDetailModel.refreshing.postValue(false)
                albumDetailModel.toolbarTitleVisibility.postValue(View.VISIBLE)
                val albumDetailDisplayLayout = fragmentAlbumDetailBinding.albumDetailDisplayLayout!!.albumDetailDisplayLayout
                (albumDetailDisplayLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        (AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
                albumDetailDisplayLayout.requestLayout()
                albumDetailAdapter.refresh(it)
            }
        })
    }
}