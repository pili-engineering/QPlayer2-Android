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
    QIPlayerCommandNotAllowListener, QIPlayerFormatListener,
    QIPlayerSEIDataListener, QIPlayerAuthenticationListener,
    QIPlayerVideoFrameSizeChangeListener, QIPlayerSeekListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private var mSEICount: Long = 0
    private var mSEIDataStr : String = ""
    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerQualityListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerCommandNotAllowListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerFormatListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerSEIDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerAuthenticationListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoFrameSizeChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerSeekListener(this)

    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerQualityListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoDecodeTypeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerCommandNotAllowListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerFormatListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerSEIDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerAuthenticationListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoFrameSizeChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerSeekListener(this)

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

    override fun onDecodeFailed(retry: Boolean) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "解码失败 重试：$retry")
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
        if (data.size > 16) {

            //uuid_iso_iec_11578
            val uuid = data.slice(0 until 16).toString()
            val seiData = data.slice(16 until data.size).toByteArray()

            if (mSEIDataStr == seiData.decodeToString()) {
                ++mSEICount
            } else {
                mSEICount = 0
                mSEIDataStr = seiData.decodeToString()
            }

            val toast = PlayerToast.Builder()
                .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                .setExtraString(PlayerToastConfig.EXTRA_TITLE, "$mSEICount UUID:${uuid} SEI DATA:${seiData.decodeToString()}")
                .duration(PlayerToastConfig.DURATION_3)
                .build()

            Log.i("PlayerToastService", "$mSEICount  UUID:${uuid}  SEI Decode DATA:${seiData.decodeToString()}")
            mPlayerCore.playerToastContainer?.showToast(toast)
        }

    }

    override fun on_authentication_failed(error_type: QAuthenticationErrorType) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "Qplayer2鉴权失败-${error_type}")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        Log.e("PlayerToastService", "Qplayer2鉴权失败-${error_type}")

        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun on_authentication_success() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "Qplayer2鉴权成功")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        Log.d("PlayerToastService", "Qplayer2鉴权成功")
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onVideoFrameSizeChanged(width: Int, height: Int) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "视频宽高：${width}X${height}")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onSeekFailed() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "SEEK失败")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onSeekSuccess() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "SEEK成功")
            .duration(PlayerToastConfig.DURATION_3)
            .build()
        mPlayerCore.playerToastContainer?.showToast(toast)
    }
}