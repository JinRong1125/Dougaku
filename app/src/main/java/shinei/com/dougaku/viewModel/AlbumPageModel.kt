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

class AlbumPageModel @Inject constructor(val application: Application,
                                         val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val albumsLiveData = MutableLiveData<List<Album>>()
    val refreshing = MutableLiveData<Boolean>()

    init {
        refreshing.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun loadAlbums() {
        compositeDisposable.add(dougakuRepository.loadAlbums().retry(3)
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