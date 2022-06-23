package com.qiniu.qplayer2.logic

import com.qiniu.qmedia.component.player.QPlayerControlHandler

abstract class BasePlayerControlVM {

     var playerControlHandler: QPlayerControlHandler? = null
        set(value) {
            field = value
            onSetPlayerControlHandler(field)
        }

    abstract fun onSetPlayerControlHandler(controlHandler: QPlayerControlHandler?)
}