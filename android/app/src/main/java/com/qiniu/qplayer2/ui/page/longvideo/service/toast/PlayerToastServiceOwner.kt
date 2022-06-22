package com.qiniu.qplayer2.ui.page.longvideo.service.toast

import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType

class PlayerToastServiceOwner :
    IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    override val name: String
        get() = ServiceOwnerType.PLAYER_TOAST_SERVICE.type

    private val mPlayerToastServiceClient: PlayerServiceManager.Client<PlayerToastService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerToastService::class.java
            ), mPlayerToastServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerToastService::class.java
            ), mPlayerToastServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerToastServiceClient.service as T
    }
}