package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.buffering

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig

class BufferingFunctionWidget(context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context)
{
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override val tag: String
        get() = "BufferingFunctionWidget"

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
    }

    override fun onWidgetDismiss() {
    }

    override fun createContentView(context: Context): View {
        return LayoutInflater.from(mContext).inflate(R.layout.function_buffering, null)
    }

    override fun onRelease() {
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}