package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qmedia.component.player.QIPlayerSpeedListener
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.speed.SpeedFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams

class CommonPlayerSelectSpeedWidget: AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>, View.OnClickListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mSpeedToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? = null

    private val mSpeedChangeListener = object: QIPlayerSpeedListener {
        override fun onSpeedChanged(speed: Float) {
            updateSpeedText(speed)
        }

    }
    constructor(context: Context) : super(context) {
        init(context, null)

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)

    }

    private fun init(context: Context, attrs: AttributeSet?) {
        text = "倍速"
    }

    private fun updateSpeedText(speed: Float) {
        if (speed <= 0.51f) {
            text = "0.5X"
        } else if (speed < 0.76f) {
            text = "0.75X"
        } else if (speed < 1.01f) {
            text = context.getString(R.string.common_player_speed)
        } else if (speed < 1.26f) {
            text = "1.25X"
        } else if (speed < 1.51f) {
            text = "1.5X"
        }else {
            text = "2.0X"
        }
    }

    override fun onWidgetActive() {
        setOnClickListener(this)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerSpeedChangeListener(mSpeedChangeListener)
        updateSpeedText(mPlayerCore.mPlayerContext.getPlayerControlHandler().speed)

    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerSpeedListener(mSpeedChangeListener)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onClick(v: View?) {
        val layoutParams = FunctionWidgetLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.layoutType = FunctionWidgetLayoutParams.LAYOUT_TYPE_ALIGN_RIGHT
        mSpeedToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(SpeedFunctionWidget::class.java, layoutParams)
    }
}