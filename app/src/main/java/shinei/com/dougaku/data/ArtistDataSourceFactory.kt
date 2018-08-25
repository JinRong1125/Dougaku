package shinei.com.dougaku.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.model.Artist

class ArtistDataSourceFactory(val dougakuRepository: DougakuRepository,
                             val compositeDisposable: CompositeDisposable) : DataSource.Factory<Long, Artist>() {

    val artistDataSourceLiveData = MutableLiveData<ArtistDataSource>()

    override fun create(): DataSource<Long, Artist> {
        val artistDataSource = ArtistDataSource(dougakuRepository, compositeDisposable)
        artistDataSourceLiveData.postValue(artistDataSource)
        return artistDataSource
    }
}