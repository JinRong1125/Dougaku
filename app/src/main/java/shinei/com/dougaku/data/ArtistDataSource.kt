package shinei.com.dougaku.data

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.ItemKeyedDataSource
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.api.parameter.ArtistItem
import shinei.com.dougaku.model.Artist

class ArtistDataSource(val dougakuRepository: DougakuRepository,
                       val compositeDisposable: CompositeDisposable): ItemKeyedDataSource<Long, Artist>() {

    var queryKey: Long = 0

    val initialLoading = MutableLiveData<Boolean>()

    val afterLoading = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Artist>) {
        compositeDisposable.add(
                dougakuRepository.loadArtists(ArtistItem(1, params.requestedLoadSize))
                        .doOnSubscribe({ initialLoading.postValue(true) })
                        .subscribe({
                            initialLoading.postValue(false)
                            callback.onResult(it)
                        }, {
                            initialLoading.postValue(false)
                        }))
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Artist>) {
        val startPoint = params.key + 1
        if (queryKey != startPoint) {
            queryKey = startPoint
            compositeDisposable.add(
                    dougakuRepository.loadArtists(ArtistItem(startPoint, params.requestedLoadSize))
                            .doOnSubscribe({ afterLoading.postValue(true) })
                            .subscribe({
                                afterLoading.postValue(false)
                                callback.onResult(it)
                            }, {
                                afterLoading.postValue(false)
                            }))
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Artist>) {

    }

    override fun getKey(item: Artist): Long {
        return item.artistId.toLong()
    }
}