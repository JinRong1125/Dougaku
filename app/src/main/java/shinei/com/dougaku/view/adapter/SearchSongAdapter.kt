package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemSearchSongBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.SearchSongModel
import shinei.com.dougaku.viewModel.SharedViewModel

class SearchSongAdapter(var songsList: List<Song>, val searchSongModel: SearchSongModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<SearchSongAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemSearchSongBinding: LayoutItemSearchSongBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_search_song, parent, false)
        layoutItemSearchSongBinding.searchSongModel = searchSongModel
        return SongViewHolder(layoutItemSearchSongBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemSearchSongBinding: LayoutItemSearchSongBinding):
            RecyclerView.ViewHolder(layoutItemSearchSongBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemSearchSongBinding.song = song
            layoutItemSearchSongBinding.position = adapterPosition
            layoutItemSearchSongBinding.sharedViewModel = sharedViewModel
            layoutItemSearchSongBinding.executePendingBindings()
        }
    }
}