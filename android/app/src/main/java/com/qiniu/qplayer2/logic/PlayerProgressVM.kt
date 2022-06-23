package com.qiniu.qplayer2.logic

import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QIPlayerDownloadListener
import com.qiniu.qmedia.component.player.QIPlayerProgressListener
import com.qiniu.qmedia.component.player.QPlayerControlHandler

class PlayerProgressVM : QIPlayerProgressListener, QIPlayerDownloadListener, BasePlayerControlVM() {
    val playerProgressLiveData = MutableLiveData<Long>()
    val playBufferProgressLiveData = MutableLiveData<Long>()


    public fun getDuration(): Long {
        return playerControlHandler?.duration ?: 0L
    }

    override fun onProgressChanged(duration: Long, progress: Long) {
        playerProgressLiveData.value = progress
    }

    override fun onSetPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        playerControlHandler?.addPlayerProgressChangeListener(this)
        playerControlHandler?.addPlayerDownloadChangeListener(this)
    }

    public fun seek(position: Long) {
        playerControlHandler?.seek(position)
    }

    override fun onDownloadChanged(speed: Long, bufferProgress: Long) {
        playBufferProgressLiveData.value = bufferProgress;
    }
}