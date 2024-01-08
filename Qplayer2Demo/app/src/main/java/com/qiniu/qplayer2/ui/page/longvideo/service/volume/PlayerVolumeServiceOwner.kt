package com.qiniu.qplayer2.ui.page.longvideo.service.volume

import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.shoot.PlayerCaptureService
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager

class PlayerVolumeServiceOwner
    : IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {


    override val name: String
        get() = ServiceOwnerType.PLAYER_VOLUME_SERVICE.type

    private val mPlayerVolumeServiceClient: PlayerServiceManager.Client<PlayerVolumeService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerVolumeService::class.java
            ), mPlayerVolumeServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerCaptureService::class.java
            ), mPlayerVolumeServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerVolumeServiceClient.service as T
    }
}