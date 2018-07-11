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
import shinei.com.dougaku.databinding.FragmentMyPlaylistDetailBinding
import shinei.com.dougaku.view.adapter.MyPlaylistTracksAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.MyPlaylistDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class MyPlaylistDetailFragment: FrameFragment() {

    lateinit var fragmentMyPlaylistDetailBinding: FragmentMyPlaylistDetailBinding
    lateinit var myPlaylistDetailModel: MyPlaylistDetailModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMyPlaylistDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlist_detail, container, false)
        fragmentMyPlaylistDetailBinding.setLifecycleOwner(this)
        myPlaylistDetailModel = ViewModelProviders.of(this, viewModelFactory).get(MyPlaylistDetailModel::class.java)
        fragmentMyPlaylistDetailBinding.myPlaylistDetailModel = myPlaylistDetailModel
        return fragmentMyPlaylistDetailBinding.root
    }

    override fun setUp() {
        fragmentMyPlaylistDetailBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))
        fragmentMyPlaylistDetailBinding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            myPlaylistDetailModel.onOffsetChanged(appBarLayout, verticalOffset)
        }

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentMyPlaylistDetailBinding.sharedViewModel = sharedViewModel
        val myPlaylistTracksAdapter = MyPlaylistTracksAdapter(emptyList(), myPlaylistDetailModel, sharedViewModel)
        fragmentMyPlaylistDetailBinding.playlistTracksRecyclerView.adapter = myPlaylistTracksAdapter

        sharedViewModel.selectedPlaylist.observe(this, Observer {
            it?.run {
                myPlaylistDetailModel.playlistLiveData.postValue(it)
            }
        })
        sharedViewModel.myPlaylistUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.myPlaylistUpdated.postValue(false)
                    myPlaylistDetailModel.getMyPlaylist(myPlaylistDetailModel.playlistLiveData.value!!.title)
                }
            }
        })
        myPlaylistDetailModel.playlistLiveData.observe(this, Observer {
            if (it != null)
                myPlaylistDetailModel.songsLiveData.postValue(it.songsList)
            else
                activity!!.supportFragmentManager.popBackStack()
        })
        myPlaylistDetailModel.songsLiveData.observe(this, Observer {
            it?.run {
                myPlaylistDetailModel.refreshing.postValue(false)
                myPlaylistDetailModel.toolbarTitleVisibility.postValue(View.VISIBLE)
                val myPlaylistDetailDisplayLayout = fragmentMyPlaylistDetailBinding.myPlaylistDetailDisplayLayout!!.myPlaylistDetailDisplayLayout
                (myPlaylistDetailDisplayLayout.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        (AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
                myPlaylistDetailDisplayLayout.requestLayout()
                myPlaylistTracksAdapter.refresh(it)
                if (it.isEmpty())
                    myPlaylistDetailModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    myPlaylistDetailModel.emptyImageVisibility.postValue(View.GONE)
            }
        })
    }
}