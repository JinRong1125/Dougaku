package shinei.com.dougaku.helper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class ControlSlidingUpPanelLayout: SlidingUpPanelLayout {

    var clickCollapseEnabled = false

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, diffStyle: Int): super(context, attrs, diffStyle)

    override fun setDragView(dragView: View?) {
        super.setDragView(dragView)
        dragView?.isClickable = true
        dragView?.isFocusable = false
        dragView?.isFocusableInTouchMode = false
        dragView?.setOnClickListener(OnClickListener {
            if (!isEnabled || !isTouchEnabled) return@OnClickListener
            if (panelState != PanelState.EXPANDED && panelState != PanelState.ANCHORED) {
                panelState = if (anchorPoint < 1.0f) {
                    PanelState.ANCHORED
                } else {
                    PanelState.EXPANDED
                }
            } else if (clickCollapseEnabled){
                panelState = PanelState.COLLAPSED
            }
        })
    }
}