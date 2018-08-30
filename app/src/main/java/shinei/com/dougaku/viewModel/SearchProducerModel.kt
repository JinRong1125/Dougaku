package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Producer
import shinei.com.dougaku.view.fragment.ProducerDetailFragment
import javax.inject.Inject

class SearchProducerModel @Inject constructor(val application: Application,
                                              val dougakuRepository: DougakuRepository): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val producersLiveData = MutableLiveData<List<Producer>>()

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

    fun searchProducers(keyword: String) {
        compositeDisposable.add(dougakuRepository.searchProducers(keyword).retry(3)
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    producersLiveData.postValue(it)
                }, {
                    refreshing.postValue(false)
                    dougakuRepository.showError(application)}))
    }

    fun intentToProducerDetail(view: View, producer: Producer, selectedProducer: MutableLiveData<Producer>) {
        selectedProducer.postValue(producer)
        Utils.addFrameFragment(view, ProducerDetailFragment())
    }
}