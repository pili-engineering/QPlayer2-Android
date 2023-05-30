package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.subtitle

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.measure.DpUtils
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig
import com.qiniu.qplayer2ext.commonplayer.screen.ICommonPlayerScreenChangedListener
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType


class SubtitleFunctionWidget(context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context),
    ICommonPlayerScreenChangedListener
{
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private lateinit var mSubtitleTextTV: TextView

    override val tag: String
        get() = "SubtitleFunctionWidget"

    override val functionWidgetConfig: FunctionWidgetConfig
        get() {
            val builder = FunctionWidgetConfig.Builder()
            builder.persistent(true)
            builder.dismissWhenVideoCompleted(true)
            builder.dismissWhenVideoChange(false)
            builder.dismissWhenScreenTypeChange(false)
            builder.dismissWhenActivityStop(false)
            builder.launchType(FunctionWidgetConfig.LaunchType.Normal)
            return builder.build()
        }

    override fun onWidgetShow() {
        mPlayerCore.getCommonPlayerScreenHandler()?.getCurrentScreenType()?.let {
            onScreenTypeChanged(it)
        }

        mPlayerCore.getCommonPlayerScreenHandler()?.addScreenChangedListener(this)
    }

    override fun onWidgetDismiss() {
        mPlayerCore.getCommonPlayerScreenHandler()?.removeScreenChangedListener(this)
    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_subtitle, null)
        mSubtitleTextTV = view.findViewById(R.id.subtitle_text_TV)
        return view
    }

    override fun onRelease() {
    }

    override fun onConfigurationChanged(configuration: Configuration) {

        mSubtitleTextTV.text = (configuration as PlayerSubtitleFunctionWidgetConfiguration).subtitleText
        if (mSubtitleTextTV.text == "") {
            mSubtitleTextTV.visibility = View.INVISIBLE
        } else {
            mSubtitleTextTV.visibility = View.VISIBLE
        }
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    class PlayerSubtitleFunctionWidgetConfiguration(public val subtitleText: String) : Configuration {

        override fun different(other: Configuration): Boolean {
            return true
        }

    }

    override fun onScreenTypeChanged(screenType: ScreenType) {
        // 创建布局参数对象
        // 创建布局参数对象
        val layoutParams = ConstraintLayout.LayoutParams(mSubtitleTextTV.layoutParams)
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        if (screenType == ScreenType.HALF_SCREEN) {
            mSubtitleTextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f)
            layoutParams.setMargins(DpUtils.dpToPx(8), 0, DpUtils.dpToPx(8), DpUtils.dpToPx(20))
        } else {
            mSubtitleTextTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f)
            layoutParams.setMargins(DpUtils.dpToPx(12), 0, DpUtils.dpToPx(12), DpUtils.dpToPx(50))
        }
        mSubtitleTextTV.layoutParams = layoutParams
    }
}