package com.qiniu.qplayer2.tools

import com.qiniu.qmedia.component.player.QIPlayerVideoDataListener

class VideoFrame(val width: Int,
                 val height: Int,
                 val videoDataType: QIPlayerVideoDataListener.QVideoDataType,
                 data: ByteArray,
                 presentationTimeUs: Long): BaseFrame(data, presentationTimeUs)