package com.qiniu.qplayer2.repository.setting

import android.content.SharedPreferences
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qplayer2.application.QPlayerApplicationContext
import com.qiniu.qplayer2.repository.common.SharedPreferencesHelper
import io.reactivex.rxjava3.subjects.PublishSubject

object PlayerSettingRespostory : SharedPreferences.OnSharedPreferenceChangeListener {


    private const val INIT_SETTING_SP_NAME = "InitSettingSP"
    private const val DECODER_SETTING_KEY_NAME = "DecoderSetting"
    private const val SEEK_SETTING_KEY_NAME = "SeekSetting"
    private const val START_SETTING_KEY_NAME = "StartSetting"
    private const val RENDER_RATIO_SETTING_KEY_NAME = "RenderRatioSetting"
    private const val SPEED_SETTING_KEY_NAME = "SpeedSetting"
    private const val START_POSITION_SETTING_KEY_NAME = "StartPositionSetting"
    private const val BLIND_SETTING_KEY_NAME = "BlindSetting"
    private const val SEI_SETTING_KEY_NAME = "SEISetting"
    private const val SUBTITLE_ENABLE_SETTING_KEY_NAME = "SubtitleEnableSetting"
    private const val QUALITY_SWITCH_TYPE_SETTING_KEY_NAME = "QualitySwitchTypeSetting"
    private const val SHOOT_VIDEO_SOURCE_KEY_NAME = "ShootVideoSource"
    private const val MIRROR_SETTING_KEY_NAME = "MirrorSetting"
    private const val ROTATION_SETTING_KEY_NAME = "RotationSetting"
    private const val SCALE_SETTING_KEY_NAME = "ScaleSetting"


    public enum class QualitySwitchType(val value: Int) {
        //自动选择
        QPLAYER_QUALITY_SWITCH_TYPE_AUTO(0),
        //立即模式
        QPLAYER_QUALITY_SWITCH_IMMEDIATELY(1),
        //无缝模式
        QPLAYER_QUALITY_SWITCH_SEAMLESS(2)
    }

    var decoderTypeSubject = PublishSubject.create<QPlayerSetting.QPlayerDecoder>()
    var seekTypeSubject = PublishSubject.create<QPlayerSetting.QPlayerSeek>()
    var startTypeSubject = PublishSubject.create<QPlayerSetting.QPlayerStart>()
    var renderRatioSubject = PublishSubject.create<QPlayerSetting.QPlayerRenderRatio>()
    var blindTypeSubject = PublishSubject.create<QPlayerSetting.QPlayerBlind>()
    var speedSubject = PublishSubject.create<Float>()
    var startPositionSubject = PublishSubject.create<Long>()


    private val mSharedPreferencesHelper =
        SharedPreferencesHelper(QPlayerApplicationContext.applicationContext, INIT_SETTING_SP_NAME)

    init {
        mSharedPreferencesHelper.registerOnSharedPreferenceChangeListener(this)
    }

