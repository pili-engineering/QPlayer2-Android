package com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible

import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceOwner
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerServiceManager
import com.qiniu.qplayer2ext.commonplayer.service.PlayerServiceManager
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType

class PlayerControlPanelContainerVisibleServiceOwner
    : IPlayerServiceOwner<LongLogicProvider, LongPlayableParams, LongVideoParams> {
    override val name: String
        get() = ServiceOwnerType.PLAYER_CONTROL_PANEL_CONTATINER_VISIBLE_SERVICE.type

    private val mPlayerControlPanelContainerVisibleServiceClient: PlayerServiceManager.Client<PlayerControlPanelContainerVisibleService, LongLogicProvider, LongPlayableParams, LongVideoParams> =
        PlayerServiceManager.Client()

    override fun start(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.bindService(PlayerServiceManager.ServiceDescriptor.obtain(
            PlayerControlPanelContainerVisibleService::class.java), mPlayerControlPanelContainerVisibleServiceClient)
    }

    override fun stop(serviceManager: IPlayerServiceManager<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        serviceManager.unbindService(PlayerServiceManager.ServiceDescriptor.obtain(
            PlayerControlPanelContainerVisibleService::class.java), mPlayerControlPanelContainerVisibleServiceClient)
    }

    override  fun <T>service(): T {
        return mPlayerControlPanelContainerVisibleServiceClient.service as T
    }
}