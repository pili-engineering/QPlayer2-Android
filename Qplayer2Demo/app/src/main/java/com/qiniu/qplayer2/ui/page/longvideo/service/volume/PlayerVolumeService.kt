package com.qiniu.qplayer2.ui.page.longvideo.service.volume

import android.util.Log
import android.view.ViewGroup
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.VerticalScrollRightListener
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import kotlin.math.abs

class PlayerVolumeService: IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
VerticalScrollRightListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null

    companion object {
        const val TAG = "PlayerVolumeService"
    }
    override fun onStart() {
//        mPlayerCore.playerGestureLayer?.setVerticalScrollRightListener(this)
    }

    override fun onStop() {
//        mPlayerCore.playerGestureLayer?.setVerticalScrollRightListener(null)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }


    override fun onScrollStart() {
//        Log.d(TAG, "onScrollStart")
//        if (mToken == null) {
//            val layoutParams = FunctionWidgetLayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.CENTER
//            layoutParams.functionType = FunctionWidgetLayoutParams.FunctionType.DIALOG
//            layoutParams.touchOutsideDismiss(false)
//
//            mToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
//                PlayerVolumeFunctionWidget::class.java, layoutParams)
//        }
    }

    override fun onScroll(progress: Float) {
//        Log.d(TAG, "onScroll:${progress}")
//
//        if (abs(progress) < 0.06f) {
//            return
//        }
//
//        mPlayerCore.mPlayerContext.getPlayerControlHandler().setAudioVolume((mPlayerCore.mPlayerContext.getPlayerControlHandler().volume.toFloat() - 20 * progress).toInt())
    }

    override fun onScrollStop(progress: Float) {
//        Log.d(TAG, "onScrollStop:${progress}")
//        mToken?.also {
//            mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
//        }
//        mToken = null
    }

    override fun onCancel() {
//        Log.d(TAG, "onCancel")
//        mToken?.also {
//            mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
//        }
//        mToken = null
    }
}