package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerProgressListener
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import java.util.*

class CommonPlayerProgressTextWidget: AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mPlayerProgressListener = object: QIPlayerProgressListener {
        override fun onProgressChanged(duration: Long, progress: Long) {
            updateTime(progress, duration)
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

    private fun formatTime(position: Long): String {
        // 毫秒转秒，向上取整
        val totalSeconds = (position + 999) / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60

        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTime(currentPosition: Long, duration: Long) {
        var proStr = formatTime(currentPosition)
        if (TextUtils.isEmpty(proStr)) {
            proStr = "00:00"
        }
        var durStr = formatTime(duration)
        if (TextUtils.isEmpty(durStr)) {
            durStr = "00:00"
        }
        text = "$proStr/$durStr"
    }

    override fun onWidgetActive() {

        mPlayerCore.mPlayerContext.getPlayerControlHandler().also {
            updateTime(it.currentPosition, it.duration)
        }

        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .addPlayerProgressChangeListener(mPlayerProgressListener)
    }

    override fun onWidgetInactive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerProgressChangeListener(mPlayerProgressListener)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}