package com.qiniu.qplayer2.ui.page.longvideo.service.toast

import android.util.Log
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
    QIPlayerQualityListener, QIPlayerVideoDecodeListener,
    QIPlayerCommandNotAllowListener, QIPlayerFormatListener, QIPlayerSEIDataListener, QIPlayerAuthenticationListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerQualityListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerCommandNotAllowListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerFormatListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerSEIDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerAuthenticationListener(this)
    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerQualityListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerCommandNotAllowListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerFormatListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerSEIDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerAuthenticationListener(this)
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

    override fun onVideoDecodeByType(type: QPlayerDecodeType) {

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

    override fun notSupportCodecFormat(codecId: Int) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "不支持的编码类型：$codecId")
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
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onFormatNotSupport() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "流中有播放器不支持的像素格式或音频sample格式")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onSEIData(data: ByteArray) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "SEI DATA:${data.decodeToString()}")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        Log.i("PlayerToastService", "SEI DATA:${data}")
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun on_authentication_failed() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "Qplayer2鉴权失败")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        Log.e("PlayerToastService", "Qplayer2鉴权失败")

        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun on_authentication_success() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "Qplayer2鉴权成功")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        Log.e("PlayerToastService", "Qplayer2鉴权成功")
        mPlayerCore.playerToastContainer?.showToast(toast)
    }


}