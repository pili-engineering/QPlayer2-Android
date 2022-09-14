package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerBiteRateListener
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class CommonPlayerBiteRateTextWidget : AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mPlayerBiteRateListener = object : QIPlayerBiteRateListener {
        @SuppressLint("SetTextI18n")
        override fun onBiteRateChanged(biteRate: Long) {
            text = "${biteRate / 1024}kbps"
        }

    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onWidgetActive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .addPlayerBiteRateChangeListener(mPlayerBiteRateListener)
    }

    override fun onWidgetInactive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerBiteRateChangeListener(mPlayerBiteRateListener)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}