package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemArtistBinding
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.viewModel.ArtistPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistAdapter(var artistsList: List<Artist>, val artistPageModel: ArtistPageModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    override fun onBindViewHolder(artistViewHolder: ArtistViewHolder, position: Int) {
        artistViewHolder.bind(artistsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val layoutItemArtistBinding: LayoutItemArtistBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.layout_item_artist, parent, false)
        layoutItemArtistBinding.artistPageModel = artistPageModel
        return ArtistViewHolder(layoutItemArtistBinding)
    }

    override fun getItemCount(): Int {
        return artistsList.size
    }

    fun refresh(artistsList: List<Artist>) {
        this.artistsList = artistsList
        notifyDataSetChanged()
    }

    class ArtistViewHolder(val layoutItemArtistBinding: LayoutItemArtistBinding):
            RecyclerView.ViewHolder(layoutItemArtistBinding.root) {
        fun bind(artist: Artist, sharedViewModel: SharedViewModel) {
            layoutItemArtistBinding.artist = artist
            layoutItemArtistBinding.songCounts = artist.songCounts.toString() +
                    layoutItemArtistBinding.root.resources.getString(R.string.songs)
            layoutItemArtistBinding.sharedViewModel = sharedViewModel
            layoutItemArtistBinding.executePendingBindings()
        }
    }
}