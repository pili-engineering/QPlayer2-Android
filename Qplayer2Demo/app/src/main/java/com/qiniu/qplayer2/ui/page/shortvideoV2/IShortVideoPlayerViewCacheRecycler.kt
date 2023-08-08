package com.qiniu.qplayer2.ui.page.shortvideoV2

import com.qiniu.qmedia.ui.QSurfacePlayerView

interface IShortVideoPlayerViewCacheRecycler {
    fun recycleSurfacePlayerView(surfacePlayerView: QSurfacePlayerView)

    fun fetchSurfacePlayerView(id: Int): QSurfacePlayerView?
}