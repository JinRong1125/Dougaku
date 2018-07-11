package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.PopupMenu
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import shinei.com.dougaku.R
import shinei.com.dougaku.helper.RxSchedulersHelper
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.room.MyPlaylists
import shinei.com.dougaku.room.MyPlaylistsDao
import shinei.com.dougaku.view.activity.MainActivity
import shinei.com.dougaku.view.fragment.MyPlaylistDetailFragment
import javax.inject.Inject

class MyPlaylistsModel @Inject constructor(val application: Application,
                                           val myPlaylistsDao: MyPlaylistsDao): ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val myPlaylistsLiveData = MutableLiveData<List<MyPlaylists>>()

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

    fun getMyPlaylists() {
        compositeDisposable.add(myPlaylistsDao.getMyPlaylists()
                .compose(RxSchedulersHelper.singleIoToMain())
                .doOnSubscribe({ refreshing.postValue(true) })
                .subscribe({
                    myPlaylistsLiveData.postValue(it)
                }, {
                    refreshing.postValue(false)
                }))
    }

    fun playlistPopupMenu(view: View, sharedViewModel: SharedViewModel, myPlaylists: MyPlaylists) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menu.add(0, 0, 0, application.getString(R.string.menu_remove_playlist))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                0 -> Utils.deleteMyPlaylists(view.context, compositeDisposable, myPlaylistsDao, myPlaylists, sharedViewModel)
            }
            true
        }
        popupMenu.show()
    }

    fun intentToPlaylist(view: View, myPlaylists: MyPlaylists, selectedPlaylist: MutableLiveData<MyPlaylists>) {
        selectedPlaylist.postValue(myPlaylists)
        Utils.addFrameFragment(view, MyPlaylistDetailFragment())
    }
}