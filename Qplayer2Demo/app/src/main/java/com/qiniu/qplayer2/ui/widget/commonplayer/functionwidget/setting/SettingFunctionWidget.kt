package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.setting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qplayer2.R

import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayerDataSource
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SettingFunctionWidget(context: Context) :
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context) {

    private lateinit var mDecodrRG: RadioGroup
    private lateinit var mAutoDecodrRB: RadioButton
    private lateinit var mSoftDecodrRB: RadioButton
    private lateinit var mHardDecodrRB: RadioButton
    private lateinit var mMixDecodrRB: RadioButton

    private lateinit var mSeekRG: RadioGroup
    private lateinit var mNormalSeekRB: RadioButton
    private lateinit var mAccurateSeekRB: RadioButton

    private lateinit var mStartRG: RadioGroup
    private lateinit var mPlayStartRB: RadioButton
    private lateinit var mPauseStartRB: RadioButton

    private lateinit var mRenderRatioRG: RadioGroup
    private lateinit var mAutoRatioRB: RadioButton
    private lateinit var mStretchRatioRB: RadioButton
    private lateinit var mFullScreenRatioRB: RadioButton
    private lateinit var m16_9RatioRB: RadioButton
    private lateinit var m4_3RatioRB: RadioButton

    private lateinit var mBlindRG: RadioGroup
    private lateinit var mNoneBlindRB: RadioButton
    private lateinit var mRedBlindRB: RadioButton
    private lateinit var mGreenBlindRB: RadioButton
    private lateinit var mBlueBlindRB: RadioButton

    private lateinit var mMirrorRG: RadioGroup
    private lateinit var mNoneMirrorRB: RadioButton
    private lateinit var mXMirrorRB: RadioButton
    private lateinit var mYMirrorRB: RadioButton
    private lateinit var mXYMirrorRB: RadioButton


    private lateinit var mQualitySwitchTypeRG: RadioGroup
    private lateinit var mQualitySwitchImmediatelyRB: RadioButton
    private lateinit var mQualitySwitchAutoRB: RadioButton
    private lateinit var mQualitySwitchSeamlessRB: RadioButton

    private lateinit var mStartPositionEdit: EditText

    private lateinit var mRotationSeekBar: AppCompatSeekBar

    private lateinit var mScaleSeekBar: AppCompatSeekBar

    private lateinit var mForceFlushAuthenticationCB: CheckBox

    private lateinit var mSEIEnableCB: CheckBox

    private lateinit var mShootVideoSourceCB: CheckBox

    private val mCompositeDisposable = CompositeDisposable();

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override val tag: String
        get() = "SettingFunctionWidget"


    override val functionWidgetConfig: FunctionWidgetConfig
        get() {
            val builder = FunctionWidgetConfig.Builder()
            builder.dismissWhenActivityStop(true)
            builder.dismissWhenScreenTypeChange(true)
            builder.dismissWhenVideoChange(true)
            builder.dismissWhenVideoCompleted(true)
            builder.persistent(true)
            builder.changeOrientationDisableWhenShow(true)
            return builder.build()
        }

    override fun onWidgetShow() {

        updateDecoder(PlayerSettingRespostory.decoderType)
        updateStartType(PlayerSettingRespostory.startAction)
        updateSeekType(PlayerSettingRespostory.seekMode)
        updateRenderRatio(PlayerSettingRespostory.ratioType)
        updateStartPosition(PlayerSettingRespostory.startPosition)
        updateBlindType(PlayerSettingRespostory.blindType)
        updateSEIEnable(PlayerSettingRespostory.seiEnable)
        updateQualitySwitchType(PlayerSettingRespostory.qualitySwitchType)
        updateForceFlushAuthentication()
        updateIsShootVideoSource(PlayerSettingRespostory.isShootVideoSource)
        updateRotation(0)
        updateMirror(PlayerSettingRespostory.mirrorType)
        updateScale(1.0f)
//        registerSubjects()
        registerClickListeners()
    }

    override fun onWidgetDismiss() {
//        unregisterSubjects()

        if(mStartPositionEdit.editableText.toString().isEmpty()) {
            updateDataSourceStartPos(0)
        } else {
            updateDataSourceStartPos(mStartPositionEdit.editableText.toString().toLong())
        }

        unRegisterClickListeners()
    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_setting, null)
        mDecodrRG = view.findViewById(R.id.decoder_RG)
        mSeekRG = view.findViewById(R.id.seek_RG)
        mStartRG = view.findViewById(R.id.start_RG)
        mRenderRatioRG = view.findViewById(R.id.ratio_RG)

        mAutoDecodrRB = view.findViewById(R.id.auto_decoder_RB)
        mSoftDecodrRB = view.findViewById(R.id.software_decoder_RB)
        mHardDecodrRB = view.findViewById(R.id.hardware_decoder_RB)
        mMixDecodrRB = view.findViewById(R.id.mix_decoder_RB)

        mNormalSeekRB = view.findViewById(R.id.normal_seek_RB)
        mAccurateSeekRB = view.findViewById(R.id.accurate_seek_RB)

        mPlayStartRB = view.findViewById(R.id.start_play_RB)
        mPauseStartRB = view.findViewById(R.id.start_pause_RB)

        mAutoRatioRB = view.findViewById(R.id.ratio_auto_RB)
        mStretchRatioRB = view.findViewById(R.id.ratio_stretch_RB)
        mFullScreenRatioRB = view.findViewById(R.id.ratio_full_screen_RB)
        m16_9RatioRB = view.findViewById(R.id.ratio_16_9_RB)
        m4_3RatioRB = view.findViewById(R.id.ratio_4_3_RB)

        mBlindRG = view.findViewById(R.id.blind_RG)
        mNoneBlindRB = view.findViewById(R.id.blind_none_RB)
        mRedBlindRB = view.findViewById(R.id.blind_red_RB)
        mGreenBlindRB = view.findViewById(R.id.blind_green_RB)
        mBlueBlindRB = view.findViewById(R.id.blind_blue_RB)

        mMirrorRG = view.findViewById(R.id.mirror_RG)
        mNoneMirrorRB = view.findViewById(R.id.mirror_none_RB)
        mXMirrorRB = view.findViewById(R.id.mirror_x_RB)
        mYMirrorRB = view.findViewById(R.id.mirror_y_RB)
        mXYMirrorRB = view.findViewById(R.id.mirror_xy_RB)

        mRotationSeekBar = view.findViewById(R.id.rotation_SB)
        mScaleSeekBar = view.findViewById(R.id.scale_SB)
        mStartPositionEdit = view.findViewById(R.id.start_pos_ET)


        mForceFlushAuthenticationCB = view.findViewById(R.id.flush_authentication_CB)

        mSEIEnableCB = view.findViewById(R.id.sei_CB)

        mQualitySwitchTypeRG = view.findViewById(R.id.quality_switch_type_RG)
        mQualitySwitchImmediatelyRB = view.findViewById(R.id.quality_switch_immediately_RB)
        mQualitySwitchAutoRB = view.findViewById(R.id.quality_switch_type_by_is_living_RB)
        mQualitySwitchSeamlessRB = view.findViewById(R.id.quality_switch_seamless_RB)

        mShootVideoSourceCB = view.findViewById(R.id.shoot_video_source_CB)
        return view
    }

    private fun updateMirror(mirrorType: QPlayerSetting.QPlayerMirror) {
        when(mirrorType) {
            QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_NONE ->
                mNoneMirrorRB.isChecked = true
            QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_X ->
                mXMirrorRB.isChecked = true
            QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_Y ->
                mYMirrorRB.isChecked = true
            QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_XY ->
                mXYMirrorRB.isChecked = true
        }
    }

    private fun updateDecoder(decoderType: QPlayerSetting.QPlayerDecoder) {
        when (decoderType) {
            QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO ->
                mAutoDecodrRB.isChecked = true
            QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_SOFT_PRIORITY ->
                mSoftDecodrRB.isChecked = true
            QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY ->
                mHardDecodrRB.isChecked = true
            QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_FIRST_FRAME_ACCEL_PRIORITY ->
                mMixDecodrRB.isChecked = true
        }
    }


    private fun updateSEIEnable(enable: Boolean) {
        mSEIEnableCB.isChecked = enable
    }

    private fun updateForceFlushAuthentication() {
        mForceFlushAuthenticationCB.isChecked = false
    }
    private fun updateStartType(startType: QPlayerSetting.QPlayerStart) {
        when (startType) {
            QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING ->
                mPlayStartRB.isChecked = true
            QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE ->
                mPauseStartRB.isChecked = true
        }
    }

    private fun updateSeekType(seekType: QPlayerSetting.QPlayerSeek) {
        when (seekType) {
            QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL ->
                mNormalSeekRB.isChecked = true
            QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_ACCURATE ->
                mAccurateSeekRB.isChecked = true
        }
    }

    private fun updateRenderRatio(renderRatio: QPlayerSetting.QPlayerRenderRatio) {
        when (renderRatio) {
            QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO ->
                mAutoRatioRB.isChecked = true
            QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_STRETCH ->
                mStretchRatioRB.isChecked = true
            QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_FULL_SCREEN ->
                mFullScreenRatioRB.isChecked = true
            QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_16_9 ->
                m16_9RatioRB.isChecked = true
            QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_4_3 ->
                m4_3RatioRB.isChecked = true
        }
    }



    private fun updateBlindType(blindType: QPlayerSetting.QPlayerBlind) {
        when (blindType) {
            QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_NONE ->
                mNoneBlindRB.isChecked = true
            QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_RED ->
                mRedBlindRB.isChecked = true
            QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_GREEN ->
                mGreenBlindRB.isChecked = true
            QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_BLUE ->
                mBlueBlindRB.isChecked = true
        }
    }

    private fun updateRotation(angle: Int) {
        mRotationSeekBar.progress = angle * 1000 / 360
    }

    private fun updateScale(scale: Float) {
        mScaleSeekBar.progress = (1000 * (scale - 0.5f) / 1.5f).toInt()
    }

    private fun updateStartPosition(startPos: Long) {
        mStartPositionEdit.setText(startPos.toString())
    }

    private fun updateQualitySwitchType(type: PlayerSettingRespostory.QualitySwitchType) {
        when (type) {
            PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_TYPE_AUTO ->
                mQualitySwitchAutoRB.isChecked = true
            PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_IMMEDIATELY ->
                mQualitySwitchImmediatelyRB.isChecked = true
            PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_SEAMLESS ->
                mQualitySwitchSeamlessRB.isChecked = true
        }
    }

    private fun updateIsShootVideoSource(isShootVideoSource: Boolean) {
        mShootVideoSourceCB.isChecked = isShootVideoSource
    }
