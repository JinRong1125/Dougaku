package shinei.com.dougaku.data

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.model.Artist

class ArtistDataSourceFactory(val application: Application,
                              val dougakuRepository: DougakuRepository,
                              val compositeDisposable: CompositeDisposable) : DataSource.Factory<Long, Artist>() {

    val artistDataSourceLiveData = MutableLiveData<ArtistDataSource>()

    override fun create(): DataSource<Long, Artist> {
        val artistDataSource = ArtistDataSource(application, dougakuRepository, compositeDisposable)
        artistDataSourceLiveData.postValue(artistDataSource)
        return artistDataSource
    }
}