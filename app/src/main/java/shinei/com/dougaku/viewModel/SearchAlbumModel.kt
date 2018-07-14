package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import javax.inject.Inject

class SearchAlbumModel @Inject constructor(val application: Application,
                                           val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val albumsLiveData = MutableLiveData<List<Album>>()

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

    fun searchAlbums(keyword: String) {
        compositeDisposable.add(dougakuRepository.searchAlbums(keyword)
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    albumsLiveData.postValue(it)
                }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application)}))
    }

    fun intentToAlbumDetail(view: View, album: Album, selectedAlbum: MutableLiveData<Album>) {
        selectedAlbum.postValue(album)
        Utils.addFrameFragment(view, AlbumDetailFragment())
    }
}