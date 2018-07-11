package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemSearchArtistBinding
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.viewModel.SearchArtistModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchArtistAdapter(var artistsList: List<Artist>, val searchArtistModel: SearchArtistModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<SearchArtistAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(artistsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemSearchArtistBinding: LayoutItemSearchArtistBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_search_artist, parent, false)
        layoutItemSearchArtistBinding.searchArtistModel = searchArtistModel
        return SongViewHolder(layoutItemSearchArtistBinding)
    }

    override fun getItemCount(): Int {
        return artistsList.size
    }

    fun refresh(artistsList: List<Artist>) {
        this.artistsList = artistsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemSearchArtistBinding: LayoutItemSearchArtistBinding):
            RecyclerView.ViewHolder(layoutItemSearchArtistBinding.root) {
        fun bind(artist: Artist, sharedViewModel: SharedViewModel) {
            layoutItemSearchArtistBinding.artist = artist
            layoutItemSearchArtistBinding.songCounts = artist.songCounts.toString() +
                    layoutItemSearchArtistBinding.root.resources.getString(R.string.songs)
            layoutItemSearchArtistBinding.sharedViewModel = sharedViewModel
            layoutItemSearchArtistBinding.executePendingBindings()
        }
    }
}