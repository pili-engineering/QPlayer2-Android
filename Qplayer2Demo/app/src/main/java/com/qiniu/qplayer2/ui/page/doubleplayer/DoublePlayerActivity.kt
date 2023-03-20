package com.qiniu.qplayer2.ui.page.doubleplayer

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.qiniu.qmedia.component.player.*
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2ext.common.measure.DpUtils

class DoublePlayerActivity: AppCompatActivity() {

    lateinit var mPlayerA: QSurfacePlayerView
    lateinit var mPlayerB: QSurfacePlayerView
    lateinit var mPlayerRoot: ConstraintLayout
    var IsPlayerABackendFlag: Boolean = true

    val mPlayerClickListener = View.OnClickListener {
        val backendLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        val frontLayoutParams = ConstraintLayout.LayoutParams(DpUtils.dpToPx(200), DpUtils.dpToPx(200))
        frontLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        frontLayoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID


        mPlayerRoot.removeView(mPlayerA)
        mPlayerRoot.removeView(mPlayerB)
        if (IsPlayerABackendFlag) {
            mPlayerA.layoutParams = frontLayoutParams
            mPlayerB.layoutParams = backendLayoutParams

            mPlayerRoot.addView(mPlayerB)
            mPlayerRoot.addView(mPlayerA)
            IsPlayerABackendFlag = false
        } else {
            mPlayerA.layoutParams = backendLayoutParams
            mPlayerB.layoutParams = frontLayoutParams

            mPlayerRoot.addView(mPlayerA)
            mPlayerRoot.addView(mPlayerB)
            IsPlayerABackendFlag = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_double_player)
        mPlayerA = findViewById(R.id.player_a)
        mPlayerB = findViewById(R.id.player_b)
        mPlayerRoot = findViewById(R.id.root_CL)
        mPlayerA.setOnClickListener(mPlayerClickListener)
        mPlayerB.setOnClickListener(mPlayerClickListener)

        mPlayerA.playerControlHandler.init(this)
        mPlayerA.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY)
        mPlayerA.playerRenderHandler.setRenderRatio(PlayerSettingRespostory.ratioType)
        mPlayerB.playerControlHandler.init(this)
        if (Build.MODEL.equals("PDVM00") && Build.MANUFACTURER.equals("OPPO")) {
            mPlayerB.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY)
        } else {
            mPlayerB.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_HARDWARE_PRIORITY)
        }

        mPlayerB.playerRenderHandler.setRenderRatio(PlayerSettingRespostory.ratioType)

        var builder = QMediaModelBuilder()
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0,
            "http://demo-videos.qnsdk.com/shortvideo/nike.mp4", true)
        mPlayerA.playerControlHandler.playMediaModel(builder.build(false), 0)
        mPlayerA.playerControlHandler.addPlayerStateChangeListener(object :
                QIPlayerStateChangeListener{
                override fun onStateChanged(state: QPlayerState) {
                    Log.d("PlayerA", "state=$state")
                    if (state == QPlayerState.COMPLETED) {
//                        mPlayerA.playerControlHandler.release()
                    }
                }
            })

        builder = QMediaModelBuilder()
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 1080,
            "http://demo-videos.qnsdk.com/qiniu-360p.mp4", true
        )
        mPlayerB.playerControlHandler.addPlayerStateChangeListener(object :
            QIPlayerStateChangeListener{
            override fun onStateChanged(state: QPlayerState) {
                Log.d("PlayerB", "state=$state")
            }
        })
        mPlayerB.playerControlHandler.playMediaModel(builder.build(false), 0)
    }

    override fun onDestroy() {
        mPlayerA.playerControlHandler.release()
        mPlayerB.playerControlHandler.release()
        super.onDestroy()
    }
}