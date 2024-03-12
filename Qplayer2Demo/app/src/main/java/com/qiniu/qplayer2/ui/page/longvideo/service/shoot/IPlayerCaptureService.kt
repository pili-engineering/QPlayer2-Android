package com.qiniu.qplayer2.ui.page.longvideo.service.shoot

interface IPlayerCaptureService {

    fun shootVideo()

    fun startRecord()

    fun stopRecord(): Boolean

}