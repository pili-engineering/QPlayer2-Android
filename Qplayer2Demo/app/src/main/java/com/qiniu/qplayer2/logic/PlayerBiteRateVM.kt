package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerBiteRateListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler

class PlayerBiteRateVM: QIPlayerBiteRateListener, BasePlayerControlVM() {

    val playerBiteRateLiveData = MutableLiveData<Long>()

    val bitRate: Long
        public get() = playerControlHandler?.biteRate ?: 0L


    override fun onBiteRateChanged(biteRate: Long) {
        playerBiteRateLiveData.value = biteRate
    }

    override fun onSetPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        playerControlHandler?.addPlayerBiteRateChangeListener(this)
    }
}