package shinei.com.dougaku.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.view.fragment.SearchFragment
import javax.inject.Inject

class MainActivityModel @Inject constructor(): ViewModel() {

    val containerEnabled = MutableLiveData<Boolean>()
}