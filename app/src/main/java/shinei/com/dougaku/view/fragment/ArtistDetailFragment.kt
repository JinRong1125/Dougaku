package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentArtistDetailBinding
import shinei.com.dougaku.view.adapter.ArtistAlbumAdapter
import shinei.com.dougaku.view.adapter.ArtistSongAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.ArtistDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistDetailFragment: FrameFragment() {

    lateinit var fragmentArtistDetailBinding: FragmentArtistDetailBinding
    lateinit var artistDetailModel: ArtistDetailModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentArtistDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist_detail, container, false)
        fragmentArtistDetailBinding.setLifecycleOwner(this)
        artistDetailModel = ViewModelProviders.of(this, viewModelFactory).get(ArtistDetailModel::class.java)
        fragmentArtistDetailBinding.artistDetailModel = artistDetailModel
        return fragmentArtistDetailBinding.root
    }

    override fun setUp() {
        fragmentArtistDetailBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))
        fragmentArtistDetailBinding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            artistDetailModel.onOffsetChanged(appBarLayout, verticalOffset)
        }
        fragmentArtistDetailBinding.albumRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentArtistDetailBinding.sharedViewModel = sharedViewModel
        val artistAlbumAdapter = ArtistAlbumAdapter(emptyList(), artistDetailModel, sharedViewModel)
        fragmentArtistDetailBinding.albumRecyclerView.adapter = artistAlbumAdapter
        val artistSongAdapter = ArtistSongAdapter(emptyList(), artistDetailModel, sharedViewModel)
        fragmentArtistDetailBinding.songRecyclerView.adapter = artistSongAdapter

        sharedViewModel.selectedArtist.observe(this, Observer {
            it?.run {
                artistDetailModel.artistLiveData.postValue(it)
            }
        })
        artistDetailModel.artistLiveData.observe(this, Observer {
            it?.run {
                artistDetailModel.loadArtistSongsAlbums(it)
            }
        })
        artistDetailModel.albumsLiveData.observe(this, Observer {
            it?.run {
                artistAlbumAdapter.refresh(it)
            }
        })
        artistDetailModel.songsLiveData.observe(this, Observer {
            it?.run {
                artistDetailModel.loadCompleted.postValue(true)
                artistDetailModel.refreshing.postValue(false)
                artistDetailModel.toolbarNameVisibility.postValue(View.VISIBLE)
                val artistDetailDisplayLayout = fragmentArtistDetailBinding.artistDetailDisplayLayout!!.artistDetailDisplayLayout
                (artistDetailDisplayLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        (AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
                artistDetailDisplayLayout.requestLayout()
                artistSongAdapter.refresh(it)
            }
        })
    }
}