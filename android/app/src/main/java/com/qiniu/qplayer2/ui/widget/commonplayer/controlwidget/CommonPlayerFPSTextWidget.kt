package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerFPSListener
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class CommonPlayerFPSTextWidget: AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mPlayerFPSListener = object : QIPlayerFPSListener {
        @SuppressLint("SetTextI18n")
        override fun onFPSChanged(fps: Int) {
            text = "FPS:${fps}"
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
            .addPlayerFPSChangeListener(mPlayerFPSListener)
    }

    override fun onWidgetInactive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerFPSChangeListener(mPlayerFPSListener)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}