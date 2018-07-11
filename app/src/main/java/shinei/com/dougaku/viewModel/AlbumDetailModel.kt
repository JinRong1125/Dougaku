package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.*
import shinei.com.dougaku.view.activity.MainActivity
import javax.inject.Inject

class AlbumDetailModel @Inject constructor(val application: Application,
                                           val albumSongsDao: AlbumSongsDao,
                                           val likedTracksDao: LikedTracksDao,
                                           val likedAlbumsDao: LikedAlbumsDao,
                                           val myPlaylistsDao: MyPlaylistsDao,
                                           val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val albumLiveData = MutableLiveData<Album>()
    val songsLiveData = MutableLiveData<List<Song>>()

    val refreshing = MutableLiveData<Boolean>()
    val loadCompleted = MutableLiveData<Boolean>()
    val displayAlpha = MutableLiveData<Float>()
    val toolbarTitleAlpha = MutableLiveData<Float>()
    val toolbarTitleVisibility = MutableLiveData<Int>()

    init {
        refreshing.postValue(false)
        loadCompleted.postValue(false)
        displayAlpha.postValue(1f)
        toolbarTitleAlpha.postValue(0f)
        toolbarTitleVisibility.postValue(View.GONE)
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

    fun getAlbumSongs(album: Album) {
        compositeDisposable.add(albumSongsDao.getAlbumSongs(album.albumId)
                .map { albumSongs -> albumSongs.songsList }
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    songsLiveData.postValue(it)
                }, {
                    loadAlbumSongs(album)
                }))
    }

    fun loadAlbumSongs(album: Album) {
        compositeDisposable.add(dougakuRepository.loadAlbumSongs(album.albumId)
                .doOnSubscribe{ refreshing.postValue(true) }
                .subscribe({
                    songsLiveData.postValue(it)
                    insertAlbumSongs(album, it)
                }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application) }))
    }

    fun insertAlbumSongs(album: Album, songsList: List<Song>) {
        compositeDisposable.add(Completable.fromAction {
            albumSongsDao.insertAlbumSongs(AlbumSongs(album.albumId, songsList)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe())
    }

    fun optionPopupMenu(view: View, sharedViewModel: SharedViewModel) {
        val popupMenu = PopupMenu(view.context, view)
        val album = albumLiveData.value!!
        var likedAlbum = false
        var likedAlbumId = 0

        if (loadCompleted.value!!)
            popupMenu.menu.add(0, 1, 1, application.getString(R.string.menu_add_to_playlist))
        compositeDisposable.add(likedAlbumsDao.getLikedAlbum(album.albumId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_unlike_album))
                    likedAlbum = true
                    likedAlbumId = it.id
                    popupMenu.show()
                }, {
                    popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_like_album))
                    popupMenu.show()
                }))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {
                    if (!likedAlbum)
                        Utils.insertLikedAlbums(view.context, compositeDisposable, likedAlbumsDao, sharedViewModel, album)
                    else
                        Utils.deleteLikedAlbums(view.context, compositeDisposable, likedAlbumsDao, sharedViewModel, likedAlbumId, album)
                }
                1 ->
                    Utils.createPlaylistDialog(view.context, compositeDisposable, myPlaylistsDao, sharedViewModel, ArrayList(songsLiveData.value!!))
            }
            true
        }
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, likedTracksDao, myPlaylistsDao, sharedViewModel, song)
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}