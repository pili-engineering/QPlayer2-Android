package com.qiniu.qplayer2.ui.page.longvideo.service.shoot

import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.network.PlayerNetworkService
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager

class PlayerShootVideoServiceOwner
    : IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {


    override val name: String
        get() = ServiceOwnerType.PLAYER_SHOOT_VIDEO_SERVICE.type

    private val mPlayerShootVideoServiceClient: PlayerServiceManager.Client<PlayerShootVideoService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerShootVideoService::class.java
            ), mPlayerShootVideoServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerShootVideoService::class.java
            ), mPlayerShootVideoServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerShootVideoServiceClient.service as T
    }
}