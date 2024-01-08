package com.qiniu.qplayer2.ui.page.longvideo.service.shoot

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.contentValuesOf
import com.qiniu.qmedia.component.player.QIPlayerAudioDataListener
import com.qiniu.qmedia.component.player.QIPlayerShootVideoListener
import com.qiniu.qmedia.component.player.QIPlayerVideoDataListener
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qmedia.component.player.QVideoDataCallbackType
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
import java.io.IOException


class PlayerCaptureService :
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    IPlayerCaptureService, QIPlayerShootVideoListener, QIPlayerVideoDataListener,
    QIPlayerAudioDataListener {

        companion object {
            const val TAG = "PlayerCaptureService"
        }

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mMediaRecord: MediaRecord? = null

    private var mToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null

    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerShootVideoListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerAudioDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerVideoDataListener(this)
    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerShootVideoListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerAudioDataListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerVideoDataListener(this)

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
            return saveRecordToAlbum(it)
        }
        return false
    }

    private fun saveRecordToAlbum(path: String): Boolean {
        Log.d(TAG, "Check AlbumPermission")
        if (mPlayerCore.logicProvider?.checkPhotoAlbumPermission() == true) {
            //保存视频
            val contentValues = contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to System.currentTimeMillis().toString(),
                MediaStore.MediaColumns.MIME_TYPE to "video/mp4",
                MediaStore.MediaColumns.RELATIVE_PATH to "Movies/QPlayer2VideoRecords/"
            )

            mPlayerCore.mAndroidContext?.contentResolver?.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )?.also {dstUri ->

                val sourceFile = File(path)
                val srcUri = Uri.fromFile(sourceFile)
                Log.d(TAG, "START COPY 1 ${srcUri.path}")
                try {
                    val inputStream = mPlayerCore.mAndroidContext?.contentResolver?.openInputStream(srcUri)
                    if (inputStream == null) {
                        Log.d(TAG, "inputStream is null")

                        return false
                    }
                    Log.d(TAG, "START COPY 2")

                    val outputStream = mPlayerCore.mAndroidContext?.contentResolver?.openOutputStream(dstUri)
                    Log.d(TAG, "START COPY 3")

                    if (outputStream == null) {
                        inputStream.close()
                        return false
                    }
                    Log.d(TAG, "START COPY 4")

                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    inputStream.close()
                    outputStream.close()
                    Log.d(TAG, "END COPY")

                    return true
                } catch (e: IOException) {
                    Log.d(TAG, "catch ${e.stackTrace}")

                }
            }
        }
        return false
    }

    override fun onShootSuccessful(
        image: ByteArray,
        width: Int,
        height: Int,
        type: QIPlayerShootVideoListener.ShootVideoType
    ) {

        if (mPlayerCore.logicProvider?.checkPhotoAlbumPermission() == true) {
            //保存截图
            val contentValues = contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to System.currentTimeMillis().toString(),
                MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
                MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/QPlayer2VideoShoots/"
            )

            mPlayerCore.mAndroidContext?.contentResolver?.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )?.apply {
                mPlayerCore.mAndroidContext?.contentResolver?.openOutputStream(this)?.use {
                    it.write(image, 0, image.size)
                }
            }
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
        mMediaRecord?.putAudioData(sampleRate, channelNum, format, channelLayout, data)
    }

    override fun onVideoData(
        width: Int,
        height: Int,
        type: QIPlayerVideoDataListener.QVideoDataType,
        data: ByteArray) {

        mMediaRecord?.putVideoData(width, height, type, data)

    }
}