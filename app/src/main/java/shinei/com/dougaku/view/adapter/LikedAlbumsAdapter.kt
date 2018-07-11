package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemLikedAlbumBinding
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.viewModel.LikedAlbumsModel
import shinei.com.dougaku.viewModel.SharedViewModel

class LikedAlbumsAdapter(var albumsList: List<Album>, val likedAlbumsModel: LikedAlbumsModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<LikedAlbumsAdapter.AlbumViewHolder>() {

    override fun onBindViewHolder(albumViewHolder: AlbumViewHolder, position: Int) {
        albumViewHolder.bind(albumsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val layoutItemLikedAlbumBinding: LayoutItemLikedAlbumBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_liked_album, parent, false)
        layoutItemLikedAlbumBinding.likedAlbumsModel = likedAlbumsModel
        return AlbumViewHolder(layoutItemLikedAlbumBinding)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun refresh(albumsList: List<Album>) {
        this.albumsList = albumsList
        notifyDataSetChanged()
    }

    class AlbumViewHolder(val layoutItemLikedAlbumBinding: LayoutItemLikedAlbumBinding):
            RecyclerView.ViewHolder(layoutItemLikedAlbumBinding.root) {
        fun bind(album: Album, sharedViewModel: SharedViewModel) {
            layoutItemLikedAlbumBinding.album = album
            layoutItemLikedAlbumBinding.sharedViewModel = sharedViewModel
            layoutItemLikedAlbumBinding.executePendingBindings()
        }
    }
}