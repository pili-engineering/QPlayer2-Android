package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import com.qiniu.qmedia.component.player.QIPlayerDownloadListener
import com.qiniu.qmedia.component.player.QIPlayerProgressListener
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerControlHandler
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible.IPlayerControlPanelContainerVisibleService


class CommonPlayerSeekWidget : AppCompatSeekBar,
    SeekBar.OnSeekBarChangeListener,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private var mIsNeedUpdateProgress: Boolean = false
    private var mIsTrackingTouch: Boolean = false
    private val mPlayerProgressListener = object : QIPlayerProgressListener {
        override fun onProgressChanged(duration: Long, progress: Long) {
            if (mIsNeedUpdateProgress && !mIsTrackingTouch) {
                if (duration > 0) {
                    this@CommonPlayerSeekWidget.progress = (progress * 1000 / duration).toInt()
                }
            }

        }
    }

    private val mPlayerDownloadListener = object : QIPlayerDownloadListener {
        override fun onDownloadChanged(speed: Long, bufferProgress: Long) {
            if (mPlayerCore.mPlayerContext.getPlayerControlHandler().duration > 0) {
                this@CommonPlayerSeekWidget.secondaryProgress =
                    (bufferProgress * 1000 / mPlayerCore.mPlayerContext.getPlayerControlHandler().duration).toInt()
            }

        }
    }

    private val mPlayerStateChangeListener = object: QIPlayerStateChangeListener {
        override fun onStateChanged(state: QPlayerState) {
            mIsNeedUpdateProgress = state == QPlayerState.PLAYING
        }

    }

    private val mVideoPlayEventListener = object : ICommonPlayerControlHandler.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {


        override fun onPlayableParamsStart(
            playableParams: LongPlayableParams,
            videoParams: LongVideoParams
        ) {
            super.onPlayableParamsStart(playableParams, videoParams)
            setSeekBarClickable(!playableParams.isLive)
            progress = 0
            secondaryProgress = 0
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    private fun setSeekBarClickable(enable: Boolean) {
        if (enable) {
            //启用状态
            isClickable = true
            isEnabled = true
            isSelected = true
            isFocusable = true
//            val drawable = resources.getDrawable(R.drawable.yellow_mid_img_40)
//            thumb = drawable
        } else {
            //禁用状态
            isClickable = false
            isEnabled = false
            isSelected = false
            isFocusable = false
            progress = 0
            secondaryProgress = 0
////            val drawable = resources.getDrawable(R.drawable.seek_bar_grey_img_40)
//            thumb = drawable
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerControlPanelContainerVisibleService>(
            ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type
        )
        service?.setAutoHideEnable(false)
        mIsTrackingTouch = true

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().also {
            it.seek(progress * it.duration / 1000)
        }

        val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerControlPanelContainerVisibleService>(
            ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type
        )
        service?.setAutoHideEnable(true)
        mIsTrackingTouch = false
        mIsNeedUpdateProgress = false
    }

    override fun onWidgetActive() {
        mPlayerCore.mCommonPlayerController.getCurrentPlayableParams().let { playableParams->

            if (playableParams?.isLive == false) {
                mPlayerCore.mPlayerContext.getPlayerControlHandler().let {
                    mIsNeedUpdateProgress = it.currentPlayerState == QPlayerState.PLAYING
                    if (it.duration > 0) {
                        progress = (it.currentPosition * 1000 / it.duration).toInt()
                        secondaryProgress = (it.bufferPositon * 1000 / it.duration).toInt()
                    }
                }
                setSeekBarClickable(true)
            } else {
                setSeekBarClickable(false)
            }
        }

        mPlayerCore.mPlayerContext.getPlayerControlHandler().also {
            it.addPlayerDownloadChangeListener(mPlayerDownloadListener)
            it.addPlayerProgressChangeListener(mPlayerProgressListener)
            it.addPlayerStateChangeListener(mPlayerStateChangeListener)
        }
        setOnSeekBarChangeListener(this)
        mPlayerCore.mCommonPlayerController.addVideoPlayEventListener(mVideoPlayEventListener)

    }

    override fun onWidgetInactive() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().also {
            it.removePlayerProgressListener(mPlayerProgressListener)
            it.removePlayerDownloadListener(mPlayerDownloadListener)
            it.removePlayerStateChangeListener(mPlayerStateChangeListener)

        }

        mPlayerCore.mCommonPlayerController.removeVideoPlayEventListener(mVideoPlayEventListener)
        setOnSeekBarChangeListener(null)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}