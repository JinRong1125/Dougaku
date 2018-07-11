package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.view.fragment.ArtistDetailFragment
import javax.inject.Inject

class ArtistPageModel @Inject constructor(val application: Application,
                                          val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val artistsLiveData = MutableLiveData<List<Artist>>()
    val refreshing = MutableLiveData<Boolean>()

    init {
        refreshing.postValue(true)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun loadArtists() {
        compositeDisposable.add(dougakuRepository.loadArtists()
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({ artistsLiveData.postValue(it) }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application)}))
    }

    fun intentToArtistDetail(view: View, artist: Artist, selectedArtist: MutableLiveData<Artist>) {
        selectedArtist.postValue(artist)
        Utils.addFrameFragment(view, ArtistDetailFragment())
    }
}