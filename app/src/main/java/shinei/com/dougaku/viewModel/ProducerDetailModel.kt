package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.design.widget.AppBarLayout
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.model.Producer
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import javax.inject.Inject

class ProducerDetailModel @Inject constructor(val application: Application,
                                              val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val producerLiveData = MutableLiveData<Producer>()
    val albumsLiveData = MutableLiveData<List<Album>>()

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

    fun loadProducerAlbums(producer: Producer) {
        compositeDisposable.add(dougakuRepository.loadProducerAlbums(producer.producerId).retry(3)
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({ albumsLiveData.postValue(it) }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application) }))
    }

    fun intentToAlbumDetail(view: View, album: Album, selectedAlbum: MutableLiveData<Album>) {
        selectedAlbum.postValue(album)
        Utils.addFrameFragment(view, AlbumDetailFragment())
    }
}