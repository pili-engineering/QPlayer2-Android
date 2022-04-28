package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.logic.PlayerPausePlayVM

class PlayerPlayWidget : AppCompatImageView {
    private val mPlayerPausePlayVM = PlayerPausePlayVM()

    private val mObserver: Observer<QPlayerState> = Observer<QPlayerState> { it ->
        if (it == QPlayerState.PAUSED_RENDER) {
            visibility = View.VISIBLE
        } else {
            visibility = View.INVISIBLE
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
        mPlayerPausePlayVM.playerStateLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        mPlayerPausePlayVM.playerStateLiveData.removeObserver(mObserver)

    }
}