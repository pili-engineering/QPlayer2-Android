package com.qiniu.qplayer2.ui.widget.commonplayer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class CommonPlayerPausePlayWidget : AppCompatImageView, View.OnClickListener,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mPlayerStateChangeListener = object : QIPlayerStateChangeListener {
        override fun onStateChanged(state: QPlayerState) {
            updateIcon(state)
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

    private fun updateIcon(state: QPlayerState) {
        if (state == QPlayerState.PLAYING) {
            setImageLevel(1)
        } else if (state == QPlayerState.PAUSED_RENDER ||
            state == QPlayerState.PREPARE ||
            state == QPlayerState.INIT ||
            state == QPlayerState.NONE
        ) {
            setImageLevel(0)
        } else if( state == QPlayerState.COMPLETED) {
            setImageLevel(2)
        }
    }

    override fun onClick(v: View?) {
        val playerState = mPlayerCore.mPlayerContext.getPlayerControlHandler().currentPlayerState
        if (playerState == QPlayerState.PLAYING) {
            mPlayerCore.mPlayerContext.getPlayerControlHandler().pauseRender()
        } else if (playerState == QPlayerState.COMPLETED){
            mPlayerCore.mCommonPlayerController.replayCurrentVideo()
        } else {
            mPlayerCore.mPlayerContext.getPlayerControlHandler().resumeRender()

        }
    }

    override fun onWidgetActive() {
        setImageResource(R.drawable.qmedia_player_play_pause_level_list)
        setOnClickListener(this)

        updateIcon(mPlayerCore.mPlayerContext.getPlayerControlHandler().currentPlayerState)
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .addPlayerStateChangeListener(mPlayerStateChangeListener)


    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerStateChangeListener(mPlayerStateChangeListener)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}