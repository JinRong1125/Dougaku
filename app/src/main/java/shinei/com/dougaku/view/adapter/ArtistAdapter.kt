package shinei.com.dougaku.view.adapter

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemArtistBinding
import shinei.com.dougaku.model.Artist
import shinei.com.dougaku.viewModel.ArtistPageModel
import shinei.com.dougaku.viewModel.SharedViewModel

class ArtistAdapter(val artistPageModel: ArtistPageModel, val sharedViewModel: SharedViewModel) : PagedListAdapter<Artist, RecyclerView.ViewHolder>(artistDiffCallback) {

    private var afterLoad = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.layout_item_artist -> ArtistViewHolder.create(parent)
            R.layout.layout_item_progressbar -> ProgressbarViewHolder.create(parent)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.layout_item_artist -> (holder as ArtistViewHolder).bind(getItem(position), artistPageModel, sharedViewModel)
            R.layout.layout_item_progressbar -> (holder as ProgressbarViewHolder)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (afterLoad && position == itemCount - 1)
            R.layout.layout_item_progressbar
        else
            R.layout.layout_item_artist
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (afterLoad) 1 else 0
    }

    fun setAfterLoad(afterLoad: Boolean) {
        this.afterLoad = afterLoad
        if (afterLoad)
            notifyItemInserted(super.getItemCount())
        else
            notifyItemRemoved(super.getItemCount())
    }

    class ArtistViewHolder(val layoutItemArtistBinding: LayoutItemArtistBinding): RecyclerView.ViewHolder(layoutItemArtistBinding.root) {
        companion object {
            fun create(parent: ViewGroup): ArtistViewHolder {
                val layoutItemArtistBinding: LayoutItemArtistBinding =
                        DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                                R.layout.layout_item_artist, parent, false)
                return ArtistViewHolder(layoutItemArtistBinding)
            }
        }

        fun bind(artist: Artist?, artistPageModel: ArtistPageModel, sharedViewModel: SharedViewModel) {
            layoutItemArtistBinding.artist = artist
            layoutItemArtistBinding.songCounts = artist?.songCounts.toString() +
                    layoutItemArtistBinding.root.resources.getString(R.string.songs)
            layoutItemArtistBinding.artistPageModel = artistPageModel
            layoutItemArtistBinding.sharedViewModel = sharedViewModel
            layoutItemArtistBinding.executePendingBindings()
        }
    }

    class ProgressbarViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup): ProgressbarViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_progressbar, parent, false)
                return ProgressbarViewHolder(view)
            }
        }
    }

    companion object {
        val artistDiffCallback = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.artistId == newItem.artistId
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }
        }
    }
}