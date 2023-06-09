package com.qiniu.qplayer2.ui.page.longvideo.service.subtitle

import android.view.ViewGroup
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QIPlayerSubtitleListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.subtitle.SubtitleFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig
import com.qiniu.qplayer2ext.commonplayer.screen.ICommonPlayerScreenChangedListener
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService

class PlayerSubtitleService :
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>, IPlayerSubtitleService,
    QIPlayerSubtitleListener, QIPlayerStateChangeListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mSubtitleToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null



    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerSubtitleListener(this)
//        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerStateChangeListener(this)

    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerSubtitleListener(this)
//        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerStateChangeListener(this)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }


    override fun on_subtitle_text_change(text: String) {
        updateSubtitleWidget(text)
    }

    override fun on_subtitle_name_change(name: String) {
    }

    override fun on_subtitle_close() {
        updateSubtitleWidget("")
    }

    override fun on_subtitle_loaded(name: String, result: Boolean) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, if(result) "字幕：{$name} 加载 成功" else "字幕：{$name} 加载 失败")
            .duration(PlayerToastConfig.DURATION_3)
            .build()

        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    override fun on_subtitle_decoded(name: String, result: Boolean) {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, if(result) "字幕：{$name}解析成功" else "字幕：{$name}解析失败")
            .duration(PlayerToastConfig.DURATION_3)
            .build()

        mPlayerCore.playerToastContainer?.showToast(toast)
    }

    private fun updateSubtitleWidget(text: String) {
        //显示
        if (mSubtitleToken == null) {
            val layoutParams = FunctionWidgetLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.BOTTOM
            layoutParams.functionType = FunctionWidgetLayoutParams.FunctionType.EMBEDDED_VIEW
            layoutParams.enterAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.exitAnim = FunctionWidgetLayoutParams.NO_ANIMATION
            layoutParams.touchEnable(false)
            layoutParams.touchOutsideDismiss(false)
            mSubtitleToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
                SubtitleFunctionWidget::class.java, layoutParams
            )
        }

        mPlayerCore.playerFunctionWidgetContainer?.updateWidget(mSubtitleToken!!,
            SubtitleFunctionWidget.PlayerSubtitleFunctionWidgetConfiguration(text))

    }

    override fun onStateChanged(state: QPlayerState) {

    }


}