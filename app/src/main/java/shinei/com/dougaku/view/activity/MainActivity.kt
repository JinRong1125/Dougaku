package shinei.com.dougaku.view.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import net.skoumal.fragmentback.BackFragmentHelper
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.ActivityMainBinding
import shinei.com.dougaku.view.fragment.MainFragment
import shinei.com.dougaku.viewModel.MainActivityModel
import shinei.com.dougaku.viewModel.SharedViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var mainActivityModel: MainActivityModel

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.setLifecycleOwner(this)
        mainActivityModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityModel::class.java)
        activityMainBinding.mainActivityModel = mainActivityModel

        setUp()
    }

    override fun onBackPressed() {
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            moveTaskToBack(true)
        }
    }

    fun setUp() {
        val sharedViewModel = ViewModelProviders.of(this, viewModelFactory).get(SharedViewModel::class.java)
        sharedViewModel.isTouchEnabled.observe(this, Observer {
            it?.run {
                activityMainBinding.mainPanelLayout.isTouchEnabled = it
            }
        })
        sharedViewModel.collapsePlayer.observe(this, Observer {
            it?.run {
                activityMainBinding.mainPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        })

        val panelSlideListener = object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                sharedViewModel.panelSlideOffset.postValue(slideOffset)
            }

            override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState?) {
                sharedViewModel.panelState.postValue(newState)
            }
        }
        activityMainBinding.mainPanelLayout.addPanelSlideListener(panelSlideListener)
        sharedViewModel.panelState.postValue(SlidingUpPanelLayout.PanelState.HIDDEN)

        supportFragmentManager.beginTransaction().add(
                R.id.main_container, MainFragment()).commit()
    }
}
