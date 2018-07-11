package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemMyPlaylistTrackBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.MyPlaylistDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class MyPlaylistTracksAdapter(var songsList: List<Song>, val myPlaylistDetailModel: MyPlaylistDetailModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<MyPlaylistTracksAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemMyPlaylistTrackBinding: LayoutItemMyPlaylistTrackBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_my_playlist_track, parent, false)
        layoutItemMyPlaylistTrackBinding.myPlaylistDetailModel = myPlaylistDetailModel
        return SongViewHolder(layoutItemMyPlaylistTrackBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemMyPlaylistTrackBinding: LayoutItemMyPlaylistTrackBinding):
            RecyclerView.ViewHolder(layoutItemMyPlaylistTrackBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemMyPlaylistTrackBinding.song = song
            layoutItemMyPlaylistTrackBinding.position = adapterPosition
            layoutItemMyPlaylistTrackBinding.sharedViewModel = sharedViewModel
            layoutItemMyPlaylistTrackBinding.executePendingBindings()
        }
    }
}