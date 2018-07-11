package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemArtistAlbumBinding
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.viewModel.ArtistDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistAlbumAdapter(var albumsList: List<Album>, val artistDetailModel: ArtistDetailModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ArtistAlbumAdapter.ArtistAlbumViewHolder>() {

    override fun onBindViewHolder(artistAlbumViewHolder: ArtistAlbumViewHolder, position: Int) {
        artistAlbumViewHolder.bind(albumsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistAlbumViewHolder {
        val layoutItemArtistAlbumBinding: LayoutItemArtistAlbumBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_artist_album, parent, false)
        layoutItemArtistAlbumBinding.artistDetailModel = artistDetailModel
        return ArtistAlbumViewHolder(layoutItemArtistAlbumBinding)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun refresh(albumsList: List<Album>) {
        this.albumsList = albumsList
        notifyDataSetChanged()
    }

    class ArtistAlbumViewHolder(val layoutItemArtistAlbumBinding: LayoutItemArtistAlbumBinding):
            RecyclerView.ViewHolder(layoutItemArtistAlbumBinding.root) {
        fun bind(album: Album, sharedViewModel: SharedViewModel) {
            layoutItemArtistAlbumBinding.album = album
            layoutItemArtistAlbumBinding.sharedViewModel = sharedViewModel
            layoutItemArtistAlbumBinding.executePendingBindings()
        }
    }
}