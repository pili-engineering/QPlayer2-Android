package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videoquality.VideoQualityFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams

class CommonPlayerSelectQualityWidget: AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>, View.OnClickListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mQualityToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? = null

    constructor(context: Context) : super(context) {
        init(context, null)

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)

    }

    private fun init(context: Context, attrs: AttributeSet?) {
        text = "清晰度"
    }

    override fun onWidgetActive() {
        setOnClickListener(this)

    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onClick(v: View?) {
        if (mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams() != null) {
            val layoutParams = FunctionWidgetLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.RIGHT
            mQualityToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(VideoQualityFunctionWidget::class.java, layoutParams)
        }

    }
}