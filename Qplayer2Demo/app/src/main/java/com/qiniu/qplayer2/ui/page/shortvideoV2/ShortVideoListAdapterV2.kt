package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R

class ShortVideoListAdapterV2(context: Context,
                              private val mPlayItemManager: PlayItemManager,
                              private val mExternalFilesDir: String) :
    RecyclerView.Adapter<ShortVideoHolderV2>() {

    companion object {
        private const val TAG = "ShortVideoListAdapterV2"
    }

    private val mShortVideoPlayerViewCache = ShortVideoPlayerViewCache(context, mPlayItemManager, mExternalFilesDir)
    private var mCurrentPostion: Int = 0
    private var mCurrentHolder: ShortVideoHolderV2? = null

    private val mPlayItemListRefreshListener = object : PlayItemManager.IPlayItemListRefreshListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onRefresh(itemList: List<PlayItem>) {
            notifyDataSetChanged()
        }


    }

    private val mPlayItemAppendListener = object : PlayItemManager.IPlayItemAppendListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onAppend(appendItems: List<PlayItem>) {
            if (appendItems.count() == mPlayItemManager.count()) {
                notifyDataSetChanged()
            } else {
                notifyItemRangeInserted(0, appendItems.size)
            }
        }
    }

    private val mPlayItemDeleteListener = object : PlayItemManager.IPlayItemDeleteListener {
        override fun onDelete(position: Int, deletePlayItem: PlayItem) {
            notifyItemRemoved(position)
        }
    }

    private val mPlayItemReplaceListener = object : PlayItemManager.IPlayItemReplaceListener {
        override fun onReplace(position: Int, oldPlayItem: PlayItem, newPlayItem: PlayItem) {
            notifyItemChanged(position)
        }
    }

    init {

        //先添加 ShortVideoPlayerViewCache 中的 PlayItemManager监听 后添加@this Adapter 监听
        mShortVideoPlayerViewCache.start()

        mPlayItemManager.addPlayItemListRefreshListener(mPlayItemListRefreshListener)
        mPlayItemManager.addPlayItemAppendListener(mPlayItemAppendListener)
        mPlayItemManager.addPlayItemReplaceListener(mPlayItemReplaceListener)
        mPlayItemManager.addPlayItemDeleteListener(mPlayItemDeleteListener)
    }

    fun makeProxyURL(url: String): String {
        return MikuClientManager.getInstance().makeProxyURL(url)
    }

    fun clear() {
        mCurrentHolder?.stopVideo()
        mShortVideoPlayerViewCache.stop()
    }


    fun getItem(position: Int): PlayItem? {
        return mPlayItemManager.getOrNullByPosition(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortVideoHolderV2 {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView: View = inflater.inflate(R.layout.holder_short_video, parent, false)
        return ShortVideoHolderV2(mShortVideoPlayerViewCache, contactView)
    }

    override fun onViewDetachedFromWindow(holder: ShortVideoHolderV2) {
//        holder.playerView.playerControlHandler.stop()
        Log.d("INIT-STEP", "onViewDetachedFromWindow::position=${holder.position}")

        super.onViewDetachedFromWindow(holder)

    }

    override fun onViewAttachedToWindow(holder: ShortVideoHolderV2) {
        Log.d("INIT-STEP", "onViewAttachedToWindow::position=${holder.position}")
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewRecycled(holder: ShortVideoHolderV2) {
//        holder.playerView.playerControlHandler.release()
        Log.d("INIT-STEP", "onViewRecycled:position=${holder.position}")
    }


    override fun onBindViewHolder(holder: ShortVideoHolderV2, position: Int) {
        Log.d("INIT-STEP", "onBindViewHolder::position=${position}")
        holder.bind(mPlayItemManager.getOrNullByPosition(position), position)
    }

    override fun getItemCount(): Int {
        return mPlayItemManager.count()
    }

    fun onPageSelected(position: Int, holder: ShortVideoHolderV2) {
        mCurrentPostion = position
//        mMediaItemContextManager.updateMediaItemContext(mCurrentPostion, mItems, mExternalFilesDir)

        //!!!!此处执行顺序不能变
        mCurrentHolder?.stopVideo()
        mCurrentHolder = holder
        val surfaceVideoPlayerView = mShortVideoPlayerViewCache.fetchSurfacePlayerView(holder.playItemId)
        if (surfaceVideoPlayerView != null) {
            holder.startVideo(surfaceVideoPlayerView)
        } else {
            Log.e(TAG, "surface video player view is null!!!")
        }
        mShortVideoPlayerViewCache.changePosition(mCurrentPostion)

    }
}

