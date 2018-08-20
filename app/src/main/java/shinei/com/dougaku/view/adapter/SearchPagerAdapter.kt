package shinei.com.dougaku.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.view.fragment.SearchAlbumFragment
import shinei.com.dougaku.view.fragment.SearchArtistFragment
import shinei.com.dougaku.view.fragment.SearchProducerFragment
import shinei.com.dougaku.view.fragment.SearchSongFragment

class SearchPagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    val tabTitles = arrayOf(
            context.getString(R.string.song),
            context.getString(R.string.artist),
            context.getString(R.string.album),
            context.getString(R.string.producer))

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchSongFragment()
            1 -> SearchArtistFragment()
            2 -> SearchAlbumFragment()
            3 -> SearchProducerFragment()
            else -> SearchSongFragment()
        }
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

    fun destroyAllItems(viewPager: ViewPager, fragmentManager: FragmentManager) {
        for (i in 0 until tabTitles.size) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(this.instantiateItem(viewPager, i) as Fragment)
            fragmentTransaction.commit()
        }
    }
}