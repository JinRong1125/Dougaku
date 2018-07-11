package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemAlbumTrackBinding
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.viewModel.AlbumDetailModel
import shinei.com.dougaku.viewModel.SharedViewModel

class AlbumDetailAdapter(var songsList: List<Song>, val albumDetailModel: AlbumDetailModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<AlbumDetailAdapter.TrackViewHolder>() {

    override fun onBindViewHolder(trackViewHolder: TrackViewHolder, position: Int) {
        trackViewHolder.bind(songsList[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutItemAlbumTrackBinding: LayoutItemAlbumTrackBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_album_track, parent, false)
        layoutItemAlbumTrackBinding.albumDetailModel = albumDetailModel
        return TrackViewHolder(layoutItemAlbumTrackBinding)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun refresh(songsList: List<Song>) {
        this.songsList = songsList
        notifyDataSetChanged()
    }

    class TrackViewHolder(val layoutItemAlbumTrackBinding: LayoutItemAlbumTrackBinding):
            RecyclerView.ViewHolder(layoutItemAlbumTrackBinding.root) {
        fun bind(song: Song, sharedViewModel: SharedViewModel) {
            layoutItemAlbumTrackBinding.song = song
            layoutItemAlbumTrackBinding.position = adapterPosition
            layoutItemAlbumTrackBinding.sharedViewModel = sharedViewModel
            if (song.track == 1 && song.disc > 1)
                layoutItemAlbumTrackBinding.discVisibility = View.VISIBLE
            else
                layoutItemAlbumTrackBinding.discVisibility = View.GONE
            layoutItemAlbumTrackBinding.executePendingBindings()
        }
    }
}