//
//    private fun registerSubjects() {
//        PlayerSettingRespostory.decoderTypeSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateDecoder(it)
//                }
//                onError { }
//            } into mCompositeDisposable
//
//
//        PlayerSettingRespostory.seekTypeSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateSeekType(it)
//
//                }
//                onError { }
//            } into mCompositeDisposable
//
//        PlayerSettingRespostory.startTypeSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateStartType(it)
//                }
//                onError { }
//            } into mCompositeDisposable
//
//
//        PlayerSettingRespostory.renderRatioSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateRenderRatio(it)
//                }
//                onError { }
//            } into mCompositeDisposable
//
//
//
//        PlayerSettingRespostory.startPositionSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateStartPosition(it)
//                }
//                onError { }
//            } into mCompositeDisposable
//
//
//        PlayerSettingRespostory.blindTypeSubject
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy {
//                onNext {
//                    updateBlindType(it)
//                }
//                onError { }
//            } into mCompositeDisposable
//
//    }
//
//    private fun unregisterSubjects() {
//        mCompositeDisposable.clear()
//    }

    private fun unRegisterClickListeners() {
        mDecodrRG.setOnCheckedChangeListener(null)

        mSeekRG.setOnCheckedChangeListener(null)

        mStartRG.setOnCheckedChangeListener(null)

        mRenderRatioRG.setOnCheckedChangeListener(null)

        mBlindRG.setOnCheckedChangeListener(null)

        mStartPositionEdit.setOnFocusChangeListener(null)

        mForceFlushAuthenticationCB.setOnCheckedChangeListener(null)

        mSEIEnableCB.setOnCheckedChangeListener(null)

        mQualitySwitchTypeRG.setOnCheckedChangeListener(null)

        mMirrorRG.setOnCheckedChangeListener(null)
    }
    private fun registerClickListeners() {
        mDecodrRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mAutoDecodrRB.id ->
                    PlayerSettingRespostory.decoderType =
                        QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO
                mSoftDecodrRB.id ->
                    PlayerSettingRespostory.decoderType =
                        QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_SOFT_PRIORITY
                mHardDecodrRB.id ->
                    PlayerSettingRespostory.decoderType =
                        QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY

                mMixDecodrRB.id ->
                    PlayerSettingRespostory.decoderType =
                        QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_FIRST_FRAME_ACCEL_PRIORITY
            }

            mPlayerCore.mPlayerContext.getPlayerControlHandler().setDecodeType(PlayerSettingRespostory.decoderType)

        }

        mMirrorRG.setOnCheckedChangeListener { group, checkId ->
            when (checkId) {
                mNoneMirrorRB.id ->
                    PlayerSettingRespostory.mirrorType =
                        QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_NONE
                mXMirrorRB.id ->
                    PlayerSettingRespostory.mirrorType =
                        QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_X
                mYMirrorRB.id ->
                    PlayerSettingRespostory.mirrorType =
                        QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_Y
                mXYMirrorRB.id ->
                    PlayerSettingRespostory.mirrorType =
                        QPlayerSetting.QPlayerMirror.QPLAYER_MIRROR_XY
            }
            mPlayerCore.mPlayerContext.getPlayerRenderHandler().setMirrorType(PlayerSettingRespostory.mirrorType)

        }

        mSeekRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mNormalSeekRB.id ->
                    PlayerSettingRespostory.seekMode =
                        QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL
                mAccurateSeekRB.id ->
                    PlayerSettingRespostory.seekMode =
                        QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_ACCURATE
            }

            mPlayerCore.mPlayerContext.getPlayerControlHandler().setSeekMode(PlayerSettingRespostory.seekMode)

        }

        mStartRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mPlayStartRB.id ->
                    PlayerSettingRespostory.startAction =
                        QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING
                mPauseStartRB.id ->
                    PlayerSettingRespostory.startAction =
                        QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE
            }
            mPlayerCore.mPlayerContext.getPlayerControlHandler().setStartAction(PlayerSettingRespostory.startAction)

        }

        mRenderRatioRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mAutoRatioRB.id ->
                    PlayerSettingRespostory.ratioType =
                        QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO

                mStretchRatioRB.id ->
                    PlayerSettingRespostory.ratioType =
                        QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_STRETCH

                mFullScreenRatioRB.id ->
                    PlayerSettingRespostory.ratioType =
                        QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_FULL_SCREEN

                m16_9RatioRB.id ->
                    PlayerSettingRespostory.ratioType =
                        QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_16_9

                m4_3RatioRB.id ->
                    PlayerSettingRespostory.ratioType =
                        QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_4_3
            }

            mPlayerCore.mPlayerContext.getPlayerRenderHandler().setRenderRatio(PlayerSettingRespostory.ratioType)
        }

        mBlindRG.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                mNoneBlindRB.id ->
                    PlayerSettingRespostory.blindType =
                        QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_NONE

                mRedBlindRB.id ->
                    PlayerSettingRespostory.blindType =
                        QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_RED

                mGreenBlindRB.id ->
                    PlayerSettingRespostory.blindType =
                        QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_GREEN

                mBlueBlindRB.id ->
                    PlayerSettingRespostory.blindType =
                        QPlayerSetting.QPlayerBlind.QPLAYER_BLIND_SETTING_BLUE
            }
            mPlayerCore.mPlayerContext.getPlayerRenderHandler().setBlindType(PlayerSettingRespostory.blindType)

        }

        mQualitySwitchTypeRG.setOnCheckedChangeListener { radioGroup, checkedId ->
            when (checkedId) {
                mQualitySwitchAutoRB.id ->
                    PlayerSettingRespostory.qualitySwitchType =
                        PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_TYPE_AUTO

                mQualitySwitchImmediatelyRB.id ->
                    PlayerSettingRespostory.qualitySwitchType =
                        PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_IMMEDIATELY

                mQualitySwitchSeamlessRB.id ->
                    PlayerSettingRespostory.qualitySwitchType =
                        PlayerSettingRespostory.QualitySwitchType.QPLAYER_QUALITY_SWITCH_SEAMLESS

            }
        }

        mStartPositionEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateDataSourceStartPos(mStartPositionEdit.editableText.toString().toLong())
            }
        }

        mRotationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rotation = progress * 360 / 1000
                mPlayerCore.mPlayerContext.getPlayerRenderHandler().setRotation(rotation)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        mScaleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var scale: Float = 0.0f
                if (progress >= 500) {
                    scale = 2.0f * progress / 1000
                } else {
                    scale = 0.5f + 0.5f * progress / 500

                }
                scale = 0.5f + 1.5f * progress / 1000
                mPlayerCore.mPlayerContext.getPlayerRenderHandler().setScale(scale)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


        mSEIEnableCB.setOnCheckedChangeListener { cb, is_checked ->
            PlayerSettingRespostory.seiEnable = is_checked
            mPlayerCore.mPlayerContext.getPlayerControlHandler().setSEIEnable(is_checked)
        }

        mShootVideoSourceCB.setOnCheckedChangeListener{ cb, is_checked ->
            PlayerSettingRespostory.isShootVideoSource = is_checked
        }

        mForceFlushAuthenticationCB.setOnCheckedChangeListener { cb, is_checked ->
            if (is_checked) {
                mPlayerCore.mPlayerContext.getPlayerControlHandler().forceAuthenticationFromNetwork()
            }
        }


    }

    private fun updateDataSourceStartPos(startPos: Long) {
        PlayerSettingRespostory.startPosition = startPos

        mPlayerCore.mCommonPlayerVideoSwitcher.updateDataSource(object: CommonPlayerDataSource.CommonPlayableParamsUpdater<LongPlayableParams, LongVideoParams > {
            override fun update(playableParams: LongPlayableParams) {
                if (!playableParams.mediaModel.isLive) {
                    playableParams.startPos = PlayerSettingRespostory.startPosition
                }
            }

            override fun update(videoParams: LongVideoParams) {
            }

            override fun filter(
                videoParams: LongVideoParams,
                playableParamsList: List<LongPlayableParams>
            ): Boolean {
                return false

            }
        })
    }

    override fun onRelease() {
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}