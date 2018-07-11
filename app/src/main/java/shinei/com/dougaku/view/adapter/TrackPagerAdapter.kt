package shinei.com.dougaku.view.adapter

import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.LayoutItemCoverBinding
import shinei.com.dougaku.model.Song

class TrackPagerAdapter(val songsList: List<Song>): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return songsList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutItemCoverBinding: LayoutItemCoverBinding =
                DataBindingUtil.inflate(LayoutInflater.from(container.context),
                        R.layout.layout_item_cover, container, false)
        layoutItemCoverBinding.song = songsList[position]
        layoutItemCoverBinding.executePendingBindings()
        container.addView(layoutItemCoverBinding.root)
        return layoutItemCoverBinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}