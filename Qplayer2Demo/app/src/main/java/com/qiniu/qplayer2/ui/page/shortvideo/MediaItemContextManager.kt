package com.qiniu.qplayer2.ui.page.shortvideo

import android.util.Log
import com.qiniu.qmedia.component.player.QLogLevel
import com.qiniu.qmedia.component.player.QMediaItemContext
import com.qiniu.qmedia.component.player.QMediaItemState
import com.qiniu.qmedia.component.player.QMediaModel
import com.qiniu.qplayer2.ui.page.shortvideoV2.MikuClientManager

class MediaItemContextManager {

    private val mMediaItemContextHashMap = HashMap<Int, QMediaItemContext>()

    companion object {
        private const val LOAD_FORWARD_POS = 1
        private const val LOAD_BACKWARD_POS = 5

    }

    fun start() {
        MikuClientManager.init()
    }

    fun stop() {
        MikuClientManager.uninit()

    }

    fun load(id: Int, mediaModel: QMediaModel, startPos: Long, logLevel: QLogLevel, localStorageDir: String) {
        var mediaItem = mMediaItemContextHashMap[id]
        if (mediaItem != null) {
            if (mediaItem.playMediaControlHandler.currentState == QMediaItemState.STOPED ||
                mediaItem.playMediaControlHandler.currentState == QMediaItemState.ERROR)  {
                mediaItem.playMediaControlHandler.stop()
                mMediaItemContextHashMap.remove(id)
                mediaItem = null
            }
        }

        if (mediaItem == null) {
            mediaItem = QMediaItemContext(mediaModel, logLevel, localStorageDir, startPos)
            mediaItem.playMediaControlHandler.start()
            mMediaItemContextHashMap[id] = mediaItem
        }
    }

    fun fetchMediaItemContext(id: Int): QMediaItemContext? {
        val mediaItem  = mMediaItemContextHashMap[id]
        mMediaItemContextHashMap.remove(id)

        Log.d("MediaItemContextManager", mediaItem?.playMediaControlHandler?.currentState?.value?.toString() ?: "")
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
    }

    public fun updateMediaItemContext(currentPosition: Int, videoItems: MutableList<PlayItem>, externalFilesDir: String) {

        return
        var start = currentPosition - LOAD_FORWARD_POS
        var end = currentPosition - 1

        //当前pos的视频 不加载
        for (i: Int in start .. end) {
            videoItems.getOrNull(i)?.let {
                load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, externalFilesDir)
            }
        }

        start = currentPosition + 1
        end = currentPosition + LOAD_BACKWARD_POS
        for (i: Int in start .. end) {
            videoItems.getOrNull(i)?.let {
                load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, externalFilesDir)
            }
        }
    }
}