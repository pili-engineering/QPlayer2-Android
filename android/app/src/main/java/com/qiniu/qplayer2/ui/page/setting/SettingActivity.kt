package com.qiniu.qplayer2.ui.page.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.logic.PlayerSettingVM

class SettingActivity : AppCompatActivity() {



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

    private lateinit var mSpeedRG: RadioGroup
    private lateinit var m_0_5_0_RB: RadioButton
    private lateinit var m_0_7_5_RB: RadioButton
    private lateinit var m_1_0_0_RB: RadioButton
    private lateinit var m_1_2_5_RB: RadioButton
    private lateinit var m_1_5_0_RB: RadioButton
    private lateinit var m_2_0_0_RB: RadioButton


    private lateinit var mStartPositionEdit: EditText


    private lateinit var mPlayerSettingVM: PlayerSettingVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPlayerSettingVM = PlayerSettingVM(this.lifecycle)

        setContentView(R.layout.activity_setting)

        mDecodrRG = findViewById(R.id.decoder_RG)
        mSeekRG = findViewById(R.id.seek_RG)
        mStartRG = findViewById(R.id.start_RG)
        mRenderRatioRG = findViewById(R.id.ratio_RG)
        mSpeedRG = findViewById(R.id.speed_RG)

        mAutoDecodrRB = findViewById(R.id.auto_decoder_RB)
        mSoftDecodrRB = findViewById(R.id.software_decoder_RB)
        mHardDecodrRB = findViewById(R.id.hardware_decoder_RB)
        mMixDecodrRB = findViewById(R.id.mix_decoder_RB)

        mNormalSeekRB = findViewById(R.id.normal_seek_RB)
        mAccurateSeekRB = findViewById(R.id.accurate_seek_RB)

        mPlayStartRB = findViewById(R.id.start_play_RB)
        mPauseStartRB = findViewById(R.id.start_pause_RB)

        mAutoRatioRB = findViewById(R.id.ratio_auto_RB)
        mStretchRatioRB = findViewById(R.id.ratio_stretch_RB)
        mFullScreenRatioRB = findViewById(R.id.ratio_full_screen_RB)
        m16_9RatioRB = findViewById(R.id.ratio_16_9_RB)
        m4_3RatioRB = findViewById(R.id.ratio_4_3_RB)

        m_0_5_0_RB = findViewById(R.id.speed_0_5_0_RB)
        m_0_7_5_RB = findViewById(R.id.speed_0_7_5_RB)
        m_1_0_0_RB = findViewById(R.id.speed_1_0_0_RB)
        m_1_2_5_RB = findViewById(R.id.speed_1_2_5_RB)
        m_1_5_0_RB = findViewById(R.id.speed_1_5_0_RB)
        m_2_0_0_RB = findViewById(R.id.speed_2_0_0_RB)

        mStartPositionEdit = findViewById(R.id.start_pos_ET)


        mSpeedRG.setOnCheckedChangeListener {
                group, checkedId ->
            when(checkedId) {
                m_0_5_0_RB.id ->
                    mPlayerSettingVM.setSpeed(0.5f)
                m_0_7_5_RB.id ->
                    mPlayerSettingVM.setSpeed(0.75f)
                m_1_0_0_RB.id ->
                    mPlayerSettingVM.setSpeed(1.0f)
                m_1_2_5_RB.id ->
                    mPlayerSettingVM.setSpeed(1.25f)
                m_1_5_0_RB.id ->
                    mPlayerSettingVM.setSpeed(1.5f)
                m_2_0_0_RB.id ->
                    mPlayerSettingVM.setSpeed(2.0f)

            }
        }

        mDecodrRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                mAutoDecodrRB.id ->
                    mPlayerSettingVM.setDecoderType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO)
                mSoftDecodrRB.id ->
                    mPlayerSettingVM.setDecoderType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_SOFT_PRIORITY)
                mHardDecodrRB.id ->
                    mPlayerSettingVM.setDecoderType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY)
                mMixDecodrRB.id ->
                    mPlayerSettingVM.setDecoderType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_FIRST_FRAME_ACCEL_PRIORITY)
            }
        }

        mSeekRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                mNormalSeekRB.id ->
                    mPlayerSettingVM.setSeekType(QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL)
                mAccurateSeekRB.id ->
                    mPlayerSettingVM.setSeekType(QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_ACCURATE)
            }
        }

        mStartRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                mPlayStartRB.id ->
                    mPlayerSettingVM.setStartType(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING)
                mPauseStartRB.id ->
                    mPlayerSettingVM.setStartType(QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE)
                }
        }

        mRenderRatioRG.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                mAutoRatioRB.id ->
                    mPlayerSettingVM.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO)
                mStretchRatioRB.id ->
                    mPlayerSettingVM.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_STRETCH)
                mFullScreenRatioRB.id ->
                    mPlayerSettingVM.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_FULL_SCREEN)
                m16_9RatioRB.id ->
                    mPlayerSettingVM.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_16_9)
                m4_3RatioRB.id ->
                    mPlayerSettingVM.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_4_3)

            }
        }



        mStartPositionEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if(mStartPositionEdit.editableText.toString().isEmpty()) {
                    mPlayerSettingVM.setStartPostion(0)
                } else {
                    mPlayerSettingVM.setStartPostion(mStartPositionEdit.editableText.toString().toLong())
                }

            }
        }


        registerVM()
    }

    private fun registerVM() {
        mPlayerSettingVM.decoderTypeLiveData.observe(this, Observer {

            when(it) {
                QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO ->
                    mAutoDecodrRB.isChecked = true
                QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_SOFT_PRIORITY ->
                    mSoftDecodrRB.isChecked = true
                QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY ->
                    mHardDecodrRB.isChecked = true
                QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_FIRST_FRAME_ACCEL_PRIORITY ->
                    mMixDecodrRB.isChecked = true
            }
        })

        mPlayerSettingVM.seekTypeLiveData.observe(this, Observer {

            when(it) {
                QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL ->
                    mNormalSeekRB.isChecked = true
                QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_ACCURATE ->
                    mAccurateSeekRB.isChecked = true
            }
        })

        mPlayerSettingVM.startTypeLiveData.observe(this, Observer {

            when(it) {
                QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING ->
                    mPlayStartRB.isChecked = true
                QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PAUSE ->
                    mPauseStartRB.isChecked = true
            }
        })

        mPlayerSettingVM.renderRatioLiveData.observe(this, Observer {

            when(it) {
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
        })

        mPlayerSettingVM.speedLiveData.observe(this, Observer {

            if (it < 0.51f) {
                m_0_5_0_RB.isChecked = true
            } else if (it < 0.76f)
                m_0_7_5_RB.isChecked = true
            else if (it < 1.01f)
                m_1_0_0_RB.isChecked = true
            else if (it < 1.26f)
                m_1_2_5_RB.isChecked = true
            else if (it < 1.51)
                m_1_5_0_RB.isChecked = true
            else
                m_2_0_0_RB.isChecked = true
        })

        mPlayerSettingVM.startPositionLiveData.observe(this, Observer {
            mStartPositionEdit.setText(it.toString())
        })


    }

    private fun unregisterVM() {
        mPlayerSettingVM.decoderTypeLiveData.removeObservers(this)
        mPlayerSettingVM.seekTypeLiveData.removeObservers(this)
        mPlayerSettingVM.startTypeLiveData.removeObservers(this)
        mPlayerSettingVM.startPositionLiveData.removeObservers(this)
        mPlayerSettingVM.speedLiveData.removeObservers(this)
    }

    override fun onPause() {
        super.onPause()


        if(mStartPositionEdit.editableText.toString().isEmpty()) {
            mPlayerSettingVM.setStartPostion(0)
        } else {
            mPlayerSettingVM.setStartPostion(mStartPositionEdit.editableText.toString().toLong())
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterVM()
    }
}