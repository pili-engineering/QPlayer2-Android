package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.logic.PlayerPausePlayVM

class PlayerPausePlayWidget : AppCompatImageView, View.OnClickListener {

    private val mPlayerPausePlayVM = PlayerPausePlayVM()

    private val mObserver: Observer<QPlayerState> = Observer<QPlayerState> { it ->
        if (it == QPlayerState.PLAYING) {
            setImageLevel(1)
        } else {
            setImageLevel(0)
        }
        Toast.makeText(context, "state: $it", Toast.LENGTH_SHORT).show()
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

    fun setPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        mPlayerPausePlayVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        setImageResource(R.drawable.qmedia_player_play_pause_level_list)
        setOnClickListener(this)
        mPlayerPausePlayVM.playerStateLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        setOnClickListener(null)
        mPlayerPausePlayVM.playerStateLiveData.observeForever(mObserver)
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