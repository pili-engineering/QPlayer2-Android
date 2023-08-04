package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.QIPlayerRenderListener
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QLogLevel
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.shortvideo.MediaItemContextManager
import com.qiniu.qplayer2.ui.page.shortvideo.PlayItem
import com.qiniu.qplayer2.ui.page.shortvideo.ShortVideoHolder

class ShortVideoListAdapterV2(context: Context, private val mExternalFilesDir: String) :
    RecyclerView.Adapter<ShortVideoHolderV2>() {
    private val mItems: MutableList<PlayItem> = ArrayList<PlayItem>()
    private val mQSurfacePlayerViews = HashSet<QSurfacePlayerView>()
    private val mMediaItemContextManager = MediaItemContextManager()
    private val mSurfacePlayerViewManager = SurfacePlayerViewManager(context)
    private var mCurrentPostion: Int = 0
    private var mCurrentHolder: ShortVideoHolderV2? = null

    fun preappendItems(videoItems: List<PlayItem>) {
        if (videoItems.isNotEmpty()) {
            mItems.addAll(0, videoItems)
            mMediaItemContextManager.updateMediaItemContext(
                mCurrentPostion,
                mItems,
                mExternalFilesDir
            )
            notifyItemRangeInserted(0, videoItems.size)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appendItems(videoItems: List<PlayItem>) {
        if (videoItems.isNotEmpty()) {
            val count = mItems.size
            mItems.addAll(videoItems)
            mMediaItemContextManager.updateMediaItemContext(
                mCurrentPostion,
                mItems,
                mExternalFilesDir
            )
            if (count > 0) {
                notifyItemRangeInserted(count, mItems.size)
            } else {
                notifyDataSetChanged()
            }
        }
    }

    fun deleteItem(position: Int) {
        if (position >= 0 && position < mItems.size) {
            mMediaItemContextManager.discardMediaItemContext(mItems[position].id)
            mItems.removeAt(position)
            mMediaItemContextManager.updateMediaItemContext(
                mCurrentPostion,
                mItems,
                mExternalFilesDir
            )
            notifyItemRemoved(position)
        }
    }

    fun replaceItem(position: Int, videoItem: PlayItem) {
        if (0 <= position && position < mItems.size) {
            mMediaItemContextManager.discardMediaItemContext(mItems[position].id)
            mItems[position] = videoItem
            mMediaItemContextManager.updateMediaItemContext(
                mCurrentPostion,
                mItems,
                mExternalFilesDir
            )
            notifyItemChanged(position)
        }
    }

    fun getItem(position: Int): PlayItem {
        return mItems[position]
    }

    @set:SuppressLint("NotifyDataSetChanged")
    var items: List<PlayItem>?
        get() = mItems
        set(videoItems) {
            MikuClientManager.getInstance().clearCache()
            mItems.clear()
            mItems.addAll(videoItems!!)
            mMediaItemContextManager.discardAllMediaItemContexts()
            mMediaItemContextManager.updateMediaItemContext(
                mCurrentPostion,
                mItems,
                mExternalFilesDir
            )
            notifyDataSetChanged()
        }

    fun init() {
        mSurfacePlayerViewManager.start()
        mMediaItemContextManager.start()
    }

    fun clear() {
        mSurfacePlayerViewManager.stop()
        mMediaItemContextManager.stop()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortVideoHolderV2 {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView: View = inflater.inflate(R.layout.holder_short_video, parent, false)
        return ShortVideoHolderV2(contactView, mSurfacePlayerViewManager)
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
        Log.d("INIT-STEP", "QSurfacePlayerView SIZE=${mQSurfacePlayerViews.size}")
    }


    override fun onBindViewHolder(holder: ShortVideoHolderV2, position: Int) {
        val videoItem: PlayItem = mItems[position]
        holder.bind(videoItem, position)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun onPageSelected(position: Int, holder: ShortVideoHolderV2) {
        mCurrentPostion = position
//        mMediaItemContextManager.updateMediaItemContext(mCurrentPostion, mItems, mExternalFilesDir)

        mCurrentHolder?.stopVideo()
        mCurrentHolder = holder
        val mediaItem = mMediaItemContextManager.fetchMediaItemContext(holder.playItemId)
        holder.startVideo(mediaItem)
    }
//
//    class ViewHolder(private val mPlayerView: QSurfacePlayerView) :
//        RecyclerView.ViewHolder(mPlayerView) {
//
//        val playerView: QSurfacePlayerView
//            get() = mPlayerView
//
//
//        private var mCurrentPlayItem: PlayItem? = null
//
//        private val mPlayerStateChangeListener: QIPlayerStateChangeListener = object :
//            QIPlayerStateChangeListener {
//            override fun onStateChanged(state: QPlayerState) {
//                if (state == QPlayerState.COMPLETED) {
//                    //循环播放
//                    mPlayerView.playerControlHandler.seek(0)
//                }
//            }
//        }
//        private val mPlayerRenderListener: QIPlayerRenderListener =
//            object : QIPlayerRenderListener {
//                override fun onFirstFrameRendered(elapsedTime: Long) {
////                updateMediaItemContext()
//                }
//            }
//
//        init {
//            mPlayerView.playerControlHandler.addPlayerStateChangeListener(mPlayerStateChangeListener)
//            mPlayerView.playerRenderHandler.addPlayerRenderListener(mPlayerRenderListener)
//            mPlayerView.playerControlHandler.init(itemView.context)
//            mPlayerView.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO)
//            mPlayerView.playerControlHandler.setLogLevel(QLogLevel.LOG_INFO)
//            mPlayerView.playerControlHandler.setStartAction(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE)
//            mPlayerView.layoutParams = RecyclerView.LayoutParams(
//                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        fun bind(position: Int, videoItem: PlayItem) {
//            Log.d("INIT-STEP", "ViewHolder::bind position=${position}")
//            if (mPlayerView.playerControlHandler.currentPlayerState == QPlayerState.NONE) {
//                mPlayerView.playerControlHandler.playMediaModel(videoItem.mediaModel, 0)
//            } else {
//
//                if ((mCurrentPlayItem?.id == videoItem.id) && (mPlayerView.playerControlHandler.currentPlayerState == QPlayerState.PAUSED_RENDER)) {
//                    mPlayerView.playerControlHandler.resumeRender()
//                } else {
//                    mPlayerView.playerControlHandler.playMediaModel(videoItem.mediaModel, 0)
//                }
//            }
//
//            mCurrentPlayItem = videoItem
//        }
//    }

}

