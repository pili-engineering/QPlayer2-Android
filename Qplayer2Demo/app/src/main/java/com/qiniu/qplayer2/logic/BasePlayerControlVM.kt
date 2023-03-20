package com.qiniu.qplayer2.logic

import com.qiniu.qmedia.component.player.QPlayerControlHandler

abstract class BasePlayerControlVM {

     var playerControlHandler: QPlayerControlHandler? = null
        set(value) {
            if (value == null) {
                onClearPlayerListeners()
            } else {
                field = value
                onInstallPlayerControlHandler(field)
            }
        }

    abstract fun onInstallPlayerControlHandler(controlHandler: QPlayerControlHandler?)

    abstract fun onClearPlayerListeners()
}