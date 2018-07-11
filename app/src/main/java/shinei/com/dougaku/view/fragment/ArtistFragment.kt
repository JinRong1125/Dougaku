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
import shinei.com.dougaku.databinding.FragmentArtistBinding
import shinei.com.dougaku.view.adapter.ArtistAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.ArtistPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistFragment: PageFragment() {

    lateinit var fragmentArtistBinding: FragmentArtistBinding
    lateinit var artistPageModel: ArtistPageModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentArtistBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artist, container, false)
        fragmentArtistBinding.setLifecycleOwner(this)
        artistPageModel = ViewModelProviders.of(this, viewModelFactory).get(ArtistPageModel::class.java)
        fragmentArtistBinding.artistPageModel = artistPageModel
        initialized = true
        load()
        return fragmentArtistBinding.root
    }

    override fun setUp() {
        fragmentArtistBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        val artistAdapter = ArtistAdapter(emptyList(), artistPageModel, sharedViewModel)
        fragmentArtistBinding.artistGridView.adapter = artistAdapter

        artistPageModel.artistsLiveData.observe(this, Observer {
            it?.run {
                artistPageModel.refreshing.postValue(false)
                artistAdapter.refresh(it)
            }
        })

        artistPageModel.loadArtists()
    }
}