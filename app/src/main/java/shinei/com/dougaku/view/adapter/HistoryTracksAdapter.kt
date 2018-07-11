package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemHistoryTrackBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.ListeningHistoryModel
import shinei.com.dougaku.viewModel.SharedViewModel

class HistoryTracksAdapter(var songsList: List<Song>, val ListeningHistoryModel: ListeningHistoryModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<HistoryTracksAdapter.SongViewHolder>() {

    override fun onBindViewHolder(songViewHolder: SongViewHolder, position: Int) {
        songViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val layoutItemHistoryTrackBinding: LayoutItemHistoryTrackBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_history_track, parent, false)
        layoutItemHistoryTrackBinding.listeningHistoryModel = ListeningHistoryModel
        return SongViewHolder(layoutItemHistoryTrackBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class SongViewHolder(val layoutItemHistoryTrackBinding: LayoutItemHistoryTrackBinding):
            RecyclerView.ViewHolder(layoutItemHistoryTrackBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemHistoryTrackBinding.song = song
            layoutItemHistoryTrackBinding.position = adapterPosition
            layoutItemHistoryTrackBinding.sharedViewModel = sharedViewModel
            layoutItemHistoryTrackBinding.executePendingBindings()
        }
    }
}