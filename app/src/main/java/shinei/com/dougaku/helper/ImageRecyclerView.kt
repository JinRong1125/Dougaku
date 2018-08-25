package shinei.com.dougaku.helper

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import com.bumptech.glide.Glide

class ImageRecyclerView: RecyclerView {

    var scrolling = false

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, diffStyle: Int): super(context, attrs, diffStyle) {
        addOnScrollListener(scrollListener)
    }

    val scrollListener = object :RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    if (scrolling) {
                        Glide.with(recyclerView).resumeRequests()
                    }

                    scrolling = false
                }
                RecyclerView.SCROLL_STATE_DRAGGING,
                RecyclerView.SCROLL_STATE_SETTLING -> {
                    if (!scrolling) {
                        Glide.with(recyclerView).pauseRequests()
                    }
                    scrolling = true
                }
            }
        }
    }
}