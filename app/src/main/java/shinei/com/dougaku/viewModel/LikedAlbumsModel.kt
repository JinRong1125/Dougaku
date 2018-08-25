package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.api.DougakuRepository
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.model.Album
import shinei.com.dougaku.room.LikedAlbumsDao
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.fragment.AlbumDetailFragment
import java.util.*
import javax.inject.Inject

class LikedAlbumsModel @Inject constructor(val application: Application,
                                           val dougakuRepository: DougakuRepository,
                                           val likedAlbumsDao: LikedAlbumsDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val albumsLiveData = MutableLiveData<List<Album>>()

    val refreshing = MutableLiveData<Boolean>()
    val emptyImageVisibility = MutableLiveData<Int>()

    init {
        refreshing.postValue(false)
        emptyImageVisibility.postValue(View.GONE)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onBackPressed(view: View) {
        (view.context as MainActivity).supportFragmentManager.popBackStack()
    }

    fun getLikedAlbums() {
        compositeDisposable.add(likedAlbumsDao.getLikedAlbums()
                .compose(RxSchedulersHelper.singleIoToMain())
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    val albumsList = ArrayList<Album>()
                    for (i in it.size - 1 downTo 0) {
                        albumsList.add(it[i].album)
                    }
                    albumsLiveData.postValue(albumsList)
                }, {
                    refreshing.postValue(false)
                }))
    }

    fun albumPopupMenu(view: View, sharedViewModel: SharedViewModel, album: Album) {
        val popupMenu = PopupMenu(view.context, view)
        var likedAlbum = false
        var likedAlbumId = 0

        popupMenu.menu.add(0, 1, 1, application.getString(R.string.menu_go_to_producer))
        compositeDisposable.add(likedAlbumsDao.getLikedAlbum(album.albumId)
                .compose(RxSchedulersHelper.singleIoToMain())
                .subscribe({
                    popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_unlike_album))
                    likedAlbum = true
                    likedAlbumId = it.id
                    popupMenu.show()
                }, {
                    popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_like_album))
                    popupMenu.show()
                }))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> {
                    if (!likedAlbum)
                        Utils.insertLikedAlbums(view.context, compositeDisposable, likedAlbumsDao, sharedViewModel, album)
                    else
                        Utils.deleteLikedAlbums(view.context, compositeDisposable, likedAlbumsDao, sharedViewModel, likedAlbumId, album)
                }
                1 ->
                    Utils.goToProducer(view, compositeDisposable, dougakuRepository, sharedViewModel, album.producerId, refreshing)
            }
            true
        }
    }

    fun intentToAlbumDetail(view: View, album: Album, selectedAlbum: MutableLiveData<Album>) {
        selectedAlbum.postValue(album)
        Utils.addFrameFragment(view, AlbumDetailFragment())
    }
}