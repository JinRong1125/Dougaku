package shinei.com.dougaku.view.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentSearchBinding
import shinei.com.dougaku.view.adapter.SearchPagerAdapter
import shinei.com.dougaku.view.adapter.SuggestionsAdapter
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.SearchViewModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchFragment: FrameFragment() {

    lateinit var fragmentSearchBinding: FragmentSearchBinding
    lateinit var searchViewModel: SearchViewModel
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        fragmentSearchBinding.setLifecycleOwner(this)
        searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
        fragmentSearchBinding.searchViewModel = searchViewModel
        return fragmentSearchBinding.root
    }

    override fun onPause() {
        super.onPause()
        fragmentSearchBinding.searchToolbarLayout!!.searchView.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.enteredKeyword.postValue(null)
        val searchViewPager = fragmentSearchBinding.searchViewPager
        (searchViewPager.adapter as SearchPagerAdapter).destroyAllItems(searchViewPager, activity!!.supportFragmentManager)
    }

    @SuppressLint("RestrictedApi")
    override fun setUp() {
        sharedViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(SharedViewModel::class.java)
        fragmentSearchBinding.searchViewPager.adapter =
                SearchPagerAdapter(context!!, activity!!.supportFragmentManager)
        fragmentSearchBinding.searchTabLayout.setupWithViewPager(
                fragmentSearchBinding.searchViewPager, true)
        val searchView = fragmentSearchBinding.searchToolbarLayout!!.searchView
        searchView.setOnQueryTextListener(searchViewModel.onQueryTextListener)
        val searchAutoCompleteTextView = searchView.findViewById(R.id.search_src_text) as SearchView.SearchAutoComplete
        searchAutoCompleteTextView.threshold = 0

        searchViewModel.historyCursorLiveData.observe(this, Observer {
            it?.run {
                searchView.suggestionsAdapter = SuggestionsAdapter(context!!, it, searchViewModel)
                if (searchView.hasFocus())
                    searchAutoCompleteTextView.performClick()
            }
        })
        searchViewModel.queryLiveData.observe(this, Observer {
            it?.run {
                searchView.setQuery(it.keyword, it.submit)
            }
        })
        searchViewModel.keywordLiveData.observe(this, Observer {
            it?.run {
                searchView.clearFocus()
                sharedViewModel.enteredKeyword.postValue(it)
                searchViewModel.getSearchHistory(it)
            }
        })

        searchView.onActionViewExpanded()
        searchViewModel.getSearchHistories()
    }
}