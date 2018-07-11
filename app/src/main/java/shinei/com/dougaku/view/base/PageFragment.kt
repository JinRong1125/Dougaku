package shinei.com.dougaku.view.base

import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.Fragment
import shinei.com.dougaku.di.Injectable
import javax.inject.Inject

abstract class PageFragment: Fragment(), Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    var initialized = false
    var loaded = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        load()
    }

    abstract fun setUp()

    fun load() {
        if (!loaded && initialized && userVisibleHint) {
            loaded = true
            setUp()
        }
    }
}