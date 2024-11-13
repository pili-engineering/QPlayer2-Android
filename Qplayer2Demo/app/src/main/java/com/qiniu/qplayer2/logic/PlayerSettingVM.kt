package com.qiniu.qplayer2.logic

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import com.qiniu.qmedia.component.player.QPlayerSetting

import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2ext.common.rxjava3.attachToLifecycle
import com.qiniu.qplayer2ext.common.rxjava3.subscribeBy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class PlayerSettingVM(lifeCycle: Lifecycle) {

    val decoderTypeLiveData =
        MutableLiveData<QPlayerSetting.QPlayerDecoder>(PlayerSettingRespostory.decoderType)
    val seekTypeLiveData =
        MutableLiveData<QPlayerSetting.QPlayerSeek>(PlayerSettingRespostory.seekMode)
    val startTypeLiveData =
        MutableLiveData<QPlayerSetting.QPlayerStart>(PlayerSettingRespostory.startAction)

    val renderRatioLiveData =
        MutableLiveData<QPlayerSetting.QPlayerRenderRatio>(PlayerSettingRespostory.ratioType)

    val speedLiveData =
        MutableLiveData<Float>(PlayerSettingRespostory.playSpeed)

    val startPositionLiveData =
        MutableLiveData<Long>(PlayerSettingRespostory.startPosition)

    val muteLiveData = MutableLiveData<Boolean>(PlayerSettingRespostory.isMute)

    init {

        PlayerSettingRespostory.decoderTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    decoderTypeLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)


        PlayerSettingRespostory.seekTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    seekTypeLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)

        PlayerSettingRespostory.startTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    startTypeLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)


        PlayerSettingRespostory.renderRatioSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    renderRatioLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)

        PlayerSettingRespostory.speedSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    speedLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)

        PlayerSettingRespostory.startPositionSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    startPositionLiveData.value = it
                }
                onError { }
            }.attachToLifecycle(lifeCycle)
    }

    fun setDecoderType(type: QPlayerSetting.QPlayerDecoder) {
        PlayerSettingRespostory.decoderType = type
    }

    fun setSeekType(type: QPlayerSetting.QPlayerSeek) {
        PlayerSettingRespostory.seekMode = type
    }

    fun setStartType(type: QPlayerSetting.QPlayerStart) {
        PlayerSettingRespostory.startAction = type
    }

    fun setRenderRatio(type: QPlayerSetting.QPlayerRenderRatio) {
        PlayerSettingRespostory.ratioType = type
    }

    fun setSpeed(speed: Float) {
        PlayerSettingRespostory.playSpeed = speed
    }

    fun setStartPostion(pos: Long) {
        PlayerSettingRespostory.startPosition = pos
    }

    fun setMute(mute: Boolean) {
        PlayerSettingRespostory.isMute = mute
    }
}
