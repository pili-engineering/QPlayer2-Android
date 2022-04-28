package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.setting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qplayer2.R

import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayerDataSource
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.rxjava3.into
import com.qiniu.qplayer2ext.common.rxjava3.subscribeBy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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

    private lateinit var mStartPositionEdit: EditText


    private val mCompositeDisposable = CompositeDisposable();

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override val tag: String
        get() = "SettingFunctionWidget"

    override val functionWidgetConfig: PlayerFunctionContainer.FunctionWidgetConfig
        get() {
            val builder = PlayerFunctionContainer.FunctionWidgetConfig.Builder()
            builder.dismissWhenActivityStop(true)
            builder.dismissWhenScreenModeChange(true)
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

        mStartPositionEdit = view.findViewById(R.id.start_pos_ET)




        return view
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

    private fun updateStartPosition(startPos: Long) {
        mStartPositionEdit.setText(startPos.toString())
    }

    private fun registerSubjects() {
        PlayerSettingRespostory.decoderTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateDecoder(it)
                }
                onError { }
            } into mCompositeDisposable


        PlayerSettingRespostory.seekTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateSeekType(it)

                }
                onError { }
            } into mCompositeDisposable

        PlayerSettingRespostory.startTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateStartType(it)
                }
                onError { }
            } into mCompositeDisposable


        PlayerSettingRespostory.renderRatioSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateRenderRatio(it)
                }
                onError { }
            } into mCompositeDisposable



        PlayerSettingRespostory.startPositionSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateStartPosition(it)
                }
                onError { }
            } into mCompositeDisposable


        PlayerSettingRespostory.blindTypeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                onNext {
                    updateBlindType(it)
                }
                onError { }
            } into mCompositeDisposable

    }

    private fun unregisterSubjects() {
        mCompositeDisposable.clear()
    }

    private fun unRegisterClickListeners() {
        mDecodrRG.setOnCheckedChangeListener(null)

        mSeekRG.setOnCheckedChangeListener(null)

        mStartRG.setOnCheckedChangeListener(null)

        mRenderRatioRG.setOnCheckedChangeListener(null)

        mBlindRG.setOnCheckedChangeListener(null)

        mStartPositionEdit.setOnFocusChangeListener(null)
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

        mStartPositionEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                updateDataSourceStartPos(mStartPositionEdit.editableText.toString().toLong())
            }
        }

    }

    private fun updateDataSourceStartPos(startPos: Long) {
        PlayerSettingRespostory.startPosition = startPos

        mPlayerCore.mCommonPlayerController.updateDataSource(object: CommonPlayerDataSource.CommonPlayableParamsUpdater<LongPlayableParams, LongVideoParams > {
            override fun update(playableParams: LongPlayableParams) {
                if (!playableParams.isLive) {
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