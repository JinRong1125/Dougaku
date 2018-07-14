package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.DialogInterface
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.AlbumId
import shinei.com.dougaku.model.ArtistName
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.LikedTracksDao
import shinei.com.dougaku.room.MyPlaylists
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import shinei.com.dougaku.view.fragment.ArtistDetailFragment
import java.util.*
import javax.inject.Inject

class MyPlaylistDetailModel @Inject constructor(val application: Application,
                                                val dougakuRepository: DougakuRepository,
                                                val likedTracksDao: LikedTracksDao,
                                                val myPlaylistsDao: MyPlaylistsDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val playlistLiveData = MutableLiveData<MyPlaylists>()
    val songsLiveData = MutableLiveData<List<Song>>()

    val refreshing = MutableLiveData<Boolean>()
    val displayAlpha = MutableLiveData<Float>()
    val toolbarTitleAlpha = MutableLiveData<Float>()
    val toolbarTitleVisibility = MutableLiveData<Int>()
    val emptyImageVisibility = MutableLiveData<Int>()

    init {
        refreshing.postValue(false)
        displayAlpha.postValue(1f)
        toolbarTitleAlpha.postValue(0f)
        toolbarTitleVisibility.postValue(View.GONE)
        emptyImageVisibility.postValue(View.GONE)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onBackPressed(view: View) {
        (view.context as MainActivity).supportFragmentManager.popBackStack()
    }

    fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val ratio = (Math.abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange.toFloat())
        toolbarTitleAlpha.postValue(ratio)
        displayAlpha.postValue(1 - ratio)
    }

    fun getMyPlaylist(title: String) {
        compositeDisposable.add(myPlaylistsDao.getMyPlaylist(title)
                .compose(RxSchedulersHelper.singleIoToMain())
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    playlistLiveData.postValue(it)
                }, {
                    playlistLiveData.postValue(null)
                    refreshing.postValue(false)
                }))
    }

    fun optionPopupMenu(view: View, sharedViewModel: SharedViewModel) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_remove_playlist))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> Utils.deleteMyPlaylists(view.context, compositeDisposable, myPlaylistsDao, playlistLiveData.value!!, sharedViewModel)
            }
            true
        }
        popupMenu.show()
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, dougakuRepository, likedTracksDao, myPlaylistsDao, sharedViewModel, song, refreshing)
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}