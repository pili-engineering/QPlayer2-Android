package com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible

import android.view.MotionEvent
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.OnSingleTapListener
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.buffering.BufferingFunctionWidget
import com.qiniu.qplayer2ext.common.thread.HandlerThreads
import com.qiniu.qplayer2ext.commonplayer.layer.function.IOnFunctionWidgetVisibilityChangeListener

class PlayerControlPanelContainerVisibleService
    : IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    IPlayerControlPanelContainerVisibleService,
    OnSingleTapListener,
    IOnFunctionWidgetVisibilityChangeListener<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    QIPlayerStateChangeListener {


    //playing时 Panel 显示一定时间后隐藏
    private var mIsPlaying: Boolean = false
    private var mIsAutoHideEnable = true
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mHideRunnable = Runnable { mPlayerCore.playerControlPanelContainer?.hide() }

    override fun onStart() {
        mPlayerCore.playerGestureLayer?.addOnSingleTapListener(this)
        mPlayerCore.playerFunctionWidgetContainer?.addOnWidgetStateChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerStateChangeListener(this)

    }

    override fun onStop() {
        mPlayerCore.playerGestureLayer?.removeOnSingleTapListener(this)
        mPlayerCore.playerFunctionWidgetContainer?.removeOnWidgetStateChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerStateChangeListener(this)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onSingleTap(event: MotionEvent?): Boolean {
        if (mPlayerCore.playerControlPanelContainer?.isShow() == true) {
            hide()
        } else {
            show()
        }

        return true
    }

    override fun onWidgetShow(token: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        if (!token.clazz.name.equals(BufferingFunctionWidget::class.java.name)) {
            mPlayerCore.playerControlPanelContainer?.hide()
        }
    }

    override fun onWidgetDismiss(token: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
    }

    override fun onStateChanged(state: QPlayerState) {
        if (state == QPlayerState.PLAYING) {
            mIsPlaying = true
            if (mPlayerCore.playerControlPanelContainer?.isShow() == true) {
                show()
            }
        } else {
            mIsPlaying = false
            show()
        }
    }

    private fun hide() {
        mPlayerCore.playerControlPanelContainer?.hide()
        HandlerThreads.remove(HandlerThreads.THREAD_UI, mHideRunnable)
    }

    private fun show() {
        mPlayerCore.playerControlPanelContainer?.show()
        if (mIsPlaying && mIsAutoHideEnable) {
            HandlerThreads.remove(HandlerThreads.THREAD_UI, mHideRunnable)
            HandlerThreads.postDelayed(HandlerThreads.THREAD_UI, mHideRunnable, 5000)
        }
    }

    override fun setAutoHideEnable(enable: Boolean) {
        mIsAutoHideEnable = enable
        if (!mIsAutoHideEnable) {
            HandlerThreads.remove(HandlerThreads.THREAD_UI, mHideRunnable)
        }
    }
}

