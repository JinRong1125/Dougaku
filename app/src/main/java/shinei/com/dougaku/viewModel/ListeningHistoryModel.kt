package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import shinei.com.dougaku.R
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.HistoryTracksDao
import shinei.com.dougaku.room.LikedTracksDao
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.activity.MainActivity
import java.util.*
import javax.inject.Inject

class ListeningHistoryModel @Inject constructor(val application: Application,
                                                val likedTracksDao: LikedTracksDao,
                                                val myPlaylistsDao: MyPlaylistsDao,
                                                val historyTracksDao: HistoryTracksDao): ViewModel() {

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

    fun getHistoryTracks() {
        compositeDisposable.add(historyTracksDao.getHistoryTracks()
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

    fun optionPopupMenu(view: View, sharedViewModel: SharedViewModel) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_clear_listening_history))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> Utils.deleteHistoryTracks(view.context, compositeDisposable, historyTracksDao, sharedViewModel)
            }
            true
        }
        popupMenu.show()
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, likedTracksDao, myPlaylistsDao, sharedViewModel, song)
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}