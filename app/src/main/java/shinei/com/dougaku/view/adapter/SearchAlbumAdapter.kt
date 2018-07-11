package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemSearchAlbumBinding
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.viewModel.SearchAlbumModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchAlbumAdapter(var albumsList: List<Album>, val searchAlbumModel: SearchAlbumModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<SearchAlbumAdapter.AlbumViewHolder>() {

    override fun onBindViewHolder(albumViewHolder: AlbumViewHolder, position: Int) {
        albumViewHolder.bind(albumsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val layoutItemSearchAlbumBinding: LayoutItemSearchAlbumBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_search_album, parent, false)
        layoutItemSearchAlbumBinding.searchAlbumModel = searchAlbumModel
        return AlbumViewHolder(layoutItemSearchAlbumBinding)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun refresh(albumsList: List<Album>) {
        this.albumsList = albumsList
        notifyDataSetChanged()
    }

    class AlbumViewHolder(val layoutItemSearchAlbumBinding: LayoutItemSearchAlbumBinding):
            RecyclerView.ViewHolder(layoutItemSearchAlbumBinding.root) {
        fun bind(album: Album, sharedViewModel: SharedViewModel) {
            layoutItemSearchAlbumBinding.album = album
            layoutItemSearchAlbumBinding.sharedViewModel = sharedViewModel
            layoutItemSearchAlbumBinding.executePendingBindings()
        }
    }
}