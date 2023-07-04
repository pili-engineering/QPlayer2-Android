package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.subtitle.SubtitleListFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer


class CommonPlayerSubtitleWidget : AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>, View.OnClickListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mSubtitleToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null

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
        text = "字幕"
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onWidgetActive() {
        setOnClickListener(this)
    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
    }

    override fun onClick(p0: View?) {
        val layoutParams = FunctionWidgetLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.RIGHT
        mSubtitleToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
            SubtitleListFunctionWidget::class.java, layoutParams
        )
    }
}