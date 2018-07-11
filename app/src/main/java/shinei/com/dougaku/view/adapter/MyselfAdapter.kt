package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemMyselfBinding
import shinei.com.dougaku.model.Myself
import shinei.com.dougaku.viewModel.MyselfPageModel

class MyselfAdapter(var myselfList: List<Myself>, val myselfPageModel: MyselfPageModel) : RecyclerView.Adapter<MyselfAdapter.MyselfViewHolder>() {

    override fun onBindViewHolder(myselfViewHolder: MyselfViewHolder, position: Int) {
        myselfViewHolder.bind(myselfList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyselfViewHolder {
        val layoutItemMyselfBinding: LayoutItemMyselfBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_myself, parent, false)
        layoutItemMyselfBinding.myselfPageModel = myselfPageModel
        return MyselfViewHolder(layoutItemMyselfBinding)
    }

    override fun getItemCount(): Int {
        return myselfList.size
    }

    class MyselfViewHolder(val layoutItemMyselfBinding: LayoutItemMyselfBinding):
            RecyclerView.ViewHolder(layoutItemMyselfBinding.root) {
        fun bind(myself: Myself) {
            layoutItemMyselfBinding.myself = myself
            layoutItemMyselfBinding.executePendingBindings()
        }
    }
}