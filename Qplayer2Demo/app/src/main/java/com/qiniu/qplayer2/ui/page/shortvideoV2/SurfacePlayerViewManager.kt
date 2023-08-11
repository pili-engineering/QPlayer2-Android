package com.qiniu.qplayer2.ui.page.shortvideoV2

import com.qiniu.qmedia.ui.QSurfacePlayerView
import android.content.Context
import android.util.Log
import com.qiniu.qmedia.component.player.QMediaItemContext
import com.qiniu.qmedia.component.player.QPlayerSetting

class SurfacePlayerViewManager(private val mContext: Context) {

    companion object {
        private const val MAX_COUNT = 3
        private const val INVALID_VIDEO_ID = -9999
        private const val TAG = "PlayerViewManager"
    }

    private val mSurfacePlayerViews = ArrayList<QSurfacePlayerView>()
    private var mCurrentCacheCount = 0
    private var mPreRenderPlayerView: QSurfacePlayerView? = null
    private var mPreRenderVideoId: Int = INVALID_VIDEO_ID

    public fun start() {

    }

    public fun stop() {
        mSurfacePlayerViews.forEach {
            it.playerControlHandler.release()
        }
    }

    public fun isPreRenderValaid(): Boolean {
        return mPreRenderVideoId != INVALID_VIDEO_ID
    }

    public fun recyclePreRenderPlayerView(): Boolean {
        var ret = false
        mPreRenderPlayerView?.let {
            it.playerControlHandler.stop()
            recycleSurfacePlayerView(it)
            ret = true
        }
        mPreRenderVideoId = INVALID_VIDEO_ID
        mPreRenderPlayerView = null

        return ret
    }


    public fun prepare(id: Int, mediaItemContext: QMediaItemContext): Boolean {

        if (mPreRenderPlayerView != null) {
            return false
        }
        mPreRenderPlayerView = fetchSurfacePlayerView()
        Log.d(TAG, "prepare id=${id} player view is $mPreRenderPlayerView")
        return mPreRenderPlayerView?.run {

            playerControlHandler.setStartAction(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE)
            playerControlHandler.playMediaItem(mediaItemContext)
            mPreRenderVideoId = id
            true
        } ?: false
    }

    fun fetchSurfacePlayerView(
        playItem: PlayItem,
        mediaItemContext: QMediaItemContext?
    ): QSurfacePlayerView? {

        if (playItem.id == mPreRenderVideoId && mPreRenderPlayerView != null) {
            val ret = mPreRenderPlayerView!!
            mPreRenderPlayerView = null
            mPreRenderVideoId = INVALID_VIDEO_ID
            ret.playerControlHandler.resumeRender()
            Log.d(TAG, "fetchSurfacePlayerView::PreRender playerview id=${playItem.id} size=${mSurfacePlayerViews.size} current-count=${mCurrentCacheCount}")
            return ret
        } else if (mediaItemContext != null) {
            val ret = fetchSurfacePlayerView()
            ret?.playerControlHandler?.setStartAction(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING)
            ret?.playerControlHandler?.playMediaItem(mediaItemContext)
            Log.d(TAG, "fetchSurfacePlayerView::PreLoad playerview id=${playItem.id} size=${mSurfacePlayerViews.size} current-count=${mCurrentCacheCount}")
            return ret
        } else {
            val ret = fetchSurfacePlayerView()
            ret?.playerControlHandler?.setStartAction(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING)
            ret?.playerControlHandler?.playMediaModel(playItem.mediaModel, 0)
            Log.d(TAG, "fetchSurfacePlayerView::normal playerview id=${playItem.id} size=${mSurfacePlayerViews.size} current-count=${mCurrentCacheCount}")
            return ret
        }
    }


    private fun fetchSurfacePlayerView(): QSurfacePlayerView? {

        return if (mSurfacePlayerViews.size > 0) {
            val surfacePlayerView = mSurfacePlayerViews[0]
            mSurfacePlayerViews.removeAt(0)
            surfacePlayerView
        } else {
            if (mCurrentCacheCount < MAX_COUNT) {
                mCurrentCacheCount += 1
                val surfaceView = QSurfacePlayerView(mContext)
                surfaceView.playerControlHandler.init(mContext)
                surfaceView
            } else {
                null
            }
        }
    }

    public fun recycleSurfacePlayerView(surfacePlayerView: QSurfacePlayerView) {
        mSurfacePlayerViews.add(surfacePlayerView)
        Log.d(TAG, "recycleSurfacePlayerView size=${mSurfacePlayerViews.size} current-count=${mCurrentCacheCount}")

    }
}