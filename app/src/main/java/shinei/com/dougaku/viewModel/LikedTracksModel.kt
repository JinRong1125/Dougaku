package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.LikedTracksDao
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.activity.MainActivity
import java.util.*
import javax.inject.Inject

class LikedTracksModel @Inject constructor(val application: Application,
                                           val dougakuRepository: DougakuRepository,
                                           val likedTracksDao: LikedTracksDao,
                                           val myPlaylistsDao: MyPlaylistsDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val songsLiveData = MutableLiveData<List<Song>>()

    val refreshing = MutableLiveData<Boolean>()
    val emptyImageVisibility = MutableLiveData<Int>()

    init {
        refreshing.postValue(false)
        emptyImageVisibility.postValue(View.GONE)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onBackPressed(view: View) {
        (view.context as MainActivity).supportFragmentManager.popBackStack()
    }

    fun getLikedTracks() {
        compositeDisposable.add(likedTracksDao.getLikedTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    val songsList = ArrayList<Song>()
                    for (i in it.size - 1 downTo 0) {
                        songsList.add(it[i].song)
                    }
                    songsLiveData.postValue(songsList)
                }, {
                    refreshing.postValue(false)
                }))
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, dougakuRepository, likedTracksDao, myPlaylistsDao, sharedViewModel, song, refreshing)
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}