package shinei.com.dougaku.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.view.fragment.AlbumFragment
import shinei.com.dougaku.view.fragment.ArtistFragment
import shinei.com.dougaku.view.fragment.MyselfFragment
import shinei.com.dougaku.view.fragment.ProducerFragment

class MainPagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    val tabTitles = arrayOf(
            context.getString(R.string.album),
            context.getString(R.string.artist),
            context.getString(R.string.producer),
            context.getString(R.string.myself))

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AlbumFragment()
            1 -> ArtistFragment()
            2 -> ProducerFragment()
            3 -> MyselfFragment()
            else -> AlbumFragment()
        }
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}
}