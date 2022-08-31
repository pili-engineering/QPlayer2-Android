package com.qiniu.qplayer2.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.logic.PlayerPausePlayVM
import com.qiniu.qplayer2.logic.PlayerProgressVM
import java.util.*

class PlayerProgressTextWidget : AppCompatTextView {

    private val mPlayerProgressVM = PlayerProgressVM()

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

    fun setPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        mPlayerProgressVM.playerControlHandler = controlHandler
        init()
    }

    private fun init() {
        updateTime(0, 0)

        mPlayerProgressVM.playerProgressLiveData.observeForever(
            androidx.lifecycle.Observer {
                updateTime(mPlayerProgressVM.getDuration(), it)
            })
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

    private fun formatTime(position: Long): String {
        // 毫秒转秒，向上取整
        val totalSeconds = (position + 999) / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60

        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}