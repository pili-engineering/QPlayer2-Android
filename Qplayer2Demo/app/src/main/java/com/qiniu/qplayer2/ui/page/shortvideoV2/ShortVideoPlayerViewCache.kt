package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.content.Context
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.ui.page.shortvideo.MediaItemContextManager

class ShortVideoPlayerViewCache(context: Context, private val mPlayItemManager: PlayItemManager,
                                externalFilesDir: String):IShortVideoPlayerViewCacheRecycler {
    private val mMediaItemContextManager = MediaItemContextManager(mPlayItemManager, externalFilesDir)
    private val mSurfacePlayerViewManager = SurfacePlayerViewManager(context)
    private var mCurrentPostion = 0
    private var mMaxChangePosition = 0

    fun start() {
        mSurfacePlayerViewManager.start()
        mMediaItemContextManager.start()
    }

    fun stop() {
        mSurfacePlayerViewManager.stop()
        mMediaItemContextManager.stop()
    }

    fun changePosition(position: Int) {
        mCurrentPostion = position

        mMediaItemContextManager.updateMediaItemContext(mCurrentPostion)
        if (mCurrentPostion > mMaxChangePosition) {

            mPlayItemManager.getOrNullByPosition(mCurrentPostion + 1)?.also {
                mMediaItemContextManager.fetchMediaItemContextById(it.id)?.also { mediaItemContext ->
                    mSurfacePlayerViewManager.prepare(it.id, mediaItemContext)
                }
            }
            mMaxChangePosition = mCurrentPostion
        }
    }

    override fun fetchSurfacePlayerView(id: Int): QSurfacePlayerView? {
        return mPlayItemManager.getOrNullById(id)?.let {
            val mediaItemContext = mMediaItemContextManager.fetchMediaItemContextById(id)
            mSurfacePlayerViewManager.fetchSurfacePlayerView(it, mediaItemContext)
        }
    }

    public override fun recycleSurfacePlayerView(surfacePlayerView: QSurfacePlayerView) {
        mSurfacePlayerViewManager.recycleSurfacePlayerView(surfacePlayerView)
    }
}