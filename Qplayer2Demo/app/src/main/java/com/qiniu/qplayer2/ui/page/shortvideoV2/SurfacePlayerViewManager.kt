package com.qiniu.qplayer2.ui.page.shortvideoV2

import com.qiniu.qmedia.component.player.QMediaModel
import com.qiniu.qmedia.ui.QSurfacePlayerView
import android.content.Context

class SurfacePlayerViewManager(private val mContext: Context) {

    companion object {
        private  const val MAX_COUNT = 3;
    }
    private val mSurfacePlayerViews = ArrayList<QSurfacePlayerView>()
    private var mCurrentCacheCount = 0


    public fun start() {

    }

    public fun stop() {
        mSurfacePlayerViews.forEach {
            it.playerControlHandler.release()
        }
    }

    public fun fetchSurfacePlayerView(): QSurfacePlayerView {

        return if (mSurfacePlayerViews.size > 0) {
            val surfacePlayerView = mSurfacePlayerViews[0]
            mSurfacePlayerViews.removeAt(0)
            surfacePlayerView
        } else {
            if (mCurrentCacheCount < MAX_COUNT) {
                mCurrentCacheCount += 1
                val surfaceView = QSurfacePlayerView(mContext)
                surfaceView.playerControlHandler.init(mContext)
                return surfaceView
            } else {
                throw Exception("error cache size")
            }
        }
    }

    public fun recycleSurfacePlayerView(surfacePlayerView: QSurfacePlayerView) {
        mSurfacePlayerViews.add(surfacePlayerView)
    }
}