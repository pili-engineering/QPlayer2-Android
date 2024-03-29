package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerBufferingListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler

class PlayerBufferingVM: QIPlayerBufferingListener, BasePlayerControlVM() {

    val playerBufferingLiveData = MutableLiveData<Boolean>()

    override fun onBufferingStart() {
        playerBufferingLiveData.value = true
    }

    override fun onBufferingEnd() {
        playerBufferingLiveData.value = false
    }

    override fun onInstallPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        controlHandler?.addPlayerBufferingChangeListener(this)
    }

    override fun onClearPlayerListeners() {
        playerControlHandler?.removePlayerBufferingChangeListener(this)

    }
}