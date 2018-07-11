package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemProducerBinding
import shinei.com.dougaku.model.Producer
import shinei.com.dougaku.viewModel.ProducerPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ProducerAdapter(var producersList: List<Producer>, val producerPageModel: ProducerPageModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ProducerAdapter.ProducerViewHolder>() {

    override fun onBindViewHolder(producerViewHolder: ProducerViewHolder, position: Int) {
        producerViewHolder.bind(producersList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducerViewHolder {
        val layoutItemProducerBinding: LayoutItemProducerBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_producer, parent, false)
        layoutItemProducerBinding.producerPageModel = producerPageModel
        return ProducerViewHolder(layoutItemProducerBinding)
    }

    override fun getItemCount(): Int {
        return producersList.size
    }

    fun refresh(producersList: List<Producer>) {
        this.producersList = producersList
        notifyDataSetChanged()
    }

    class ProducerViewHolder(val layoutItemProducerBinding: LayoutItemProducerBinding):
            RecyclerView.ViewHolder(layoutItemProducerBinding.root) {
        fun bind(producer: Producer, sharedViewModel: SharedViewModel) {
            layoutItemProducerBinding.producer = producer
            layoutItemProducerBinding.albumCounts = producer.albumCounts.toString() +
                    layoutItemProducerBinding.root.resources.getString(R.string.albums)
            layoutItemProducerBinding.sharedViewModel = sharedViewModel
            layoutItemProducerBinding.executePendingBindings()
        }
    }
}