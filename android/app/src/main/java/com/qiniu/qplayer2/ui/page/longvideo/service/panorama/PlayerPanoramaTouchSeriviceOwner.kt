package com.qiniu.qplayer2.ui.page.longvideo.service.panorama

import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager

class PlayerPanoramaTouchSeriviceOwner :
    IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {


    override val name: String
        get() = ServiceOwnerType.PLAYER_PANORAMA_TOUCH_SERVICE.type

    private val mPlayerPanoramaTouchServiceClient: PlayerServiceManager.Client<PlayerPanoramaTouchSerivice, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerPanoramaTouchSerivice::class.java
            ), mPlayerPanoramaTouchServiceClient
        )
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(
            PlayerServiceManager.ServiceDescriptor.obtain(
                PlayerPanoramaTouchSerivice::class.java
            ), mPlayerPanoramaTouchServiceClient
        )
    }

    override fun <T> service(): T {
        return mPlayerPanoramaTouchServiceClient.service as T
    }
}