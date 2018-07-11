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
import shinei.com.dougaku.databinding.FragmentLikedAlbumsBinding
import shinei.com.dougaku.view.adapter.LikedAlbumsAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.LikedAlbumsModel
import shinei.com.dougaku.viewModel.SharedViewModel

class LikedAlbumsFragment: FrameFragment() {

    lateinit var fragmentLikedAlbumsBinding: FragmentLikedAlbumsBinding
    lateinit var likedAlbumsModel: LikedAlbumsModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentLikedAlbumsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_liked_albums, container, false)
        fragmentLikedAlbumsBinding.setLifecycleOwner(this)
        likedAlbumsModel = ViewModelProviders.of(this, viewModelFactory).get(LikedAlbumsModel::class.java)
        fragmentLikedAlbumsBinding.likedAlbumsModel = likedAlbumsModel
        return fragmentLikedAlbumsBinding.root
    }

    override fun setUp() {
        fragmentLikedAlbumsBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val likedAlbumsAdapter = LikedAlbumsAdapter(emptyList(), likedAlbumsModel, sharedViewModel)
        fragmentLikedAlbumsBinding.likedAlbumsRecyclerView.adapter = likedAlbumsAdapter

        sharedViewModel.likedAlbumsUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.likedAlbumsUpdated.postValue(false)
                    likedAlbumsModel.getLikedAlbums()
                }
            }
        })
        likedAlbumsModel.albumsLiveData.observe(this, Observer {
            it?.run {
                likedAlbumsModel.refreshing.postValue(false)
                likedAlbumsAdapter.refresh(it)
                if (it.isEmpty())
                    likedAlbumsModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    likedAlbumsModel.emptyImageVisibility.postValue(View.GONE)
            }
        })

        likedAlbumsModel.getLikedAlbums()
    }
}