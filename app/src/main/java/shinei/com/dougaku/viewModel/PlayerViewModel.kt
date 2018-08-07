package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.*
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.*
import shinei.com.dougaku.model.AlbumId
import shinei.com.dougaku.model.ArtistName
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.*
import shinei.com.dougaku.service.PlayerService
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import shinei.com.dougaku.view.fragment.ArtistDetailFragment
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerViewModel @Inject constructor(val application: Application,
                                          val dougakuRepository: DougakuRepository,
                                          val likedTracksDao: LikedTracksDao,
                                          val myPlaylistsDao: MyPlaylistsDao,
                                          val historyTracksDao: HistoryTracksDao,
                                          val settingsDao: SettingsDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()
    var disposable: Disposable? = null

    val settingsLiveData = MutableLiveData<Settings>()
    val songsLiveData = MutableLiveData<List<Song>>()
    val songLiveData = MutableLiveData<Song>()
    val currentTrack = MutableLiveData<Int>()
    val playModel = MutableLiveData<PlayMode>()

    val panelState = MutableLiveData<SlidingUpPanelLayout.PanelState>()
    val playerToolbarAlpha = MutableLiveData<Float>()
    val bottomPlayerAlpha = MutableLiveData<Float>()
    val playerToolbarVisibility = MutableLiveData<Int>()
    val bottomPlayerVisibility = MutableLiveData<Int>()
    val progressBarLayoutVisibility = MutableLiveData<Int>()
    val playListOpened = MutableLiveData<Boolean>()

    val playpauseDrawable = MutableLiveData<Drawable>()
    val buttomPlaypauseDrawable = MutableLiveData<Drawable>()
    val heartDrawable = MutableLiveData<Drawable>()
    val shuffleDrawable = MutableLiveData<Drawable>()
    val repeatDrawable = MutableLiveData<Drawable>()
    val durationSeconds = MutableLiveData<Int>()
    val positionSeconds = MutableLiveData<Int>()
    val trackLiked = MutableLiveData<Boolean>()
    val historyTracksUpdated = MutableLiveData<Boolean>()

    var likedTrackId = 0

    var shuffleList = emptyList<Int>()
    var currentShuffle = 0
    var firstTrack = 0
    var shuffleTrack = 0

    var previousTrack = 0
    var updateTrack = 0
    var haveSettings = false
    var isUIUnUpdated = false
    var isSeeking = false
    var isShuffling = false

    init {
        playModel.postValue(PlayMode.NORMAL)
        playerToolbarAlpha.postValue(0f)
        bottomPlayerAlpha.postValue(1f)
        playerToolbarVisibility.postValue(View.GONE)
        bottomPlayerVisibility.postValue(View.VISIBLE)
        progressBarLayoutVisibility.postValue(View.GONE)
        playListOpened.postValue(false)
        playpauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_play))
        buttomPlaypauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_play))
        heartDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_heart_hollow))
        shuffleDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_shuffle))
        repeatDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_repeat))
        durationSeconds.postValue(0)
        positionSeconds.postValue(0)
        trackLiked.postValue(false)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    val panelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View, slideOffset: Float) {
            when {
                slideOffset > 0.5f -> {
                    playerToolbarVisibility.postValue(View.VISIBLE)
                    bottomPlayerVisibility.postValue(View.GONE)
                    playerToolbarAlpha.postValue((slideOffset - 0.5f) * 2f)
                }
                slideOffset < 0.5f -> {
                    playerToolbarVisibility.postValue(View.GONE)
                    bottomPlayerVisibility.postValue(View.VISIBLE)
                    bottomPlayerAlpha.postValue((0.5f - slideOffset) * 2f)
                }
                else -> {
                    playerToolbarVisibility.postValue(View.GONE)
                    bottomPlayerVisibility.postValue(View.GONE)
                    playerToolbarAlpha.postValue(0f)
                    bottomPlayerAlpha.postValue(0f)
                }
            }
        }

        override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState?) {
            if (newState != SlidingUpPanelLayout.PanelState.DRAGGING)
                panelState.postValue(newState)
        }
    }

    val onPageChangeListener = object: ViewPager.OnPageChangeListener {

        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            prepareIsTrackRepeated(position)
        }
    }

    val playerServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(componetName: ComponentName, service: IBinder) {
            val playerService = (service as PlayerService.PlayerBinder).getService()
            playerService.setPlayerListner(playerListener)
        }

        override fun onServiceDisconnected(className: ComponentName) {

        }
    }

    val playerListener = object: PlayerService.PlayerListener {
        override fun onPlay() {
            playpauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_pause))
            buttomPlaypauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_pause))
        }

        override fun onPause() {
            playpauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_play))
            buttomPlaypauseDrawable.postValue(Utils.getDrawable(application, R.drawable.icon_play))
        }

        override fun onBuffering() {}

        override fun onReady(duration: Int) {
            durationSeconds.postValue(duration)
        }

        override fun onEnded() {
            executePlayMode()
        }

        override fun onProgressUpdated(position: Int) {
            if (!isSeeking) {
                positionSeconds.postValue(position)
            }
        }

        override fun onNextTrack() {
            if (currentTrack.value!! < songsLiveData.value!!.size - 1)
                prepareTrack(currentTrack.value!! + 1)

        }

        override fun onPreviousTrack() {
            if (currentTrack.value!! > 0)
                prepareTrack(currentTrack.value!! - 1)
        }
    }

    fun initialService() {
        application.bindService(Intent(application, PlayerService::class.java),
                playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun getSettings() {
        compositeDisposable.add(settingsDao.getSettings(0)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    settingsLiveData.postValue(it)
                }, {}))
    }

    fun insertSettings() {
        compositeDisposable.add(Completable.fromAction {
            settingsDao.insertSettings(Settings(0, playModel.value!!, songsLiveData.value!!, currentTrack.value!!)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe())
    }

    fun prepareIsTrackRepeated(targetTrack: Int) {
        if (previousTrack != targetTrack)
            prepareTrack(targetTrack)
    }

    fun prepareTrack(targetTrack: Int) {
        delayLoadTrack(songsLiveData.value!![targetTrack])

        if (!isUIUnUpdated) {
            isUIUnUpdated = true
            updateTrack = previousTrack
        }
        previousTrack = targetTrack
        currentTrack.postValue(targetTrack)
    }

    fun delayLoadTrack(targetSong: Song) {
        Intent(application, PlayerService::class.java).apply {
            action = STOP
            application.startService(this)
        }
        disposable?.dispose()
        disposable = Observable.timer(1000, TimeUnit.MILLISECONDS).subscribe({
            if (!haveSettings) {
                Intent(application, PlayerService::class.java).apply {
                    action = PLAY
                    application.startService(this)
                }
            }
            else {
                haveSettings = false
                playModel.postValue(settingsLiveData.value?.playMode)
                Intent(application, PlayerService::class.java).apply {
                    action = PAUSE
                    application.startService(this)
                }
            }
            Intent(application, PlayerService::class.java).apply {
                action = PREPARE_TRACK
                putExtra(TARGET_SONG, targetSong)
                application.startService(this)
            }
            if (playModel.value == PlayMode.SHUFFLE && !isShuffling)
                createShuffleList()
            else
                isShuffling = false

            getHistoryTrack(targetSong)
        })
    }

    fun getLikedTrack(song: Song) {
        compositeDisposable.add(likedTracksDao.getLikedTrack(song.songId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    trackLiked.postValue(true)
                    likedTrackId = it.id
                }, {
                    trackLiked.postValue(false)
                }))
    }

    fun getHistoryTrack(song: Song) {
        compositeDisposable.add(historyTracksDao.getHistoryTrack(song.songId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    deleteHistoryTracks(it.id, song)
                }, {
                    insertHistoryTracks(song)
                }))
    }

    fun insertHistoryTracks(song: Song) {
        compositeDisposable.add(Completable.fromAction {
            historyTracksDao.insertHistoryTracks(HistoryTracks(0, song.songId, song)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    historyTracksUpdated.postValue(true)
                }))
    }

    fun deleteHistoryTracks(id: Int, song: Song) {
        compositeDisposable.add(Completable.fromAction {
            historyTracksDao.deleteHistoryTracks(HistoryTracks(id, song.songId, song)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    insertHistoryTracks(song)
                }, {
                    insertHistoryTracks(song)
                }))
    }

    fun hide() {
        panelState.postValue(SlidingUpPanelLayout.PanelState.COLLAPSED)
    }

    fun optionPopupMenu(view: View, sharedViewModel: SharedViewModel) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_add_to_playlist))
        popupMenu.menu.add(0, 1, 1, application.getString(R.string.menu_go_to_album))
        popupMenu.menu.add(0, 2, 2, application.getString(R.string.menu_go_to_artist))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 ->
                    Utils.createPlaylistDialog(view.context, compositeDisposable, myPlaylistsDao, sharedViewModel, arrayListOf(songLiveData.value!!))
                1 ->
                    goToAlbum(view, sharedViewModel, songLiveData.value!!.albumId)
                2 -> {
                    val artistList = songLiveData.value!!.artistList
                    if (artistList.size > 1)
                        createArtistDialog(view, sharedViewModel, artistList)
                    else
                        goToArtist(view, sharedViewModel, artistList[0])
                }
            }
            true
        }
        popupMenu.show()
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        val context = view.context
        val popupMenu = PopupMenu(context, view)
        var likedTrack = false
        var likedTrackId = 0

        popupMenu.menu.add(0, 1, 1, context.getString(R.string.menu_add_to_playlist))
        popupMenu.menu.add(0, 2, 2, application.getString(R.string.menu_go_to_album))
        popupMenu.menu.add(0, 3, 3, application.getString(R.string.menu_go_to_artist))
        compositeDisposable.add(likedTracksDao.getLikedTrack(song.songId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    popupMenu.menu.add(0, 0, 0, context.getString(R.string.menu_unlike_track))
                    likedTrack = true
                    likedTrackId = it.id
                    popupMenu.show()
                }, {
                    popupMenu.menu.add(0, 0, 0, context.getString(R.string.menu_like_track))
                    popupMenu.show()
                }))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {
                    if (!likedTrack)
                        Utils.insertLikedTracks(context, compositeDisposable, likedTracksDao, sharedViewModel, song)
                    else
                        Utils.deleteLikedTracks(context, compositeDisposable, likedTracksDao, sharedViewModel, likedTrackId, song)
                }
                1 ->
                    Utils.createPlaylistDialog(context, compositeDisposable, myPlaylistsDao, sharedViewModel, arrayListOf(song))
                2 ->
                    goToAlbum(view, sharedViewModel, song.albumId)
                3 -> {
                    if (song.artistList.size > 1)
                        createArtistDialog(view, sharedViewModel, song.artistList)
                    else
                        goToArtist(view, sharedViewModel, song.artistList[0])
                }
            }
            true
        }
    }

    fun goToAlbum(view: View, sharedViewModel: SharedViewModel, albumId: Int) {
        playListOpened.postValue(false)
        val selectedAlbumValue = sharedViewModel.selectedAlbum.value
        if (selectedAlbumValue != null && selectedAlbumValue.albumId == albumId) {
            Utils.addFrameFragment(view, AlbumDetailFragment())
            hide()
        }
        else {
            compositeDisposable.add(dougakuRepository.loadAlbums(AlbumId(albumId))
                    .doOnSubscribe({
                        progressBarLayoutVisibility.postValue(View.VISIBLE)
                    })
                    .subscribe({
                        progressBarLayoutVisibility.postValue(View.GONE)
                        sharedViewModel.selectedAlbum.postValue(it[0])
                        Utils.addFrameFragment(view, AlbumDetailFragment())
                        hide()
                    }, {
                        progressBarLayoutVisibility.postValue(View.GONE)
                        dougakuRepository.showError(application)
                    }))
        }
    }

    fun goToArtist(view: View, sharedViewModel: SharedViewModel, artistName: String) {
        playListOpened.postValue(false)
        val selectedArtistValue = sharedViewModel.selectedArtist.value
        if (selectedArtistValue != null && selectedArtistValue.name == artistName) {
            Utils.addFrameFragment(view, ArtistDetailFragment())
            hide()
        }
        else {
            compositeDisposable.add(dougakuRepository.loadArtists(ArtistName(artistName))
                    .doOnSubscribe({
                        progressBarLayoutVisibility.postValue(View.VISIBLE)
                    })
                    .subscribe({
                        progressBarLayoutVisibility.postValue(View.GONE)
                        sharedViewModel.selectedArtist.postValue(it[0])
                        Utils.addFrameFragment(view, ArtistDetailFragment())
                        hide()
                    }, {
                        progressBarLayoutVisibility.postValue(View.GONE)
                        dougakuRepository.showError(application)
                    }))
        }
    }

    fun createArtistDialog(view: View, sharedViewModel: SharedViewModel, artistList: List<String>) {
        val context = view.context
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(context.getString(R.string.menu_go_to_artist))
        alertDialog.setItems(artistList.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
            goToArtist(view, sharedViewModel, artistList[which])
        })
        alertDialog.show()
    }

    fun playpause(){
        Intent(application, PlayerService::class.java).apply {
            action = PLAY_PAUSE
            application.startService(this)
        }
    }

    fun preparePause() {
        Intent(application, PlayerService::class.java).apply {
            action = PREPARE_PAUSE
            application.startService(this)
        }
    }

    fun repeat() {
        when (playModel.value!!) {
            PlayMode.NORMAL -> playModel.postValue(PlayMode.REPEAT)
            PlayMode.SHUFFLE -> playModel.postValue(PlayMode.SHUFFLE_REPEAT)
            PlayMode.REPEAT -> playModel.postValue(PlayMode.LOOP)
            PlayMode.LOOP -> playModel.postValue(PlayMode.NORMAL)
            PlayMode.SHUFFLE_REPEAT -> playModel.postValue(PlayMode.LOOP)
        }
    }

    fun shuffle() {
        when (playModel.value!!) {
            PlayMode.NORMAL -> playModel.postValue(PlayMode.SHUFFLE)
            PlayMode.SHUFFLE -> playModel.postValue(PlayMode.NORMAL)
            PlayMode.REPEAT -> playModel.postValue(PlayMode.SHUFFLE_REPEAT)
            PlayMode.LOOP -> playModel.postValue(PlayMode.SHUFFLE_REPEAT)
            PlayMode.SHUFFLE_REPEAT -> playModel.postValue(PlayMode.REPEAT)
        }
    }

    fun createShuffleList() {
        firstTrack = currentTrack.value!!
        if (songsLiveData.value!!.size > 1) {
            val shuffleList = ArrayList<Int>()
            for(i in 0 until songsLiveData.value!!.size) {
                shuffleList.add(i)
            }
            shuffleList.remove(firstTrack)
            shuffleList.shuffle()
            this.shuffleList = shuffleList
            currentShuffle = 0
        }
    }

    fun setNextShuffleTrack() {
        var shuffleTrack = 0
        do {
            shuffleTrack = Random().nextInt(songsLiveData.value!!.size)
        } while (shuffleTrack == currentTrack.value)
        this.shuffleTrack = shuffleTrack
    }

    fun executePlayMode() {
        when (playModel.value!!) {
            PlayMode.NORMAL -> {
                if (currentTrack.value!! < songsLiveData.value!!.size - 1)
                    prepareTrack(currentTrack.value!! + 1)
                else {
                    preparePause()
                    prepareTrack(0)
                    positionSeconds.postValue(0)
                }
            }
            PlayMode.SHUFFLE -> {
                isShuffling = true
                if (songsLiveData.value!!.size > 1 && currentShuffle < shuffleList.size) {
                    prepareTrack(shuffleList[currentShuffle])
                    currentShuffle += 1
                }
                else {
                    preparePause()
                    prepareTrack(firstTrack)
                    positionSeconds.postValue(0)
                    currentShuffle = 0
                }
            }
            PlayMode.REPEAT -> {
                when {
                    currentTrack.value!! < songsLiveData.value!!.size - 1 ->
                        prepareTrack(currentTrack.value!! + 1)
                    songsLiveData.value!!.size > 1 -> prepareTrack(0)
                    else -> seek(0)
                }
            }
            PlayMode.LOOP ->
                seek(0)
            PlayMode.SHUFFLE_REPEAT -> {
                if (songsLiveData.value!!.size > 1) {
                    prepareTrack(shuffleTrack)
                    setNextShuffleTrack()
                }
                else
                    seek(0)
            }
        }
    }

    fun like(view: View, sharedViewModel: SharedViewModel) {
        if (!trackLiked.value!!)
            Utils.insertLikedTracks(view.context, compositeDisposable, likedTracksDao, sharedViewModel, songLiveData.value!!)
        else
            Utils.deleteLikedTracks(view.context, compositeDisposable, likedTracksDao, sharedViewModel, likedTrackId, songLiveData.value!!)
    }

    fun openclosePlayList(Opened: Boolean) {
        playListOpened.postValue(Opened)
    }

    fun playTrack(position: Int) {
        prepareIsTrackRepeated(position)
    }

    fun seek(position: Int) {
        Intent(application, PlayerService::class.java).apply {
            action = SEEK
            putExtra(SEEK_TIME, position)
            application.startService(this)
        }
    }

    fun startTracking() {
        isSeeking = true
    }

    fun onProgressChanged(progress: Int, fromUser: Boolean) {
        if (fromUser)
            positionSeconds.postValue(progress * 1000)
    }

    fun onStopTrackingTouch(seekBar: SeekBar) {
        isSeeking = false
        seek(seekBar.progress * 1000)
    }
}