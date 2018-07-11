package shinei.com.dougaku.viewModel

import android.app.SearchManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.support.v7.widget.SearchView
import android.view.View
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.model.Query
import shinei.com.dougaku.room.SearchHistories
import shinei.com.dougaku.room.SearchHistoriesDao
import shinei.com.dougaku.view.activity.MainActivity
import javax.inject.Inject

class SearchViewModel @Inject constructor(val searchHistoriesDao: SearchHistoriesDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val historyCursorLiveData = MutableLiveData<Cursor>()
    val queryLiveData = MutableLiveData<Query>()
    val keywordLiveData = MutableLiveData<String>()

    fun onBackPressed(view: View) {
        (view.context as MainActivity).supportFragmentManager.popBackStack()
    }

    val onQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            if (!query.trim().isEmpty())
                keywordLiveData.postValue(query)
            return false
        }

        override fun onQueryTextChange(newText: String): Boolean {
            return false
        }
    }

    fun getSearchHistories() {
        compositeDisposable.add(searchHistoriesDao.getSearchHistories()
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                    for (i in it.size - 1 downTo 0) {
                        matrixCursor.addRow(arrayOf(it[i].id, it[i].keyword))
                    }
                    historyCursorLiveData.postValue(matrixCursor)
                }, {
                }))
    }

    fun getSearchHistory(keyword: String) {
        compositeDisposable.add(searchHistoriesDao.getSearchHistory(keyword)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    deleteSearchHistories(it.id, keyword)
                }, {
                    insertSearchHistories(keyword)
                }))
    }

    fun insertSearchHistories(keyword: String) {
        compositeDisposable.add(Completable.fromAction {
            searchHistoriesDao.insertSearchHistories(SearchHistories(0, keyword)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    getSearchHistories()
                }))
    }

    fun deleteSearchHistories(id: Int, keyword: String) {
        compositeDisposable.add(Completable.fromAction {
            searchHistoriesDao.deleteSearchHistories(SearchHistories(id, keyword)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    insertSearchHistories(keyword)
                }, {
                    insertSearchHistories(keyword)
                }))
    }

    fun search(keyword: String) {
        queryLiveData.postValue(Query(keyword, true))
    }

    fun inputKeyword(keyword: String) {
        queryLiveData.postValue(Query(keyword, false))
    }
}