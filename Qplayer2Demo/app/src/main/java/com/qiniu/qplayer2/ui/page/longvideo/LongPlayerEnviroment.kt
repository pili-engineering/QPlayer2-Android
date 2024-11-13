package com.qiniu.qplayer2.ui.page.longvideo

import android.os.Bundle
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerVideoSwitcher
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
            ServiceOwnerType.PLAYER_PANORAMA_TOUCH_SERVICE.type,
            ServiceOwnerType.PLAYER_CAPTURE_SERVICE.type,
            ServiceOwnerType.PLAYER_VOLUME_SERVICE.type,
            ServiceOwnerType.PLAYER_SUBTITLE_SERVICE.type

        )

    private val mVideoPlayEventListener = object : ICommonPlayerVideoSwitcher.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {
        override fun onPlayableParamsWillChange(oldPlayableParams: LongPlayableParams?,
                                                oldVideoParams: LongVideoParams?,
                                                newPlayableParams: LongPlayableParams,
                                                newVideoParams: LongVideoParams,
                                                switchVideoParams: Bundle?) {

            newPlayableParams.startPos = PlayerSettingRespostory.startPosition

        }

        override fun onVideoParamsStart(videoParams: LongVideoParams) {
            PlayerSettingRespostory.scale = 1.0f
            PlayerSettingRespostory.rotation = 0
            mPlayerCore.mPlayerContext.getPlayerRenderHandler().setScale(PlayerSettingRespostory.scale)
            mPlayerCore.mPlayerContext.getPlayerRenderHandler().setRotation(PlayerSettingRespostory.rotation)
            super.onVideoParamsStart(videoParams)

        }
    }
    override fun start() {
        mPlayerCore.mCommonPlayerVideoSwitcher.addVideoPlayEventListener(mVideoPlayEventListener)

    }

    override fun stop() {
        mPlayerCore.mCommonPlayerVideoSwitcher.removeVideoPlayEventListener(mVideoPlayEventListener)
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