package shinei.com.dougaku.helper

import android.content.Context
import android.content.DialogInterface
import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.transition.Fade
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import shinei.com.dougaku.R
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.model.Song
import shinei.com.dougaku.room.*
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.viewModel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Utils{
    @JvmStatic @BindingAdapter("loadImage")
    fun loadImage(imageView: ImageView, url: String?) {
        Glide.with(imageView)
                .load(url)
                .apply(RequestOptions().placeholder(R.drawable.icon_thumbnail).error(R.drawable.icon_thumbnail))
                .transition(withCrossFade(100))
                .into(imageView)
    }

    @JvmStatic @BindingAdapter("blurImage")
    fun blurImage(imageView: ImageView, url: String?) {
        Glide.with(imageView)
                .load(url)
                .apply(bitmapTransform(MultiTransformation(
                        BlurTransformation(25, 10),
                        ColorFilterTransformation(Color.argb(102, 0, 0, 0)))))
                .transition(withCrossFade(100))
                .into(imageView)
    }

    @JvmStatic @BindingAdapter("formatTime")
    fun formatTime(textView: TextView, millisecond: Int) {
        textView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(millisecond)
    }

    @JvmStatic @BindingAdapter(value=["currentTrack", "position"], requireAll=false)
    fun playStausVisibility(imageView: ImageView, currentTrack: Int, position: Int) {
        if (currentTrack != position)
            imageView.visibility = View.GONE
        else
            imageView.visibility = View.VISIBLE
    }

    fun createTrackPopupMenu(view: View, compositeDisposable: CompositeDisposable, likedTracksDao: LikedTracksDao, myPlaylistsDao: MyPlaylistsDao, sharedViewModel: SharedViewModel, song: Song) {
        val context = view.context
        val popupMenu = PopupMenu(context, view)
        var likedTrack = false
        var likedTrackId = 0

        popupMenu.menu.add(0, 1, 1, context.getString(R.string.menu_add_to_playlist))
        compositeDisposable.add(likedTracksDao.getLikedTrack(song.songId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    popupMenu.menu.add(0, 0, 0, context.getString(R.string.menu_unlike_track))
                    likedTrack = true
                    likedTrackId = it.id
                    popupMenu.show()
                }, {
                    popupMenu.menu.add(0, 0, 0, context.getString(R.string.menu_like_track))
                    popupMenu.show()
                }))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {
                    if (!likedTrack)
                        insertLikedTracks(context, compositeDisposable, likedTracksDao, sharedViewModel, song)
                    else
                        deleteLikedTracks(context, compositeDisposable, likedTracksDao, sharedViewModel, likedTrackId, song)
                }
                1 ->
                    createPlaylistDialog(context, compositeDisposable, myPlaylistsDao, sharedViewModel, arrayListOf(song))
            }
            true
        }
    }

    fun createPlaylistDialog(context: Context, compositeDisposable: CompositeDisposable, myPlaylistsDao: MyPlaylistsDao, sharedViewModel: SharedViewModel, songsList: ArrayList<Song>) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(context.getString(R.string.menu_add_to_playlist))
        val items = arrayListOf(context.getString(R.string.option_create_new_playlist))
        compositeDisposable.add(myPlaylistsDao.getMyPlaylists()
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    for(i in 0 until it.size) {
                        items.add(it[i].title)
                    }
                    alertDialog.setItems(items.toTypedArray(), DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            0 -> {
                                val newPlaylistDialog = AlertDialog.Builder(context)
                                newPlaylistDialog.setTitle(context.getString(R.string.option_create_new_playlist))
                                val editText = EditText(context)
                                editText.setSingleLine(true)
                                editText.hint = context.getString(R.string.hint_enter_playlist_title)
                                newPlaylistDialog.setView(editText)
                                newPlaylistDialog.setNegativeButton(context.getString(R.string.cancel), null)
                                newPlaylistDialog.setPositiveButton(context.getString(R.string.done), DialogInterface.OnClickListener { dialog, which ->
                                    val title = editText.text.toString()
                                    if (!title.trim().isEmpty())
                                        insertMyPlaylists(context, compositeDisposable, myPlaylistsDao, sharedViewModel, 0, title, songsList)
                                    else
                                        showToast(context, context.getString(R.string.toast_playlist_title_invalid))
                                })
                                newPlaylistDialog.show()
                            }
                            else -> addToMyPlaylists(context, compositeDisposable, myPlaylistsDao, sharedViewModel, items[which], songsList)
                        }
                    })
                    alertDialog.show()
                }, {}))
    }

    fun insertLikedTracks(context: Context, compositeDisposable: CompositeDisposable, likedTracksDao: LikedTracksDao, sharedViewModel: SharedViewModel, song: Song) {
        compositeDisposable.add(Completable.fromAction {
            likedTracksDao.insertLikedTracks(LikedTracks(0, song.songId, song)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.likedTracksUpdated.postValue(true)
                    showToast(context, context.getString(R.string.toast_add_to_liked_tracks))
                }))
    }
    
    fun deleteLikedTracks(context: Context, compositeDisposable: CompositeDisposable, likedTracksDao: LikedTracksDao, sharedViewModel: SharedViewModel, likedTrackId: Int, song: Song) {
        compositeDisposable.add(Completable.fromAction {
            likedTracksDao.deleteLikedTracks(LikedTracks(likedTrackId, song.songId, song)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.likedTracksUpdated.postValue(true)
                    showToast(context, context.getString(R.string.toast_remove_from_liked_tracks))
                }))
    }

    fun insertLikedAlbums(context: Context, compositeDisposable: CompositeDisposable, likedAlbumsDao: LikedAlbumsDao, sharedViewModel: SharedViewModel, album: Album) {
        compositeDisposable.add(Completable.fromAction {
            likedAlbumsDao.insertLikedAlbums(LikedAlbums(0, album.albumId, album)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.likedAlbumsUpdated.postValue(true)
                    showToast(context, context.getString(R.string.toast_add_to_liked_albums))
                }))
    }

    fun deleteLikedAlbums(context: Context, compositeDisposable: CompositeDisposable, likedAlbumsDao: LikedAlbumsDao, sharedViewModel: SharedViewModel, likedAlbumId: Int, album: Album) {
        compositeDisposable.add(Completable.fromAction {
            likedAlbumsDao.deleteLikedAlbums(LikedAlbums(likedAlbumId, album.albumId, album)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.likedAlbumsUpdated.postValue(true)
                    showToast(context, context.getString(R.string.toast_remove_from_liked_albums))
                }))
    }

    fun executeLikedTracks(context: Context, sharedViewModel: SharedViewModel, compositeDisposable: CompositeDisposable, likedTracksDao: LikedTracksDao, likedTrack: Boolean, likedTrackId: Int, song: Song) {
        if (!likedTrack) {
            compositeDisposable.add(Completable.fromAction {
                likedTracksDao.insertLikedTracks(LikedTracks(0, song.songId, song)) }
                    .compose(RxSchedulersHelper.completableIoToMain())
                    .subscribe({
                        sharedViewModel.likedTracksUpdated.postValue(true)
                        showToast(context, context.getString(R.string.toast_add_to_liked_tracks))
                    }))
        }
        else {
            compositeDisposable.add(Completable.fromAction {
                likedTracksDao.deleteLikedTracks(LikedTracks(likedTrackId, song.songId, song)) }
                    .compose(RxSchedulersHelper.completableIoToMain())
                    .subscribe({
                        sharedViewModel.likedTracksUpdated.postValue(true)
                        showToast(context, context.getString(R.string.toast_remove_from_liked_tracks))
                    }))
        }
    }

    fun insertMyPlaylists(context: Context, compositeDisposable: CompositeDisposable, myPlaylistsDao: MyPlaylistsDao, sharedViewModel: SharedViewModel, playlistId: Int, title: String, songsList: ArrayList<Song>) {
        compositeDisposable.add(Completable.fromAction {
            myPlaylistsDao.insertMyPlaylists(MyPlaylists(playlistId, title, songsList)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.myPlaylistUpdated.postValue(true)
                    showToast(context, context.getString(R.string.toast_add_to_playlist))
                }))
    }

    fun deleteMyPlaylists(context: Context, compositeDisposable: CompositeDisposable, myPlaylistsDao: MyPlaylistsDao, myPlaylists: MyPlaylists, sharedViewModel: SharedViewModel) {
        compositeDisposable.add(Completable.fromAction {
            myPlaylistsDao.deleteMyPlaylists(myPlaylists) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.myPlaylistUpdated.postValue(true)
                    Utils.showToast(context, context.getString(R.string.toast_remove_playlist))
                }))
    }

    fun addToMyPlaylists(context: Context, compositeDisposable: CompositeDisposable, myPlaylistsDao: MyPlaylistsDao, sharedViewModel: SharedViewModel, title: String, songsList: ArrayList<Song>) {
        compositeDisposable.add(myPlaylistsDao.getMyPlaylist(title)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    val oldList = ArrayList(it.songsList)
                    for (i in 0 until songsList.size) {
                        if (!oldList.contains(songsList[i])) {
                            oldList.add(songsList[i])
                        }
                    }
                    compositeDisposable.add(Completable.fromAction {
                        myPlaylistsDao.updateMyPlaylists(MyPlaylists(it.playlistId, it.title, oldList)) }
                            .compose(RxSchedulersHelper.completableIoToMain())
                            .subscribe({
                                sharedViewModel.myPlaylistUpdated.postValue(true)
                                showToast(context, context.getString(R.string.toast_add_to_playlist))
                            }))
                }, {}))
    }

    fun removeFromMyPlaylists(context: Context, compositeDisposable: CompositeDisposable, myPlaylistsDao: MyPlaylistsDao, sharedViewModel: SharedViewModel, playlistId: Int, title: String, songsList: ArrayList<Song>) {
        compositeDisposable.add(Completable.fromAction {
            myPlaylistsDao.updateMyPlaylists(MyPlaylists(playlistId, title, songsList)) }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.myPlaylistUpdated.postValue(true)
                    Utils.showToast(context, context.getString(R.string.toast_remove_from_playlist))
                }))
    }

    fun deleteHistoryTracks(context: Context, compositeDisposable: CompositeDisposable, historyTracksDao: HistoryTracksDao, sharedViewModel: SharedViewModel) {
        compositeDisposable.add(Completable.fromAction {
            historyTracksDao.deleteHistoryTracks() }
                .compose(RxSchedulersHelper.completableIoToMain())
                .subscribe({
                    sharedViewModel.historyTracksUpdated.postValue(true)
                    Utils.showToast(context, context.getString(R.string.toast_clear_listening_history))
                }))
    }

    fun addFrameFragment(view: View, fragment: FrameFragment) {
        val fade = Fade()
        fade.duration = 100
        fragment.enterTransition = fade
        (view.context as MainActivity).supportFragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getDrawable(context: Context, id: Int): Drawable {
        return context.getDrawable(id)
    }
}