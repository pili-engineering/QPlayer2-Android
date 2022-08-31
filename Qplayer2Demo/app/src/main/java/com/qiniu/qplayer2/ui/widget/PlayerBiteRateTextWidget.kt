package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qplayer2.logic.PlayerBiteRateVM

class PlayerBiteRateTextWidget : AppCompatTextView {

    private val mPlayerBiteRateVM = PlayerBiteRateVM()

    private val mObserver: Observer<Long> = Observer<Long> { it -> text = "${it / 1024}kbps" }


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
        mPlayerBiteRateVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        mPlayerBiteRateVM.playerBiteRateLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        mPlayerBiteRateVM.playerBiteRateLiveData.removeObserver(mObserver)
    }
}