package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemPlaylistBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.PlayerViewModel
import shinei.com.dougaku.viewModel.SharedViewModel

class PlayListAdapter(var songsList: List<Song>, val playerViewModel: PlayerViewModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<PlayListAdapter.TrackViewHolder>() {

    override fun onBindViewHolder(trackViewHolder: TrackViewHolder, position: Int) {
        trackViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutItemPlaylistBinding: LayoutItemPlaylistBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_playlist, parent, false)
        layoutItemPlaylistBinding.playerViewModel = playerViewModel
        return TrackViewHolder(layoutItemPlaylistBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    class TrackViewHolder(val layoutItemPlaylistBinding: LayoutItemPlaylistBinding):
            RecyclerView.ViewHolder(layoutItemPlaylistBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemPlaylistBinding.song = song
            layoutItemPlaylistBinding.sharedViewModel = sharedViewModel
            layoutItemPlaylistBinding.position = adapterPosition
            layoutItemPlaylistBinding.executePendingBindings()
        }
    }
}