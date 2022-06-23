package com.qiniu.qplayer2.ui.page.longvideo.service.buffering

import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType

class PlayerBufferingServiceOwner :
    IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {


    override val name: String
        get() = ServiceOwnerType.PLAYER_BUFFERING_SERVICE.type

    private val mPlayerBufferingServiceClient: PlayerServiceManager.Client<PlayerBufferingService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerBufferingService::class.java
            ), mPlayerBufferingServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerBufferingService::class.java
            ), mPlayerBufferingServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerBufferingServiceClient.service as T
    }
}