    var blindType: QPlayerSetting.QPlayerBlind
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                BLIND_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_NONE.value
            )
            return QPlayerSetting.QPlayerBlind.values().find { it.value == type }
                ?: QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_NONE

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(BLIND_SETTING_KEY_NAME, value.value)
        }

    var decoderType: QPlayerSetting.QPlayerDecoder
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                DECODER_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO.value
            )
            return QPlayerSetting.QPlayerDecoder.values().find { it.value == type }
                ?: QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(DECODER_SETTING_KEY_NAME, value.value)
        }

    var mirrorType: QPlayerSetting.QPlayerMirror
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                MIRROR_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_NONE.value
            )
            return QPlayerSetting.QPlayerMirror.values().find { it.value == type } ?: QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_NONE
        }

        set(value) {
            mSharedPreferencesHelper.setInteger(MIRROR_SETTING_KEY_NAME, value.value)
        }

    var scale: Float
        get() {
            return mSharedPreferencesHelper.optFloat(
                SCALE_SETTING_KEY_NAME,
                1.0f
            )
        }

        set(value) {
            mSharedPreferencesHelper.setFloat(SCALE_SETTING_KEY_NAME, value)
        }
    var rotation:Int
        get() {
            return mSharedPreferencesHelper.optInteger(
                ROTATION_SETTING_KEY_NAME,
                0
            )
        }
        set(value) {
            mSharedPreferencesHelper.setInteger(ROTATION_SETTING_KEY_NAME, value)
        }


    var seekMode: QPlayerSetting.QPlayerSeek
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                START_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL.value
            )
            return QPlayerSetting.QPlayerSeek.values().find { it.value == type }
                ?: QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(START_SETTING_KEY_NAME, value.value)
        }

    var startAction: QPlayerSetting.QPlayerStart
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                SEEK_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING.value
            )
            return QPlayerSetting.QPlayerStart.values().find { it.value == type }
                ?: QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(SEEK_SETTING_KEY_NAME, value.value)
        }

    var ratioType: QPlayerSetting.QPlayerRenderRatio
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                RENDER_RATIO_SETTING_KEY_NAME,
                QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO.value
            )
            return QPlayerSetting.QPlayerRenderRatio.values().find { it.value == type }
                ?: QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(RENDER_RATIO_SETTING_KEY_NAME, value.value)
        }

    var playSpeed: Float
        get() {
            return mSharedPreferencesHelper.optFloat(
                SPEED_SETTING_KEY_NAME,
                1.0f
            )
        }
        set(value) {
            mSharedPreferencesHelper.setFloat(SPEED_SETTING_KEY_NAME, value)
        }

    var startPosition: Long
        get() {
            return mSharedPreferencesHelper.optLong(
                START_POSITION_SETTING_KEY_NAME,
                0
            )
        }
        set(value) {
            mSharedPreferencesHelper.setLong(START_POSITION_SETTING_KEY_NAME, value)
        }

    var seiEnable: Boolean
        get() {
            return mSharedPreferencesHelper.optBoolean(
                SEI_SETTING_KEY_NAME,
                false
            )
        }
        set(value) {
            mSharedPreferencesHelper.setBoolean(SEI_SETTING_KEY_NAME, value)
        }

    var subtitleEnable: Boolean
        get() {
            return mSharedPreferencesHelper.optBoolean(
                SUBTITLE_ENABLE_SETTING_KEY_NAME,
                false
            )
        }
        set(value) {
            mSharedPreferencesHelper.setBoolean(SUBTITLE_ENABLE_SETTING_KEY_NAME, value)
        }

    var qualitySwitchType:QualitySwitchType
        get() {
            val type: Int = mSharedPreferencesHelper.optInteger(
                QUALITY_SWITCH_TYPE_SETTING_KEY_NAME,
                QualitySwitchType.QPLAYER_QUALITY_SWITCH_TYPE_AUTO.value
            )
            return QualitySwitchType.values().find { it.value == type }
                ?: QualitySwitchType.QPLAYER_QUALITY_SWITCH_TYPE_AUTO

        }
        set(value) {
            mSharedPreferencesHelper.setInteger(QUALITY_SWITCH_TYPE_SETTING_KEY_NAME, value.value)
        }

    var isShootVideoSource:Boolean
        get() {
            return mSharedPreferencesHelper.optBoolean(
                SHOOT_VIDEO_SOURCE_KEY_NAME,
                true
            )
        }
        set(value) {
            mSharedPreferencesHelper.setBoolean(SHOOT_VIDEO_SOURCE_KEY_NAME, value)
        }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(DECODER_SETTING_KEY_NAME)) {
            decoderTypeSubject.onNext(decoderType)
        } else if (key.equals(SEEK_SETTING_KEY_NAME)) {
            seekTypeSubject.onNext(seekMode)
        } else if (key.equals(START_SETTING_KEY_NAME)) {
            startTypeSubject.onNext(startAction)
        } else if (key.equals(RENDER_RATIO_SETTING_KEY_NAME)) {
            renderRatioSubject.onNext(ratioType)
        } else if (key.equals(SPEED_SETTING_KEY_NAME)) {
            speedSubject.onNext(playSpeed)
        } else if (key.equals(START_POSITION_SETTING_KEY_NAME)) {
            startPositionSubject.onNext(startPosition)
        }
    }
}