package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemMyPlaylistBinding
import shinei.com.dougaku.room.MyPlaylists
import shinei.com.dougaku.viewModel.MyPlaylistsModel
import shinei.com.dougaku.viewModel.SharedViewModel

class MyPlaylistsAdapter(var myPlayLists: List<MyPlaylists>, val myPlaylistsModel: MyPlaylistsModel, val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<MyPlaylistsAdapter.PlaylistViewHolder>() {

    override fun onBindViewHolder(playlistViewHolder: PlaylistViewHolder, position: Int) {
        playlistViewHolder.bind(myPlayLists[position], sharedViewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutItemMyPlaylistBinding: LayoutItemMyPlaylistBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.layout_item_my_playlist, parent, false)
        layoutItemMyPlaylistBinding.myPlaylistsModel = myPlaylistsModel
        return PlaylistViewHolder(layoutItemMyPlaylistBinding)
    }

    override fun getItemCount(): Int {
        return myPlayLists.size
    }

    fun refresh(myPlayLists: List<MyPlaylists>) {
        this.myPlayLists = myPlayLists
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(val layoutItemMyPlaylistBinding: LayoutItemMyPlaylistBinding):
            RecyclerView.ViewHolder(layoutItemMyPlaylistBinding.root) {
        fun bind(myPlayList: MyPlaylists, sharedViewModel: SharedViewModel) {
            layoutItemMyPlaylistBinding.myPlaylist = myPlayList
            layoutItemMyPlaylistBinding.sharedViewModel = sharedViewModel
            layoutItemMyPlaylistBinding.executePendingBindings()
        }
    }
}