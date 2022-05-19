package com.qiniu.qplayer2.ui.page.longvideo

import android.os.Bundle
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerControlHandler
import com.qiniu.qplayer2ext.commonplayer.enviroment.ICommonPlayerEnvironment
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType

class LongPlayerEnviroment :
    ICommonPlayerEnvironment<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override val name: String
        get() = LongEnviromentType.LONG.type

    override val serviceTypes: Set<String> =
        setOf(
            ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type,
            ServiceOwnerType.PLAYER_TOAST_SERVICE.type,
            ServiceOwnerType.PLAYER_BUFFERING_SERVICE.type,
            ServiceOwnerType.PLAYER_NETWORK_SERVICE.type,
            ServiceOwnerType.PLAYER_PANORAMA_TOUCH_SERVICE.type

        )

    private val mVideoPlayEventListener = object : ICommonPlayerControlHandler.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {
        override fun onPlayableParamsWillChange(oldPlayableParams: LongPlayableParams?,
                                                oldVideoParams: LongVideoParams?,
                                                newPlayableParams: LongPlayableParams,
                                                newVideoParams: LongVideoParams,
                                                switchVideoParams: Bundle?) {

            newPlayableParams.startPos = PlayerSettingRespostory.startPosition

        }
    }
    override fun start() {
        mPlayerCore.mCommonPlayerController.addVideoPlayEventListener(mVideoPlayEventListener)

    }

    override fun stop() {
        mPlayerCore.mCommonPlayerController.removeVideoPlayEventListener(mVideoPlayEventListener)
    }

    override fun authentication(
        playableParams: LongPlayableParams,
        videoParams: LongVideoParams?
    ): Boolean {
        return true
    }


    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}