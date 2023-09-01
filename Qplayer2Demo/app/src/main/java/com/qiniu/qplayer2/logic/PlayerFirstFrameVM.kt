package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerRenderListener
import com.qiniu.qmedia.component.player.QPlayerRenderHandler

class PlayerFirstFrameVM : QIPlayerRenderListener, BasePlayerRenderVM() {
    val playerFirstFrameElapsedTimeLiveData = MutableLiveData<Long>()

    override fun onFirstFrameRendered(elapsedTime: Long) {
        playerFirstFrameElapsedTimeLiveData.value = elapsedTime
    }

    override fun onInstallPlayerRenderHandler(renderHandler: QPlayerRenderHandler?) {
        playerRenderHandler?.addPlayerRenderListener(this)
    }

    override fun onClearPlayerListeners() {
        playerRenderHandler?.removePlayerRenderListener(this)
    }
}