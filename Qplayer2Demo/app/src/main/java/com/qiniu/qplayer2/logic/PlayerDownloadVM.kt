package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerDownloadListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler

class PlayerDownloadVM: QIPlayerDownloadListener, BasePlayerControlVM() {
    val playerDownloadSpeedLiveData = MutableLiveData<Long>()

    val downloadSpeed: Long
        public get() = playerControlHandler?.downloadSpeed ?: 0L

    override fun onDownloadChanged(speed: Long, bufferProgress: Long) {
        playerDownloadSpeedLiveData.value = speed
    }

    override fun onInstallPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        controlHandler?.addPlayerDownloadChangeListener(this)
    }

    override fun onClearPlayerListeners() {
        playerControlHandler?.removePlayerDownloadChangeListener(this)
    }
}