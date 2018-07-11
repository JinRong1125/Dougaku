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
import shinei.com.dougaku.databinding.FragmentListeningHistoryBinding
import shinei.com.dougaku.view.adapter.HistoryTracksAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.ListeningHistoryModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ListeningHistoryFragment: FrameFragment() {

    lateinit var fragmentListeningHistoryBinding: FragmentListeningHistoryBinding
    lateinit var listeningHistoryModel: ListeningHistoryModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentListeningHistoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_listening_history, container, false)
        fragmentListeningHistoryBinding.setLifecycleOwner(this)
        listeningHistoryModel = ViewModelProviders.of(this, viewModelFactory).get(ListeningHistoryModel::class.java)
        fragmentListeningHistoryBinding.listeningHistoryModel = listeningHistoryModel
        return fragmentListeningHistoryBinding.root
    }

    override fun setUp() {
        fragmentListeningHistoryBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.green_dark))

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentListeningHistoryBinding.sharedViewModel = sharedViewModel
        val historyTracksAdapter = HistoryTracksAdapter(emptyList(), listeningHistoryModel, sharedViewModel)
        fragmentListeningHistoryBinding.historyTracksRecyclerView.adapter = historyTracksAdapter

        sharedViewModel.historyTracksUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.historyTracksUpdated.postValue(false)
                    listeningHistoryModel.getHistoryTracks()
                }
            }
        })
        listeningHistoryModel.songsLiveData.observe(this, Observer {
            it?.run {
                listeningHistoryModel.refreshing.postValue(false)
                historyTracksAdapter.refresh(it)
                if (it.isEmpty())
                    listeningHistoryModel.emptyImageVisibility.postValue(View.VISIBLE)
                else
                    listeningHistoryModel.emptyImageVisibility.postValue(View.GONE)
            }
        })

        listeningHistoryModel.getHistoryTracks()
    }
}