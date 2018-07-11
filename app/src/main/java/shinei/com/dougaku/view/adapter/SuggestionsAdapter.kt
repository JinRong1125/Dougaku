package shinei.com.dougaku.view.adapter

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemSearchHistoryBinding
import shinei.com.dougaku.viewModel.SearchViewModel

class SuggestionsAdapter(context: Context, cursor: Cursor, val searchViewModel: SearchViewModel): CursorAdapter(context, cursor, true) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val layoutItemSearchHistoryBinding: LayoutItemSearchHistoryBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.layout_item_search_history, parent, false)
        layoutItemSearchHistoryBinding.searchViewModel = searchViewModel
        return layoutItemSearchHistoryBinding.root
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val layoutItemSearchHistoryBinding = DataBindingUtil.getBinding<LayoutItemSearchHistoryBinding>(view!!)
        layoutItemSearchHistoryBinding?.keyword = cursor?.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
    }
}