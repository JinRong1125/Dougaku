package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemArtistSongBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.ArtistDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistSongAdapter(var songsList: List<Song>, val artistDetailModel: ArtistDetailModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<ArtistSongAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemArtistSongBinding: LayoutItemArtistSongBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_artist_song, parent, false)
        layoutItemArtistSongBinding.artistDetailModel = artistDetailModel
        return SongViewHolder(layoutItemArtistSongBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemArtistSongBinding: LayoutItemArtistSongBinding):
            RecyclerView.ViewHolder(layoutItemArtistSongBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemArtistSongBinding.song = song
            layoutItemArtistSongBinding.position = adapterPosition
            layoutItemArtistSongBinding.sharedViewModel = sharedViewModel
            layoutItemArtistSongBinding.executePendingBindings()
        }
    }
}