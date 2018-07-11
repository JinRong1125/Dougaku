package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import net.skoumal.fragmentback.BackFragment
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentPlayerBinding
import shinei.com.dougaku.helper.ControlSlidingUpPanelLayout
import shinei.com.dougaku.helper.PlayMode
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.adapter.PlayListAdapter
import shinei.com.dougaku.view.adapter.TrackPagerAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.PlayerViewModel
import shinei.com.dougaku.viewModel.SharedViewModel

class PlayerFragment: FrameFragment() {

    lateinit var fragmentPlayerBinding: FragmentPlayerBinding
    lateinit var playerViewModel: PlayerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        fragmentPlayerBinding.setLifecycleOwner(this)
        playerViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlayerViewModel::class.java)
        fragmentPlayerBinding.playerViewModel = playerViewModel
        return fragmentPlayerBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.insertSettings()
    }

    override fun onBackPressed(): Boolean {
        return if (fragmentPlayerBinding.playListLayout!!.playListLayout.visibility == View.VISIBLE) {
            playerViewModel.playListOpened.postValue(false)
            true
        }
        else if (fragmentPlayerBinding.mainLayout?.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            playerViewModel.panelState.postValue(SlidingUpPanelLayout.PanelState.COLLAPSED)
            true
        }
        else
            false
    }

    override fun getBackPriority(): Int {
        return BackFragment.HIGH_BACK_PRIORITY
    }

    override fun setUp() {
        val trackViewPager = fragmentPlayerBinding.trackViewPager
        trackViewPager.addOnPageChangeListener(playerViewModel.onPageChangeListener)
        val playListLayout = fragmentPlayerBinding.playListLayout!!.playListLayout

        val sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentPlayerBinding.sharedViewModel = sharedViewModel
        sharedViewModel.selectedSongs.observe(this, Observer {
            it?.run {
                playerViewModel.songsLiveData.postValue(it)
            }
        })
        sharedViewModel.likedTracksUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    sharedViewModel.likedTracksUpdated.postValue(false)
                    val panelState = playerViewModel.panelState.value
                    if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED ||
                            panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
                        playerViewModel.getLikedTrack(playerViewModel.songLiveData.value!!)
                }
            }
        })
        playerViewModel.settingsLiveData.observe(this, Observer {
            it?.run {
                playerViewModel.haveSettings = true
                sharedViewModel.selectedSongs.postValue(it.songsList)
                sharedViewModel.selectedTrack.postValue(it.track)
            }
        })
        playerViewModel.panelState.observe(this, Observer {
            it?.run {
                fragmentPlayerBinding.mainLayout?.panelState = it
            }
        })
        playerViewModel.playListOpened.observe(this, Observer {
            it?.run {
                if (!it) {
                    playListLayout.animate().alpha(0f)
                            .withEndAction({playListLayout.visibility = View.GONE})
                    fragmentPlayerBinding.mainLayout?.isTouchEnabled = true
                }
                else {
                    fragmentPlayerBinding.mainLayout?.isTouchEnabled = false
                    fragmentPlayerBinding.playListLayout!!.playlistRecyclerView.scrollToPosition(playerViewModel.currentTrack.value!!)
                    playListLayout.animate().alpha(1f)
                            .withStartAction({playListLayout.visibility = View.VISIBLE})
                }
            }
        })
        playerViewModel.songsLiveData.observe(this, Observer {
            it?.run {
                trackViewPager.adapter = TrackPagerAdapter(it)
                fragmentPlayerBinding.playListLayout!!.playlistRecyclerView.adapter = PlayListAdapter(it, playerViewModel, sharedViewModel)
                playerViewModel.prepareTrack(sharedViewModel.selectedTrack.value!!)
                playerViewModel.panelState.postValue(SlidingUpPanelLayout.PanelState.COLLAPSED)
            }
        })
        playerViewModel.currentTrack.observe(this, Observer {
            it?.run {
                trackViewPager.currentItem = it
                playerViewModel.songLiveData.postValue(playerViewModel.songsLiveData.value?.get(it))
                fragmentPlayerBinding.playListLayout!!.playlistRecyclerView.adapter.notifyItemChanged(playerViewModel.updateTrack)
                fragmentPlayerBinding.playListLayout!!.playlistRecyclerView.adapter.notifyItemChanged(it)
                playerViewModel.isUIUnUpdated = false
            }
        })
        playerViewModel.songLiveData.observe(this, Observer {
            it?.run {
                playerViewModel.getLikedTrack(it)
                playerViewModel.delayLoadTrack(it)
            }
        })
        playerViewModel.playModel.observe(this, Observer {
            it?.run {
                when (it) {
                    PlayMode.NORMAL -> {
                        playerViewModel.shuffleDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_shuffle))
                        playerViewModel.repeatDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_repeat))
                    }
                    PlayMode.SHUFFLE -> {
                        playerViewModel.shuffleDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_shuffle_on))
                        playerViewModel.repeatDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_repeat))
                        playerViewModel.createShuffleList()
                    }
                    PlayMode.REPEAT -> {
                        playerViewModel.shuffleDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_shuffle))
                        playerViewModel.repeatDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_repeat_on))
                    }
                    PlayMode.LOOP -> {
                        playerViewModel.shuffleDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_shuffle))
                        playerViewModel.repeatDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_loop))
                    }
                    PlayMode.SHUFFLE_REPEAT -> {
                        playerViewModel.shuffleDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_shuffle_on))
                        playerViewModel.repeatDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_repeat_on))
                        if (playerViewModel.songsLiveData.value!!.size > 1) {
                            playerViewModel.setNextShuffleTrack()
                        }
                    }
                }
            }
        })
        playerViewModel.trackLiked.observe(this, Observer {
            it?.run {
                if (!it) {
                    playerViewModel.heartDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_heart_hollow))
                }
                else {
                    playerViewModel.heartDrawable.postValue(Utils.getDrawable(activity!!, R.drawable.icon_heart))
                }
            }
        })
        playerViewModel.historyTracksUpdated.observe(this, Observer {
            it?.run {
                if (it) {
                    playerViewModel.historyTracksUpdated.postValue(false)
                    sharedViewModel.historyTracksUpdated.postValue(true)
                }
            }
        })

        fragmentPlayerBinding.root.post {
            val mainLayout = (activity as MainActivity).findViewById(R.id.main_panel_layout) as ControlSlidingUpPanelLayout
            mainLayout.addPanelSlideListener(playerViewModel.panelSlideListener)
            fragmentPlayerBinding.mainLayout = mainLayout

            val layoutParams =
                    fragmentPlayerBinding.trackViewPager.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginStart = (fragmentPlayerBinding.playerLayout.width -
                    fragmentPlayerBinding.trackViewPager.width) / 2
            fragmentPlayerBinding.trackViewPager.layoutParams = layoutParams
        }

        playerViewModel.initialService()
        playerViewModel.getSettings()
    }
}