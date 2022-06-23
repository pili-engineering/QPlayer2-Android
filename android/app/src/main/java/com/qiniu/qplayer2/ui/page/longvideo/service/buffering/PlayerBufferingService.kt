package com.qiniu.qplayer2.ui.page.longvideo.service.buffering

import android.view.ViewGroup
import com.qiniu.qmedia.component.player.QIPlayerBufferingListener
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.buffering.BufferingFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams

class PlayerBufferingService:
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>, IPlayerBufferingService,
QIPlayerBufferingListener, QIPlayerStateChangeListener{

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mBufferingToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? = null

    private var mIsBuffering = false
    private var mIsPrepared = false
    private var mIsSeeking = false
    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerBufferingChangeListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerStateChangeListener(this)

    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerBufferingListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerStateChangeListener(this)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onBufferingStart() {


        mIsBuffering =true
        updateBufferingWidget()
    }

    override fun onBufferingEnd() {


        mIsBuffering =false
        updateBufferingWidget()
    }


    private fun updateBufferingWidget() {

        //隐藏
        if (mBufferingToken != null) {
            mBufferingToken?.let {
                mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
            }
            mBufferingToken = null
        }

            //显示
        if (!mIsPrepared || mIsBuffering || mIsSeeking) {
            val layoutParams = FunctionWidgetLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.CENTER
            layoutParams.functionType = FunctionWidgetLayoutParams.FunctionType.EMBEDDED_VIEW
            layoutParams.enterAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.exitAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.touchOutsideDismiss(false)
            mBufferingToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(BufferingFunctionWidget::class.java, layoutParams)
        }
    }

    override fun onStateChanged(state: QPlayerState) {
        when(state) {
            QPlayerState.INIT, QPlayerState.PREPARE -> {
                mIsPrepared = false
                mIsSeeking = false

            }
            QPlayerState.SEEKING -> {
                mIsSeeking = true
                mIsPrepared = true
            }
            else -> {
                mIsPrepared = true
                mIsSeeking = false
            }
        }
        updateBufferingWidget()
    }
}