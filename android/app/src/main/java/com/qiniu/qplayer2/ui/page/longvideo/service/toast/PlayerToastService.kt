package com.qiniu.qplayer2.ui.page.longvideo.service.toast

import com.qiniu.qmedia.component.player.*
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class PlayerToastService
    : IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>, IPlayerToastService,
    QIPlayerQualityListener, QIPlayerVideoDecodeTypeListener, QIPlayerCommandNotAllowListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerQualityChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerCommandNotAllowListener(this)

    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerQualityChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerCommandNotAllowListener(this)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onQualitySwitchStart(
        userType: String,
        urlType: QURLType,
        newQuality: Int,
        oldQuality: Int
    ) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "清晰度开始切换至$newQuality,请稍候...")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onQualitySwitchComplete(
        userType: String,
        urlType: QURLType,
        newQuality: Int,
        oldQuality: Int
    ) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "清晰度已成功切换至【$newQuality】")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onQualitySwitchCanceled(
        userType: String,
        urlType: QURLType,
        newQuality: Int,
        oldQuality: Int
    ) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "清晰度【$newQuality】切换取消")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onQualitySwitchFailed(
        userType: String,
        urlType: QURLType,
        newQuality: Int,
        oldQuality: Int
    ) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "清晰度切换至【$newQuality】失败")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onQualitySwitchRetryLater(userType: String, urlType: QURLType, newQuality: Int) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "正在切换中请稍后再试")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onDecodeByType(type: QPlayerDecodeType) {

        val type = when(type) {
            QPlayerDecodeType.NONE -> "无"
            QPlayerDecodeType.FIRST_FRAME_ACCEL -> "混解"
            QPlayerDecodeType.HARDWARE_BUFFER -> "buffer硬解"
            QPlayerDecodeType.HARDWARE_SURFACE -> "surface硬解"
            QPlayerDecodeType.SOFTWARE -> "软解"

            else -> "无"
        }
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "解码器类型：$type")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onCommandNotAllow(commandName: String, state: QPlayerState) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "not allow $commandName 状态:$state")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
    }
}