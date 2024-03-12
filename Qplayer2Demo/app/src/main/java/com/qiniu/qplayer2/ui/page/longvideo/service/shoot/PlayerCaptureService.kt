package com.qiniu.qplayer2.ui.page.longvideo.service.shoot

import android.util.Log
import android.view.ViewGroup
import com.qiniu.qmedia.component.player.QIPlayerAudioDataListener
import com.qiniu.qmedia.component.player.QIPlayerBufferingListener
import com.qiniu.qmedia.component.player.QIPlayerShootVideoListener
import com.qiniu.qmedia.component.player.QIPlayerVideoDataListener
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qmedia.component.player.QVideoDataCallbackType
import com.qiniu.qplayer2.common.system.SaveUtils
import com.qiniu.qplayer2.tools.MediaRecord
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.shoot.PlayerShootVideoFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import java.io.File


class PlayerCaptureService :
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    IPlayerCaptureService, QIPlayerShootVideoListener, QIPlayerVideoDataListener,
    QIPlayerAudioDataListener, QIPlayerBufferingListener {

        companion object {
            const val TAG = "PlayerCaptureService"
        }

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mMediaRecord: MediaRecord? = null

    private var mToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null

    private var mIsBuffering = false
    override fun onStart() {
        mIsBuffering = mPlayerCore.mPlayerContext.getPlayerControlHandler().isBuffering

        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerShootVideoListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerAudioDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerBufferingChangeListener(this)
    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerShootVideoListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerAudioDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerBufferingChangeListener(this)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun shootVideo() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().shootVideo()
    }

    override fun startRecord() {

        mMediaRecord = MediaRecord(mPlayerCore.mAndroidContext?.getExternalFilesDir(null)?.path?: "")

        var videoEnable = false
        var audioEnable = false
        mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
            it.mediaModel.streamElements.forEach { streamElement->

                if (streamElement.urlType == QURLType.QAUDIO_AND_VIDEO.value) {
                    videoEnable = true
                    audioEnable = true
                } else if (streamElement.urlType == QURLType.QAUDIO.value) {
                    audioEnable = true
                } else if (streamElement.urlType == QURLType.QVIDEO.value) {
                    videoEnable = true
                }
            }
            mMediaRecord?.start(videoEnable, audioEnable)
            mPlayerCore.mPlayerContext.getPlayerControlHandler().setVideoDataCallbackType(QVideoDataCallbackType.NV12)
            mPlayerCore.mPlayerContext.getPlayerControlHandler().setAudioDataCallbackEnable(true)
            mPlayerCore.mPlayerContext.getPlayerControlHandler().setVideoDataCallbackEnable(true)
        }
    }

    override fun stopRecord(): Boolean {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().setAudioDataCallbackEnable(false)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().setVideoDataCallbackEnable(false)

        val path = mMediaRecord?.stop()
        mMediaRecord = null

        path?.also {
            val fileSize = SaveUtils.getFileSize(it)
            if(fileSize > 100) {
                return saveRecordToAlbum(it)
            } else {
                return false
            }
        }
        return false
    }

    private fun saveRecordToAlbum(path: String): Boolean {
        Log.d(TAG, "Check AlbumPermission")
        var result = false
        mPlayerCore.mAndroidContext?.let {
            result = SaveUtils.saveVideoToAlbum(it, path)
//            val text  = if (result) "视频保存成功" else "视频保存失败"
//            val toast = PlayerToast.Builder()
//                .toastItemType(PlayerToastConfig.TYPE_NORMAL)
//                .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
//                .setExtraString(PlayerToastConfig.EXTRA_TITLE, text)
//                .duration(PlayerToastConfig.DURATION_3)
//                .build()
//            mPlayerCore.playerToastContainer?.showToast(toast)
        }
        return result
    }

    override fun onShootSuccessful(
        image: ByteArray,
        width: Int,
        height: Int,
        type: QIPlayerShootVideoListener.ShootVideoType
    ) {

        mPlayerCore.mAndroidContext?.let {
            val text  = if (SaveUtils.saveImageToAlbumImpl(it, image)) "截图保存成功" else "截图保存失败"
            val toast = PlayerToast.Builder()
                .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                .setExtraString(PlayerToastConfig.EXTRA_TITLE, text)
                .duration(PlayerToastConfig.DURATION_3)
                .build()
            mPlayerCore.playerToastContainer?.showToast(toast)
        }

        mToken?.also {
            mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
        }
        //展示截图
        val layoutParams = FunctionWidgetLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.CENTER
        layoutParams.functionType = FunctionWidgetLayoutParams.FunctionType.DIALOG
        layoutParams.enterAnim = FunctionWidgetLayoutParams.NO_ANIMATION
        layoutParams.exitAnim = FunctionWidgetLayoutParams.NO_ANIMATION
        layoutParams.touchOutsideDismiss(true)
        val configuration =
            PlayerShootVideoFunctionWidget.PlayerShootVideoFunctionWidgetConfiguration(image)
        mToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
            PlayerShootVideoFunctionWidget::class.java, layoutParams, configuration
        )

    }

    override fun onShootFailed() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "截图失败...")
            .duration(PlayerToastConfig.DURATION_3)
            .build()

        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun onAudioData(
        sampleRate: Int,
        format: QIPlayerAudioDataListener.QSampleFormat,
        channelNum: Int,
        channelLayout: QIPlayerAudioDataListener.QChannelLayout,
        data: ByteArray
    ) {
        if (!mIsBuffering) {
            mMediaRecord?.putAudioData(sampleRate, channelNum, format, channelLayout, data)
        }
    }

    override fun onVideoData(
        width: Int,
        height: Int,
        type: QIPlayerVideoDataListener.QVideoDataType,
        data: ByteArray) {
        if (!mIsBuffering) {
            mMediaRecord?.putVideoData(width, height, type, data)
        }
    }

    override fun onBufferingStart() {
        mIsBuffering = true
    }

    override fun onBufferingEnd() {
        mIsBuffering = false
    }
}