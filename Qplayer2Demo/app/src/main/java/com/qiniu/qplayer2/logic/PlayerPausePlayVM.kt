package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState

class PlayerPausePlayVM: QIPlayerStateChangeListener, BasePlayerControlVM() {

    val playerStateLiveData = MutableLiveData<QPlayerState>()



    override fun onStateChanged(state: QPlayerState) {

        playerStateLiveData.value = state
    }

    public fun getCurrentPlayerState(): QPlayerState {
        return playerControlHandler?.currentPlayerState ?: QPlayerState.NONE
    }

    public fun resume() {
        playerControlHandler?.resumeRender()
    }

    public fun pause() {
        playerControlHandler?.pauseRender()
    }

    override fun onSetPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        playerControlHandler?.addPlayerStateChangeListener(this)
    }
}