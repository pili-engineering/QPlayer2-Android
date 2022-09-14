package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.logic.PlayerPausePlayVM

class PlayerFullScreenPlayClickWidget: FrameLayout, View.OnClickListener {
    private val mPlayerPausePlayVM = PlayerPausePlayVM()



    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    fun setPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        mPlayerPausePlayVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        setOnClickListener(this)
    }

    private fun uninit() {
        setOnClickListener(null)

    }

    override fun onClick(v: View?) {
        val playerState = mPlayerPausePlayVM.getCurrentPlayerState()
        if (playerState == QPlayerState.PLAYING) {
            mPlayerPausePlayVM.pause()
        } else {
            mPlayerPausePlayVM.resume()
        }
    }
}