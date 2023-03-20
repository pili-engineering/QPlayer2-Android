package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerFPSListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler

class PlayerFPSVM:QIPlayerFPSListener, BasePlayerControlVM() {
    val playerFPSLiveData = MutableLiveData<Int>()

    val fps: Int
        public get() = playerControlHandler?.fps ?: 0

    override fun onFPSChanged(fps: Int) {
        playerFPSLiveData.value = fps
    }

    override fun onInstallPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        controlHandler?.addPlayerFPSChangeListener(this)
    }

    override fun onClearPlayerListeners() {
        playerControlHandler?.removePlayerFPSChangeListener(this)

    }
}