package shinei.com.dougaku.view.base

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import net.skoumal.fragmentback.BackFragment
import shinei.com.dougaku.di.Injectable
import javax.inject.Inject

abstract class FrameFragment: Fragment(), BackFragment, Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    override fun onBackPressed(): Boolean {
        activity!!.supportFragmentManager.popBackStack()
        return true
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    abstract fun setUp()
}