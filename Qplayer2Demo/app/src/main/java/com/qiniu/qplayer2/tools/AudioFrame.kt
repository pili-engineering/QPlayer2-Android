package com.qiniu.qplayer2.tools

import com.qiniu.qmedia.component.player.QIPlayerAudioDataListener

class AudioFrame(
    val sampleRate: Int,
    val channelCount: Int,
    val audioFormat: QIPlayerAudioDataListener.QSampleFormat,
    val channelLayout: QIPlayerAudioDataListener.QChannelLayout,

    data: ByteArray,
    presentationTimeUs: Long
) : BaseFrame(data, presentationTimeUs)