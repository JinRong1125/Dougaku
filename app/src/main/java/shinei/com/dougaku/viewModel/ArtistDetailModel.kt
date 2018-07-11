package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.LikedTracksDao
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import java.util.*
import javax.inject.Inject

class ArtistDetailModel @Inject constructor(val application: Application,
                                            val likedTracksDao: LikedTracksDao,
                                            val myPlaylistsDao: MyPlaylistsDao,
                                            val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val artistLiveData = MutableLiveData<Artist>()
    val albumsLiveData = MutableLiveData<List<Album>>()
    val songsLiveData = MutableLiveData<List<Song>>()

    val refreshing = MutableLiveData<Boolean>()
    val loadCompleted = MutableLiveData<Boolean>()
    val displayAlpha = MutableLiveData<Float>()
    val toolbarNameAlpha = MutableLiveData<Float>()
    val toolbarNameVisibility = MutableLiveData<Int>()

    init {
        refreshing.postValue(false)
        loadCompleted.postValue(false)
        displayAlpha.postValue(1f)
        toolbarNameAlpha.postValue(0f)
        toolbarNameVisibility.postValue(View.GONE)
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
        toolbarNameAlpha.postValue(ratio)
        displayAlpha.postValue(1 - ratio)
    }

    fun loadArtistSongsAlbums(artist: Artist) {
        compositeDisposable.add(dougakuRepository.loadArtistSongsAlbums(artist.artistId)
                .doOnSubscribe{ refreshing.postValue(true) }
                .subscribe({
                    albumsLiveData.postValue(it.albumsList)
                    songsLiveData.postValue(it.songsList)
                }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application) }))
    }

    fun optionPopupMenu(view: View, sharedViewModel: SharedViewModel) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_add_to_playlist))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 ->
                    Utils.createPlaylistDialog(view.context, compositeDisposable, myPlaylistsDao, sharedViewModel, ArrayList(songsLiveData.value!!))
            }
            true
        }
        popupMenu.show()
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, likedTracksDao, myPlaylistsDao, sharedViewModel, song)
    }

    fun intentToAlbumDetail(view: View, album: Album, selectedAlbum: MutableLiveData<Album>) {
        selectedAlbum.postValue(album)
        Utils.addFrameFragment(view, AlbumDetailFragment())
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}