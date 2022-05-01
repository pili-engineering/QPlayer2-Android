package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerDownloadListener
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class CommonPlayerDownloadTextWidget : AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mPlayerDownloadListener = object : QIPlayerDownloadListener {
        @SuppressLint("SetTextI18n")
        override fun onDownloadChanged(speed: Long, bufferProgress: Long) {
            text = "${speed / 1024 / 8}KB/s"
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
    )

    override fun onWidgetActive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .addPlayerDownloadChangeListener(mPlayerDownloadListener)
    }

    override fun onWidgetInactive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerDownloadListener(mPlayerDownloadListener)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}