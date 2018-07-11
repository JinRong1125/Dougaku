package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemSearchProducerBinding
import shinei.com.dougaku.model.Producer
import shinei.com.dougaku.viewModel.SearchProducerModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchProducerAdapter(var producersList: List<Producer>, val searchProducerModel: SearchProducerModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<SearchProducerAdapter.ProducerViewHolder>() {

    override fun onBindViewHolder(producerViewHolder: ProducerViewHolder, position: Int) {
        producerViewHolder.bind(producersList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducerViewHolder {
        val layoutItemSearchProducerBinding: LayoutItemSearchProducerBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_search_producer, parent, false)
        layoutItemSearchProducerBinding.searchProducerModel = searchProducerModel
        return ProducerViewHolder(layoutItemSearchProducerBinding)
    }

    override fun getItemCount(): Int {
        return producersList.size
    }

    fun refresh(producersList: List<Producer>) {
        this.producersList = producersList
        notifyDataSetChanged()
    }

    class ProducerViewHolder(val layoutItemSearchProducerBinding: LayoutItemSearchProducerBinding):
            RecyclerView.ViewHolder(layoutItemSearchProducerBinding.root) {
        fun bind(producer: Producer, sharedViewModel: SharedViewModel) {
            layoutItemSearchProducerBinding.producer = producer
            layoutItemSearchProducerBinding.albumCounts = producer.albumCounts.toString() +
                    layoutItemSearchProducerBinding.root.resources.getString(R.string.albums)
            layoutItemSearchProducerBinding.sharedViewModel = sharedViewModel
            layoutItemSearchProducerBinding.executePendingBindings()
        }
    }
}