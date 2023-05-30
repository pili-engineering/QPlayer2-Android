package com.qiniu.qplayer2.ui.page.longvideo.service.subtitle

import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager

class PlayerSubtitleServiceOwner :
    IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {


    override val name: String
        get() = ServiceOwnerType.PLAYER_SUBTITLE_SERVICE.type

    private val mPlayerSubtitleServiceClient: PlayerServiceManager.Client<PlayerSubtitleService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerSubtitleService::class.java
            ), mPlayerSubtitleServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerSubtitleService::class.java
            ), mPlayerSubtitleServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerSubtitleServiceClient.service as T
    }
}