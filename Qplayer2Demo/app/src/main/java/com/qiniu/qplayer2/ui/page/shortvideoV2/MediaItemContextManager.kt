package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.annotation.SuppressLint
import android.util.Log
import androidx.collection.ArraySet
import com.qiniu.qmedia.component.player.QLogLevel
import com.qiniu.qmedia.component.player.QMediaItemContext
import com.qiniu.qmedia.component.player.QMediaItemState
import com.qiniu.qmedia.component.player.QMediaModel

class MediaItemContextManager(private val mPlayItemManager: PlayItemManager,
    private val mExternalFilesDir: String) {

    private val mMediaItemContextHashMap = HashMap<Int, QMediaItemContext>()
    private var mCurrentPosition: Int = 0
    companion object {
        private const val LOAD_FORWARD_POS = 1
        private const val LOAD_BACKWARD_POS = 5
        private const val TAG = "MediaItemContextManager"
    }

    private val mPlayItemListRefreshListener = object : PlayItemManager.IPlayItemListRefreshListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onRefresh(itemList: List<PlayItem>) {
            mCurrentPosition = 0
            updateMediaItemContext(mCurrentPosition)
        }
    }

    private val mPlayItemAppendListener = object : PlayItemManager.IPlayItemAppendListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onAppend(appendItems: List<PlayItem>) {
            updateMediaItemContext(mCurrentPosition)

        }
    }

    private val mPlayItemDeleteListener = object : PlayItemManager.IPlayItemDeleteListener {
        override fun onDelete(position: Int, deletePlayItem: PlayItem) {
            discardMediaItemContext(deletePlayItem.id)
            updateMediaItemContext(mCurrentPosition)
        }
    }

    private val mPlayItemReplaceListener = object : PlayItemManager.IPlayItemReplaceListener {
        override fun onReplace(position: Int, oldPlayItem: PlayItem, newPlayItem: PlayItem) {
            updateMediaItemContext(mCurrentPosition)
        }
    }

    fun start() {
        MikuClientManager.init()
        mPlayItemManager.addPlayItemDeleteListener(mPlayItemDeleteListener)
        mPlayItemManager.addPlayItemReplaceListener(mPlayItemReplaceListener)
        mPlayItemManager.addPlayItemAppendListener(mPlayItemAppendListener)
        mPlayItemManager.addPlayItemListRefreshListener(mPlayItemListRefreshListener)

    }

    fun stop() {
        MikuClientManager.uninit()
        mPlayItemManager.removePlayItemDeleteListener(mPlayItemDeleteListener)
        mPlayItemManager.removePlayItemReplaceListener(mPlayItemReplaceListener)
        mPlayItemManager.removePlayItemAppendListener(mPlayItemAppendListener)
        mPlayItemManager.removePlayItemRefreshListener(mPlayItemListRefreshListener)
        discardAllMediaItemContexts()
    }

    private fun load(id: Int, mediaModel: QMediaModel, startPos: Long, logLevel: QLogLevel, localStorageDir: String) {
        var mediaItem = mMediaItemContextHashMap[id]
        if (mediaItem != null) {
            if (mediaItem.playMediaControlHandler.currentState == QMediaItemState.STOPED ||
                mediaItem.playMediaControlHandler.currentState == QMediaItemState.ERROR)  {
                mediaItem.playMediaControlHandler.stop()
                mMediaItemContextHashMap.remove(id)
                Log.d(TAG, "load::remove error or stoped mediaitem  id=$id")

                mediaItem = null
            }
        }

        if (mediaItem == null) {
            mediaItem = QMediaItemContext(mediaModel, logLevel, localStorageDir, startPos)
            mediaItem.playMediaControlHandler.start()
            mMediaItemContextHashMap[id] = mediaItem
            Log.d(TAG, "load::mediaitem id=$id")

        }
    }

    fun fetchMediaItemContextById(id: Int): QMediaItemContext? {
        val mediaItem  = mMediaItemContextHashMap[id]
        mMediaItemContextHashMap.remove(id)

        Log.d(TAG, "fetchMediaItemContextById id=$id")
        return mediaItem
    }

    fun discardAllMediaItemContexts() {

        mMediaItemContextHashMap.forEach {
            it.value.playMediaControlHandler.stop()
        }

        mMediaItemContextHashMap.clear()
    }

    fun discardMediaItemContext(id: Int) {
        val mediaItem  = mMediaItemContextHashMap[id]
        mediaItem?.playMediaControlHandler?.stop()
        mMediaItemContextHashMap.remove(id)
        Log.d(TAG, "discardMediaItemContext id=$id")

    }

    public fun updateMediaItemContext(currentPosition: Int) {

        //TODO 移除范围外的mediaitem context
        mCurrentPosition = currentPosition

        val newContextIdsSet = ArraySet<Int>()

        var start = currentPosition - LOAD_FORWARD_POS
        var end = currentPosition - 1
        //当前pos的视频 不加载
        for (i: Int in start .. end) {
            mPlayItemManager.getOrNullByPosition(i)?.let {
                newContextIdsSet.add(it.id)
            }
        }

        start = currentPosition + 1
        end = currentPosition + LOAD_BACKWARD_POS
        for (i: Int in start .. end) {
            mPlayItemManager.getOrNullByPosition(i)?.let {
                newContextIdsSet.add(it.id)
            }
        }

        //加载新的
        val addContextIdsSet = newContextIdsSet - mMediaItemContextHashMap.keys
        for (id: Int in addContextIdsSet) {
            mPlayItemManager.getOrNullById(id)?.let {
                load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, mExternalFilesDir)
            }
        }
        Log.d(TAG, "add preload ids=${addContextIdsSet.toString()}")
        //废弃老的
        val discardContextIdsSet = mMediaItemContextHashMap.keys - newContextIdsSet
        for (id: Int in discardContextIdsSet) {
            discardMediaItemContext(id)
        }
        Log.d(TAG, "remove preload ids=${discardContextIdsSet.toString()}")

    }
}