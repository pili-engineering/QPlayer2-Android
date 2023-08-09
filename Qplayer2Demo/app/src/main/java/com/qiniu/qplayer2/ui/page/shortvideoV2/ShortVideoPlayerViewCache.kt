package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.content.Context
import android.util.Log
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.ui.page.shortvideo.MediaItemContextManager

class ShortVideoPlayerViewCache(context: Context, private val mPlayItemManager: PlayItemManager,
                                externalFilesDir: String):IShortVideoPlayerViewCacheRecycler {
    private val mMediaItemContextManager = MediaItemContextManager(mPlayItemManager, externalFilesDir)
    private val mSurfacePlayerViewManager = SurfacePlayerViewManager(context)
    private var mCurrentPostion = 0
    private var mMaxChangePosition = 0

    companion object {
        private const val TAG = "PlayerViewCache"
    }

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
        if (mCurrentPostion >= mMaxChangePosition) {
            Log.d(TAG, "change position pos=${position}")
            mPlayItemManager.getOrNullByPosition(mCurrentPostion + 1)?.also {
                if (!mSurfacePlayerViewManager.isPreRenderValaid()) {
                    mMediaItemContextManager.fetchMediaItemContextById(it.id)?.also { mediaItemContext ->
                        mSurfacePlayerViewManager.prepare(it.id, mediaItemContext)
                    }
                }
            }
            mMaxChangePosition = mCurrentPostion
        }
    }

    override fun fetchSurfacePlayerView(id: Int): QSurfacePlayerView? {
        return mPlayItemManager.getOrNullById(id)?.let {
            val mediaItemContext = mMediaItemContextManager.fetchMediaItemContextById(id)
            if (mediaItemContext == null) {
                Log.d(TAG, "fetchSurfacePlayerView context is null id=$id")
            }
            mSurfacePlayerViewManager.fetchSurfacePlayerView(it, mediaItemContext)
        }
    }

    public override fun recycleSurfacePlayerView(surfacePlayerView: QSurfacePlayerView) {
        mSurfacePlayerViewManager.recycleSurfacePlayerView(surfacePlayerView)
    }
}