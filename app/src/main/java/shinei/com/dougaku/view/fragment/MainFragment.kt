package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentMainBinding
import shinei.com.dougaku.view.adapter.MainPagerAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.MainViewModel

class MainFragment: FrameFragment() {

    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        fragmentMainBinding.setLifecycleOwner(this)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        fragmentMainBinding.mainViewModel = mainViewModel
        return fragmentMainBinding.root
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun setUp() {
        fragmentMainBinding.mainViewPager.adapter =
                MainPagerAdapter(context!!, activity!!.supportFragmentManager)
        fragmentMainBinding.mainTabLayout.setupWithViewPager(
                fragmentMainBinding.mainViewPager, true)
    }
}