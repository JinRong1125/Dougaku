package shinei.com.dougaku.viewModel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.view.View
import shinei.com.dougaku.helper.Utils
import shinei.com.dougaku.view.base.FrameFragment
import shinei.com.dougaku.view.fragment.LikedAlbumsFragment
import shinei.com.dougaku.view.fragment.LikedTracksFragment
import shinei.com.dougaku.view.fragment.ListeningHistoryFragment
import shinei.com.dougaku.view.fragment.MyPlaylistsFragment
import javax.inject.Inject

class MyselfPageModel @Inject constructor(val application: Application): ViewModel() {

    fun intentToContent(view: View, id: Int) {
        Utils.addFrameFragment(view, selectFragment(id))
    }

    fun selectFragment(id: Int) : FrameFragment {
        return when (id) {
            0 -> LikedTracksFragment()
            1 -> LikedAlbumsFragment()
            2 -> MyPlaylistsFragment()
            3 -> ListeningHistoryFragment()
            else -> LikedTracksFragment()
        }
    }
}