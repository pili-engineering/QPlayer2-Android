package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible.IPlayerControlPanelContainerVisibleService
import com.qiniu.qplayer2.ui.page.longvideo.service.shoot.IPlayerCaptureService
import com.qiniu.qplayer2.ui.page.longvideo.service.toast.IPlayerToastService
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig

class CommonPlayerCaptureWidget: AppCompatImageView, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>, QIPlayerStateChangeListener {

    companion object {
        const val TAG = "PlayerCaptureWidget"
    }
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mRecording = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)

    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val drawableCompat = ContextCompat.getDrawable(context, R.drawable.qmedia_ic_picture_bold)
        if (drawableCompat != null) {
            setImageDrawable(drawableCompat)
        }
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "onClick")

        val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerCaptureService>(
            ServiceOwnerType.PLAYER_CAPTURE_SERVICE.type)
        service?.also {
            it.shootVideo()
        }
    }

    override fun onLongClick(v: View?): Boolean {
        Log.d(TAG, "onLongClick")
        startRecord()

        return false
    }

    private fun startRecord() : Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            return false
        }
        if (mPlayerCore.mPlayerContext.getPlayerControlHandler().currentPlayerState == QPlayerState.PLAYING) {
            val streamElements = mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.mediaModel?.streamElements
            var hasVideo = false
            var hasAudio = false
            streamElements?.forEach {
                if (it.urlType == QURLType.QAUDIO_AND_VIDEO.value) {
                    hasVideo = true
                    hasAudio = true
                } else if (it.urlType == QURLType.QAUDIO.value) {
                    hasAudio = true
                } else if (it.urlType == QURLType.QVIDEO.value) {
                    hasVideo = true
                }
            }
            if (!(hasAudio && hasVideo)) {
                return false
            }

            mRecording = true
            val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerCaptureService>(
                ServiceOwnerType.PLAYER_CAPTURE_SERVICE.type)
            service?.also {
                it.startRecord()
                val controlPanelService = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerControlPanelContainerVisibleService>(
                    ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type)
                controlPanelService?.setAutoHideEnable(false)
                val toast = PlayerToast.Builder()
                    .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                    .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                    .setExtraString(PlayerToastConfig.EXTRA_TITLE, "开始录制")
                    .duration(PlayerToastConfig.DURATION_3)
                    .build()
                mPlayerCore.playerToastContainer?.showToast(toast)
            }
            return true
        }
        return false
    }

    private fun stopRecord(): Boolean {
        if (mRecording) {
            val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerCaptureService>(
                ServiceOwnerType.PLAYER_CAPTURE_SERVICE.type)
            service?.also {
                val result = it.stopRecord()
                val controlPanelService = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerControlPanelContainerVisibleService>(
                    ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type)
                controlPanelService?.setAutoHideEnable(true)

                val toast = PlayerToast.Builder()
                    .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                    .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                    .setExtraString(PlayerToastConfig.EXTRA_TITLE, "结束录制:" + if (result) "成功 前往相册查看" else "失败")
                    .duration(PlayerToastConfig.DURATION_3)
                    .build()
                mPlayerCore.playerToastContainer?.showToast(toast)
            }
            mRecording = false
            return true
        }
        return false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            Log.d(TAG, "onTouch ACTION_UP")
            return stopRecord()

        }
        return false
    }

    override fun onWidgetActive() {
        setOnClickListener(this)
        setOnLongClickListener(this)
        setOnTouchListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerStateChangeListener(this)

    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
        setOnLongClickListener(null)
        setOnTouchListener(null)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerStateChangeListener(this)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onStateChanged(state: QPlayerState) {
        if (state != QPlayerState.PLAYING) {
            stopRecord()
        }
    }
}