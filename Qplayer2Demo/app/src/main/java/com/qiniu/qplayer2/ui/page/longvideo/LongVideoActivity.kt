package com.qiniu.qplayer2.ui.page.longvideo

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
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
import com.qiniu.qplayer2.ui.page.longvideo.service.buffering.PlayerBufferingServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.controlpanelcontainervisible.PlayerControlPanelContainerVisibleServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.network.PlayerNetworkServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.panorama.PlayerPanoramaTouchSeriviceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.shoot.PlayerShootVideoServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.subtitle.PlayerSubtitleServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.toast.PlayerToastServiceOwner
import com.qiniu.qplayer2.ui.page.longvideo.service.volume.PlayerVolumeServiceOwner

class LongVideoActivity : AppCompatActivity() {

    private lateinit var mCommonPlayer: CommonPlayer<Any,
            LongLogicProvider, LongPlayableParams, LongVideoParams>

    private lateinit var mWakeLock: PowerManager.WakeLock

    private lateinit var mPlayerDataSource: CommonPlayerDataSource<LongPlayableParams, LongVideoParams>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_long_video)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        acquireWakeLock();
        initCommonPlayer()
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onDestroy() {
        mCommonPlayer.release()
        releaseWakeLock()
        super.onDestroy()
    }
    @SuppressLint("InvalidWakeLockTag")
    private fun acquireWakeLock() {
        val powerManager =getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "keep_player_activate")
        mWakeLock.setReferenceCounted(false)
    }

    private fun releaseWakeLock() {
        mWakeLock.release()
    }


    private fun initCommonPlayer() {
        mPlayerDataSource = LongPlayerDataSourceFactory.create(this)
        val config = CommonPlayerConfig.Builder<Any,
                LongLogicProvider, LongPlayableParams, LongVideoParams>()
            .addControlPanel(
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
            )
            .addEnviroment(LongEnviromentType.LONG.type,
                LongPlayerEnviroment())
            .setCommonPlayerScreenChangedListener(LongCommonPlayerScreenChangedListener(this, findViewById(R.id.video_container_FL)))
            .setLogicProvider(LongLogicProvider(this))
            .setPlayerDataSource(mPlayerDataSource)
            .setContext(this)
            .addServiceOwner(PlayerControlPanelContainerVisibleServiceOwner())
            .addServiceOwner(PlayerToastServiceOwner())
            .addServiceOwner(PlayerBufferingServiceOwner())
            .addServiceOwner(PlayerNetworkServiceOwner())
            .addServiceOwner(PlayerPanoramaTouchSeriviceOwner())
            .addServiceOwner(PlayerShootVideoServiceOwner())
            .addServiceOwner(PlayerVolumeServiceOwner())
            .addServiceOwner(PlayerSubtitleServiceOwner())
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
            .setSubtitleEnable(PlayerSettingRespostory.subtitleEnable)
            .setSEIEnable(PlayerSettingRespostory.seiEnable)
//            .setSubtitleEnable(PlayerSettingRespostory.subtitleEnable)
            .build()

        mCommonPlayer = CommonPlayer(config)
        mPlayerDataSource.getVideoParamsList()[0]?.also {
            mCommonPlayer.playerVideoSwitcher.switchVideo(it.id)
        }
    }
}