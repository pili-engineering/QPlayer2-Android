package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.content.Context
import android.util.Log
import com.qiniu.qmedia.ui.QSurfacePlayerView

class ShortVideoPlayerViewCache(context: Context, private val mPlayItemManager: PlayItemManager,
                                externalFilesDir: String, allPlayerStateEndListener: IAllPlayerStateEndListener):IShortVideoPlayerViewCacheRecycler {
    private val mMediaItemContextManager = MediaItemContextManager(mPlayItemManager, externalFilesDir)
    private val mSurfacePlayerViewManager = SurfacePlayerViewManager(context, allPlayerStateEndListener)
    private var mCurrentPostion = -1


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

        mMediaItemContextManager.updateMediaItemContext(position)
        Log.d(TAG, "change position pos=${position}")

        //如果命中预渲染，则预渲染已经使用，如果没有命中，则需要回收预渲染，重建一个新pos的预渲染
        mSurfacePlayerViewManager.recyclePreRenderPlayerView()


        mPlayItemManager.getOrNullByPosition(position + 1)?.also {
            if (!mSurfacePlayerViewManager.isPreRenderValaid()) {
                mMediaItemContextManager.fetchMediaItemContextById(it.id)?.also { mediaItemContext ->
                    mSurfacePlayerViewManager.prepare(it.id, mediaItemContext)
                }
            }
        }

        mCurrentPostion = position

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