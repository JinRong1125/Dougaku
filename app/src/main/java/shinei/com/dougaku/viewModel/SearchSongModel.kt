package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.AlbumId
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.LikedTracksDao
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import javax.inject.Inject

class SearchSongModel @Inject constructor(val application: Application,
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

    fun searchSongs(keyword: String) {
        compositeDisposable.add(dougakuRepository.searchSongs(keyword)
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    songsLiveData.postValue(it)
                }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application)}))
    }

    fun trackPopupMenu(view: View, sharedViewModel: SharedViewModel, song: Song) {
        Utils.createTrackPopupMenu(view, compositeDisposable, dougakuRepository, likedTracksDao, myPlaylistsDao, sharedViewModel, song, refreshing)
    }

    fun intentToPlayer(position: Int, sharedViewModel: SharedViewModel) {
        sharedViewModel.selectedSongs.postValue(songsLiveData.value)
        sharedViewModel.selectedTrack.postValue(position)
    }
}