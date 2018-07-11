package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemAlbumBinding
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.viewModel.AlbumPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class AlbumAdapter(var albumsList: List<Album>, val albumPageModel: AlbumPageModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onBindViewHolder(albumViewHolder: AlbumViewHolder, position: Int) {
        albumViewHolder.bind(albumsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val layoutItemAlbumBinding: LayoutItemAlbumBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_album, parent, false)
        layoutItemAlbumBinding.albumPageModel = albumPageModel
        return AlbumViewHolder(layoutItemAlbumBinding)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun refresh(albumsList: List<Album>) {
        this.albumsList = albumsList
        notifyDataSetChanged()
    }

    class AlbumViewHolder(val layoutItemAlbumBinding: LayoutItemAlbumBinding):
            RecyclerView.ViewHolder(layoutItemAlbumBinding.root) {
        fun bind(album: Album, sharedViewModel: SharedViewModel) {
            layoutItemAlbumBinding.album = album
            layoutItemAlbumBinding.sharedViewModel = sharedViewModel
            layoutItemAlbumBinding.executePendingBindings()
        }
    }
}