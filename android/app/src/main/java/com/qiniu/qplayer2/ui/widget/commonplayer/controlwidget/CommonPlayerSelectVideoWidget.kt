package com.qiniu.qplayer2.ui.widget.commonplayer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videolist.VideoListFunctionWidget

class CommonPlayerSelectVideoWidget: AppCompatTextView,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>, View.OnClickListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mVideoListToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? = null


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
        text = "选集"
    }

    override fun onWidgetActive() {
        setOnClickListener(this)

    }

    override fun onWidgetInactive() {
        setOnClickListener(null)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun onClick(v: View?) {
        val layoutParams = PlayerFunctionContainer.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.layoutType = PlayerFunctionContainer.LayoutParams.LAYOUT_TYPE_ALIGN_RIGHT
        mVideoListToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(VideoListFunctionWidget::class.java, layoutParams)
    }
}