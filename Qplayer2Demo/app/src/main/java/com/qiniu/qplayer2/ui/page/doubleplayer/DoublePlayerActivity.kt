package com.qiniu.qplayer2.ui.page.doubleplayer

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.qiniu.qmedia.component.player.QMediaModelBuilder
import com.qiniu.qmedia.component.player.QPlayerSetting
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.common.measure.DpUtils

class DoublePlayerActivity: AppCompatActivity() {

    lateinit var mPlayerA: QSurfacePlayerView
    lateinit var mPlayerB: QSurfacePlayerView
    lateinit var mPlayerRoot: ConstraintLayout
    var IsPlayerABackendFlag: Boolean = true

    val mPlayerClickListener = View.OnClickListener {
        val backendLayoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        val frontLayoutParams = ConstraintLayout.LayoutParams(DpUtils.dpToPx(400), DpUtils.dpToPx(400))
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
        mPlayerA.playerRenderHandler.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_STRETCH)
        mPlayerB.playerControlHandler.init(this)
        mPlayerB.playerRenderHandler.setRenderRatio(QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_STRETCH)

        var builder = QMediaModelBuilder()
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0,
            "http://demo-videos.qnsdk.com/shortvideo/nike.mp4", true)
        mPlayerA.playerControlHandler.playMediaModel(builder.build(false), 0)

        builder = QMediaModelBuilder()
        builder.addElement(
            "", QURLType.QVIDEO, 1080,
            "http://demo-videos.qnsdk.com/bbk-bt709.mp4", true
        )
        mPlayerB.playerControlHandler.playMediaModel(builder.build(false), 0)
    }

    override fun onDestroy() {
        mPlayerA.playerControlHandler.release()
        mPlayerB.playerControlHandler.release()
        super.onDestroy()
    }
}