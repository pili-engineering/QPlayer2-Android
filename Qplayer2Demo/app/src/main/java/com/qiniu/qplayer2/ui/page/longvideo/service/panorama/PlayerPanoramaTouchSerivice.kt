package com.qiniu.qplayer2.ui.page.longvideo.service.panorama

import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qmedia.component.player.QVideoRenderType
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerVideoSwitcher
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.IOnResizableGestureListener
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.OnTouchListener
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.detector.RotateGestureDetector
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService
import kotlin.math.abs

class PlayerPanoramaTouchSerivice :
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    QIPlayerStateChangeListener, OnTouchListener,
    ICommonPlayerVideoSwitcher.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var PanoramaTouchEnable = false

    private var mPreTouchPoint: Pair<Float, Float>? = null
    private var mCurrentRotationX = 0F
    private var mCurrentRotationY = 0F

    companion object {
        const val TAG = "PanoramaTouchSerivice"
    }
    override fun onStart() {
        mPlayerCore.mCommonPlayerVideoSwitcher.addVideoPlayEventListener(this)
    }

    override fun onStop() {
        mPlayerCore.mCommonPlayerVideoSwitcher.removeVideoPlayEventListener(this)
        mPlayerCore.playerGestureLayer?.setOnTouchListener(null)
        mPlayerCore.playerGestureLayer?.setResizableGestureListener(null)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onStateChanged(state: QPlayerState) {

    }

    override fun onPlayableParamsStart(
        playableParams: LongPlayableParams,
        videoParams: LongVideoParams
    ) {
        val videoRenderType = playableParams.mediaModel.streamElements.getOrNull(0)?.videoRenderType

        if (videoRenderType == QVideoRenderType.PANORAMA_EQUIRECT_ANGULAR.value
//            || videoRenderType == QVideoRenderType.PANORAMA_ANGULAR_CUBEMAP.value
        ) {
            PanoramaTouchEnable = true
            mPlayerCore.playerGestureLayer?.setOnTouchListener(this)
            mPlayerCore.playerGestureLayer?.setResizableGestureListener(PanoramaOnResizableGestureListener(mPlayerCore))

        } else {
            PanoramaTouchEnable = false
            mPlayerCore.playerGestureLayer?.setOnTouchListener(null)
            mPlayerCore.playerGestureLayer?.setResizableGestureListener(null)

        }
    }

    override fun onTouch(event: MotionEvent?) {
        event ?: return
        val getstureLayerHeight = mPlayerCore.playerGestureLayer?.getGestureHeight()
        val getstureLayerWidth = mPlayerCore.playerGestureLayer?.getGestureWidth()

        if (getstureLayerHeight == null || getstureLayerWidth == null) return

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPreTouchPoint = Pair(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val prePoint = mPreTouchPoint ?: return
                val offsetX = event.x - prePoint.first

                val offsetY = event.y - prePoint.second

                if (offsetX != 0F || offsetY != 0F) {
                    val rx = (offsetX / getstureLayerWidth.toFloat()) * 180
                    mCurrentRotationX += rx
                    mCurrentRotationX %= 360
                    val ry = (offsetY / getstureLayerHeight.toFloat()) * 180
                    mCurrentRotationY += ry
                    mCurrentRotationY %= 360
                    val positive = mCurrentRotationY >= 0
                    if (abs(mCurrentRotationY) > 180) {
                        mCurrentRotationY = if (positive) 180F else -180F
                    }
                    mPlayerCore.mPlayerContext.getPlayerRenderHandler().setPanoramaViewRotate(mCurrentRotationY, -mCurrentRotationX)
                    Log.i(TAG, "change panorama roateX=$mCurrentRotationY, roateY=$mCurrentRotationX")
                }
                mPreTouchPoint = Pair(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mPreTouchPoint = null
        }
    }
}