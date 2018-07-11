package shinei.com.dougaku.view.fragment

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shinei.com.dougaku.R
import shinei.com.dougaku.databinding.FragmentMyselfBinding
import shinei.com.dougaku.model.Myself
import shinei.com.dougaku.view.adapter.MyselfAdapter
import shinei.com.dougaku.view.base.PageFragment
import shinei.com.dougaku.viewModel.MyselfPageModel

class MyselfFragment: PageFragment() {

    lateinit var fragmentMyselfBinding: FragmentMyselfBinding
    lateinit var myselfPageModel: MyselfPageModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMyselfBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_myself, container, false)
        fragmentMyselfBinding.setLifecycleOwner(this)
        myselfPageModel = ViewModelProviders.of(this, viewModelFactory).get(MyselfPageModel::class.java)
        fragmentMyselfBinding.myselfPageModel = myselfPageModel
        loaded = true
        return fragmentMyselfBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    override fun setUp() {
        val myselfList = listOf(
                Myself(0, context!!.getString(R.string.liked_tracks), context!!.getDrawable(R.drawable.icon_heart)),
                Myself(1, context!!.getString(R.string.liked_albums), context!!.getDrawable(R.drawable.icon_album)),
                Myself(2, context!!.getString(R.string.playlists), context!!.getDrawable(R.drawable.icon_playlist)),
                Myself(3, context!!.getString(R.string.listening_history), context!!.getDrawable(R.drawable.icon_history)))

        val myselfAdapter = MyselfAdapter(myselfList, myselfPageModel)
        fragmentMyselfBinding.myselfRecyclerView.adapter = myselfAdapter
    }
}