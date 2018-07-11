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
import shinei.com.dougaku.databinding.FragmentLikedTracksBinding
import shinei.com.dougaku.view.adapter.LikedTracksAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.LikedTracksModel
import shinei.com.dougaku.viewModel.SharedViewModel

class LikedTracksFragment: FrameFragment() {

    lateinit var fragmentLikedTracksBinding: FragmentLikedTracksBinding
    lateinit var likedTracksModel: LikedTracksModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLikedTracksBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_liked_tracks, container, false)
        fragmentLikedTracksBinding.setLifecycleOwner(this)
        likedTracksModel = ViewModelProviders.of(this, viewModelFactory).get(LikedTracksModel::class.java)
        fragmentLikedTracksBinding.likedTracksModel = likedTracksModel
        return fragmentLikedTracksBinding.root
    }

    override fun setUp() {
        fragmentLikedTracksBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val likedTracksAdapter = LikedTracksAdapter(emptyList(), likedTracksModel, sharedViewModel)
        fragmentLikedTracksBinding.likedTracksRecyclerView.adapter = likedTracksAdapter

        sharedViewModel.likedTracksUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.likedTracksUpdated.postValue(false)
                    likedTracksModel.getLikedTracks()
                }
            }
        })
        likedTracksModel.songsLiveData.observe(this, Observer {
            it?.run {
                likedTracksModel.refreshing.postValue(false)
                likedTracksAdapter.refresh(it)
                if (it.isEmpty())
                    likedTracksModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    likedTracksModel.emptyImageVisibility.postValue(View.GONE)
            }
        })

        likedTracksModel.getLikedTracks()
    }
}