package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemProducerAlbumBinding
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.viewModel.ProducerDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ProducerAlbumAdapter(var albumsList: List<Album>, val producerDetailModel: ProducerDetailModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ProducerAlbumAdapter.ProducerAlbumViewHolder>() {

    override fun onBindViewHolder(albumViewHolder: ProducerAlbumViewHolder, position: Int) {
        albumViewHolder.bind(albumsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProducerAlbumViewHolder {
        val layoutItemProducerAlbumBinding: LayoutItemProducerAlbumBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_producer_album, parent, false)
        layoutItemProducerAlbumBinding.producerDetailModel = producerDetailModel
        return ProducerAlbumViewHolder(layoutItemProducerAlbumBinding)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun refresh(albumsList: List<Album>) {
        this.albumsList = albumsList
        notifyDataSetChanged()
    }

    class ProducerAlbumViewHolder(val layoutItemProducerAlbumBinding: LayoutItemProducerAlbumBinding):
            RecyclerView.ViewHolder(layoutItemProducerAlbumBinding.root) {
        fun bind(album: Album, sharedViewModel: SharedViewModel) {
            layoutItemProducerAlbumBinding.album = album
            layoutItemProducerAlbumBinding.sharedViewModel = sharedViewModel
            layoutItemProducerAlbumBinding.executePendingBindings()
        }
    }
}