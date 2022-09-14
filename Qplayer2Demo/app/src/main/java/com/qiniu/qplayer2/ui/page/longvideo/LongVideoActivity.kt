package com.qiniu.qplayer2.ui.page.longvideo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayer
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerConfig
import com.qiniu.qplayer2ext.commonplayer.data.DisplayOrientation
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayerDataSource
import com.qiniu.qplayer2ext.commonplayer.layer.control.ControlPanelConfig
import com.qiniu.qplayer2ext.commonplayer.layer.control.ControlPanelConfigElement
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.buffering.PlayerBufferingService
import com.qiniu.qplayer2.ui.page.longvideo.service.buffering.PlayerBufferingServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible.PlayerControlPanelContainerVisibleServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.network.PlayerNetworkServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.panorama.PlayerPanoramaTouchSeriviceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.toast.PlayerToastServiceOwner

class LongVideoActivity : AppCompatActivity() {

    private lateinit var mCommonPlayer: CommonPlayer<Any,
            LongLogicProvider, LongPlayableParams, LongVideoParams>

    private lateinit var mPlayerDataSource: CommonPlayerDataSource<LongPlayableParams, LongVideoParams>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_long_video)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initCommonPlayer()
    }

    override fun onDestroy() {
        mCommonPlayer.release()
        this is Context
        super.onDestroy()

    }

    private fun initCommonPlayer() {
        mPlayerDataSource = LongPlayerDataSourceFactory.create()
        val config = CommonPlayerConfig.Builder<Any,
                LongLogicProvider, LongPlayableParams, LongVideoParams>()
            .addControlPanel(
                LongControlPanelType.Normal.type,
                ControlPanelConfig(
                    LongControlPanelType.Normal.type,
                    arrayListOf(
                        ControlPanelConfigElement(
                            R.layout.control_panel_halfscreen_landscape_normal,
                            arrayListOf(ScreenType.HALF_SCREEN),
                            DisplayOrientation.LANDSCAPE),
                        ControlPanelConfigElement(
                            R.layout.control_panel_fullscreen_landscape_normal,
                            arrayListOf(ScreenType.FULL_SCREEN, ScreenType.REVERSE_FULL_SCREEN),
                            DisplayOrientation.LANDSCAPE)
                    )
                )
            ).addEnviroment(LongEnviromentType.LONG.type,
                LongPlayerEnviroment())
            .setCommonPlayerScreenChangedListener(LongCommonPlayerScreenChangedListener(this, findViewById(R.id.video_container_FL)))
            .setLogicProvider(LongLogicProvider())
            .setPlayerDataSource(mPlayerDataSource)
            .setContext(this)
            .addServiceOwner(PlayerControlPanelContainerVisibleServiceOwner())
            .addServiceOwner(PlayerToastServiceOwner())
            .addServiceOwner(PlayerBufferingServiceOwner())
            .addServiceOwner(PlayerNetworkServiceOwner())
            .addServiceOwner(PlayerPanoramaTouchSeriviceOwner())
            .setRootUIContanier(this, findViewById(R.id.video_container_FL))
            .enableControlPanel()
            .enableFunctionWidget()
            .enableGesture()
            .enableToast()
            .enableScreenRender(CommonPlayerConfig.ScreenRenderType.SURFACE_VIEW)
            .setDecodeType(PlayerSettingRespostory.decoderType)
            .setSeekMode(PlayerSettingRespostory.seekMode)
            .setBlindType(PlayerSettingRespostory.blindType)
            .setStartAction(PlayerSettingRespostory.startAction)
            .setSpeed(PlayerSettingRespostory.playSpeed)
            .setRenderRatio(PlayerSettingRespostory.ratioType)
            .setSEIEnable(PlayerSettingRespostory.seiEnable)
            .build()

        mCommonPlayer = CommonPlayer(config)
        mPlayerDataSource.getVideoParamsList()[0]?.also {
            mCommonPlayer.playerVideoSwitcher.switchVideo(it.id)
        }
    }
}