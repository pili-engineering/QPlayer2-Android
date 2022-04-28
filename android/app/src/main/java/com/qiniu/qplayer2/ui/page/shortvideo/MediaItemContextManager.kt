package com.qiniu.qplayer2.ui.page.shortvideo

import com.qiniu.qmedia.component.player.QLogLevel
import com.qiniu.qmedia.component.player.QMediaItemContext
import com.qiniu.qmedia.component.player.QMediaItemState
import com.qiniu.qmedia.component.player.QMediaModel

class MediaItemContextManager {

    private val mMediaItemContextHashMap = HashMap<Int, QMediaItemContext>()

    fun load(id: Int, mediaModel: QMediaModel, startPos: Long, logLevel: QLogLevel, localStorageDir: String) {
        var mediaItem = mMediaItemContextHashMap[id]
        if (mediaItem != null) {
            if (mediaItem.playMediaControlHandler.currentState == QMediaItemState.STOPED ||
                mediaItem.playMediaControlHandler.currentState == QMediaItemState.USED ||
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
        return mediaItem
    }

    fun discardAllMediaItemContexts() {

        mMediaItemContextHashMap.forEach {
            it.value.playMediaControlHandler.stop()
        }

        mMediaItemContextHashMap.clear()
    }
}