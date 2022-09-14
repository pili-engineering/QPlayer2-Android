package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qplayer2.logic.PlayerDownloadVM

class PlayerDownloadTextWidget : AppCompatTextView {
    private val mPlayerDownloadVM = PlayerDownloadVM()

    private val mObserver: Observer<Long> = Observer<Long> { it -> text = "${it / 1024 / 8}KB/s" }


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
        mPlayerDownloadVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        mPlayerDownloadVM.playerDownloadSpeedLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        mPlayerDownloadVM.playerDownloadSpeedLiveData.removeObserver(mObserver)
    }
}