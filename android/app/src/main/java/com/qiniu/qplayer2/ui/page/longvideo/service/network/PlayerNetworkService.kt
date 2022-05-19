package com.qiniu.qplayer2.ui.page.longvideo.service.network

import android.view.ViewGroup
import com.qiniu.qmedia.component.player.QIPlayerMediaListener
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.error.ErrorFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams

class PlayerNetworkService:
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>, QIPlayerMediaListener,
    QIPlayerStateChangeListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mErrorToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? = null

    private var mNotifyTime = 0;
    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerReconnectListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerStateChangeListener(this)
    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerReconnectListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerStateChangeListener(this)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onReconnectStart(
        userType: String,
        urlType: QURLType,
        url: String,
        retryTime: Int
    ) {
        if (mNotifyTime % 5 == 0) {
            val toast = PlayerToast.Builder()
                .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                .setExtraString(PlayerToastConfig.EXTRA_TITLE, "开始重连...")
                .duration(PlayerToastConfig.DURATION_3)
                .build()

            mPlayerCore.playerToastContainer?.showToast(toast)
        }

    }

    override fun onReconnectEnd(
        userType: String,
        urlType: QURLType,
        url: String,
        retryTime: Int,
        error: QIPlayerMediaListener.OpenError
    ) {
        if (error == QIPlayerMediaListener.OpenError.NONE || mNotifyTime % 5 == 0) {
            val toast = PlayerToast.Builder()
                .toastItemType(PlayerToastConfig.TYPE_NORMAL)
                .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
                .setExtraString(PlayerToastConfig.EXTRA_TITLE, if (error == QIPlayerMediaListener.OpenError.NONE)"重连成功." else "重连失败")
                .duration(PlayerToastConfig.DURATION_3)
                .build()

            mPlayerCore.playerToastContainer?.showToast(toast)
            mNotifyTime = 0;
        }


        mNotifyTime++
    }

    override fun onOpenFailed(
        userType: String,
        urlType: QURLType,
        url: String,
        error: QIPlayerMediaListener.OpenError
    ) {

        if (error != QIPlayerMediaListener.OpenError.INTERRUPT) {
            val layoutParams = FunctionWidgetLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.functionType = FunctionWidgetLayoutParams.FUNCTION_TYPE_EMBEDDED_VIEW

            layoutParams.layoutType = FunctionWidgetLayoutParams.LAYOUT_TYPE_IN_CENTER
            layoutParams.enterAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.exitAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.touchOutsideDismiss(false)

            mErrorToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
                ErrorFunctionWidget::class.java, layoutParams)
        }
    }

    override fun onStateChanged(state: QPlayerState) {
        if (state == QPlayerState.PREPARE) {
            mErrorToken?.let {
                mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
            }
            mErrorToken = null
        }
    }
}