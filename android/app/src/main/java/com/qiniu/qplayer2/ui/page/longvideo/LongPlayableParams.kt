package com.qiniu.qplayer2.ui.page.longvideo

import com.qiniu.qmedia.component.player.QMediaModel
import com.qiniu.qplayer2ext.commonplayer.data.DisplayOrientation
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayableParams

class LongPlayableParams(
    mediaModel: QMediaModel,
    controlPanelType: String,
    displayOrientation: DisplayOrientation,
    environmentType: String,
    startPos: Long,
    val isLive: Boolean
): CommonPlayableParams(mediaModel, controlPanelType, displayOrientation, environmentType, startPos) {

}