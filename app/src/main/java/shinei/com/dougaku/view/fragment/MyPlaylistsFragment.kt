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
import shinei.com.dougaku.databinding.FragmentMyPlaylistsBinding
import shinei.com.dougaku.view.adapter.MyPlaylistsAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.MyPlaylistsModel
import shinei.com.dougaku.viewModel.SharedViewModel

class MyPlaylistsFragment: FrameFragment() {

    lateinit var fragmentMyPlaylistsBinding: FragmentMyPlaylistsBinding
    lateinit var myPlaylistsModel: MyPlaylistsModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMyPlaylistsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false)
        fragmentMyPlaylistsBinding.setLifecycleOwner(this)
        myPlaylistsModel = ViewModelProviders.of(this, viewModelFactory).get(MyPlaylistsModel::class.java)
        fragmentMyPlaylistsBinding.myPlaylistsModel = myPlaylistsModel
        return fragmentMyPlaylistsBinding.root
    }

    override fun setUp() {
        fragmentMyPlaylistsBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val myPlaylistsAdapter = MyPlaylistsAdapter(emptyList(), myPlaylistsModel, sharedViewModel)
        fragmentMyPlaylistsBinding.myPlaylistsRecyclerView.adapter = myPlaylistsAdapter

        sharedViewModel.myPlaylistUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.myPlaylistUpdated.postValue(false)
                    myPlaylistsModel.getMyPlaylists()
                }
            }
        })
        myPlaylistsModel.myPlaylistsLiveData.observe(this, Observer {
            it?.run {
                myPlaylistsModel.refreshing.postValue(false)
                myPlaylistsAdapter.refresh(it)
                if (it.isEmpty())
                    myPlaylistsModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    myPlaylistsModel.emptyImageVisibility.postValue(View.GONE)
            }
        })

        myPlaylistsModel.getMyPlaylists()
    }
}