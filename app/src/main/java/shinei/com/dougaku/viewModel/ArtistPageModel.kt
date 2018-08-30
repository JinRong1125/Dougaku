package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.data.ArtistDataSource
import shinei.com.dougaku.data.ArtistDataSourceFactory
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.view.fragment.ArtistDetailFragment
import javax.inject.Inject

class ArtistPageModel @Inject constructor(val application: Application,
                                          val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    var artistDataSourceFactory: ArtistDataSourceFactory

    var artistsLiveData: LiveData<PagedList<Artist>>

    init {
        artistDataSourceFactory = ArtistDataSourceFactory(application, dougakuRepository, compositeDisposable)
        artistsLiveData = loadArtists()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun loadArtists(): LiveData<PagedList<Artist>> {
        val config = PagedList.Config.Builder()
                .setPageSize(12)
                .setInitialLoadSizeHint(6)
                .setEnablePlaceholders(false)
                .build()
        return LivePagedListBuilder<Long, Artist>(artistDataSourceFactory, config).build()
    }

    fun refresh() {
        artistDataSourceFactory.artistDataSourceLiveData.value!!.invalidate()
    }

    fun getInitialLoad(): LiveData<Boolean> = Transformations.switchMap<ArtistDataSource, Boolean>(
            artistDataSourceFactory.artistDataSourceLiveData, { it.initialLoading })

    fun getAfterLoad(): LiveData<Boolean> = Transformations.switchMap<ArtistDataSource, Boolean>(
            artistDataSourceFactory.artistDataSourceLiveData, { it.afterLoading })

    fun intentToArtistDetail(view: View, artist: Artist, selectedArtist: MutableLiveData<Artist>) {
        selectedArtist.postValue(artist)
        Utils.addFrameFragment(view, ArtistDetailFragment())
    }
}