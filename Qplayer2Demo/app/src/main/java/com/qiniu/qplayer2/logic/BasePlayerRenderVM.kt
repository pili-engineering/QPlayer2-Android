package com.qiniu.qplayer2.logic

import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerRenderHandler

abstract class BasePlayerRenderVM {

    var playerRenderHandler: QPlayerRenderHandler? = null
        set(value) {
            if (value == null) {
                onClearPlayerListeners()
            } else {
                field = value
                onInstallPlayerRenderHandler(field)
            }
        }

    abstract fun onInstallPlayerRenderHandler(renderHandler: QPlayerRenderHandler?)

    abstract fun onClearPlayerListeners()
}