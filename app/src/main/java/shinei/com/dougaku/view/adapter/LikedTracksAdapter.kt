package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemLikedTrackBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.LikedTracksModel
import shinei.com.dougaku.viewModel.SharedViewModel

class LikedTracksAdapter(var songsList: List<Song>, val likedTracksModel: LikedTracksModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<LikedTracksAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemLikedTrackBinding: LayoutItemLikedTrackBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_liked_track, parent, false)
        layoutItemLikedTrackBinding.likedTracksModel = likedTracksModel
        return SongViewHolder(layoutItemLikedTrackBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemLikedTrackBinding: LayoutItemLikedTrackBinding):
            RecyclerView.ViewHolder(layoutItemLikedTrackBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemLikedTrackBinding.song = song
            layoutItemLikedTrackBinding.position = adapterPosition
            layoutItemLikedTrackBinding.sharedViewModel = sharedViewModel
            layoutItemLikedTrackBinding.executePendingBindings()
        }
    }
}