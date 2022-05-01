package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.error

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.screen.ICommonPlayerScreenChangedListener
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig

class ErrorFunctionWidget (context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context)
{
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private lateinit var mButton: Button
//    private lateinit var mBackIV: ImageView

    private val mRetryClickLister = View.OnClickListener {
        mPlayerCore.mCommonPlayerController.replayCurrentVideo()
    }

    private val mBackClickListener =
        View.OnClickListener { mPlayerCore.getCommonPlayerScreenHandler()?.switchScreenType(ScreenType.HALF_SCREEN) }

    private val mCommonPlayerScreenChangedListener = object : ICommonPlayerScreenChangedListener {
        override fun onScreenTypeChanged(screenType: ScreenType) {
//            if (screenType == ScreenType.HALF_SCREEN) {
//                mBackIV.visibility = View.INVISIBLE
//            } else {
//                mBackIV.visibility = View.VISIBLE
//            }
        }

    }

    override val tag: String
        get() = "ErrorFunctionWidget"


    override val functionWidgetConfig: FunctionWidgetConfig
        get() {
            val builder = FunctionWidgetConfig.Builder()
            builder.dismissWhenActivityStop(true)
            builder.dismissWhenScreenModeChange(true)
            builder.dismissWhenVideoChange(true)
            builder.dismissWhenVideoCompleted(true)
            builder.persistent(true)
            builder.changeOrientationDisableWhenShow(true)
            return builder.build()
        }

    override fun onWidgetShow() {
        mButton.setOnClickListener(mRetryClickLister)
//        mBackIV.setOnClickListener(mBackClickListener)
        mPlayerCore.getCommonPlayerScreenHandler()?.addScreenChangedListener(mCommonPlayerScreenChangedListener)
    }

    override fun onWidgetDismiss() {
        mButton.setOnClickListener(null)
//        mBackIV.setOnClickListener(null)
        mPlayerCore.getCommonPlayerScreenHandler()?.removeScreenChangedListener(mCommonPlayerScreenChangedListener)


    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_error, null)
        mButton = view.findViewById<Button>(R.id.button)
//        mBackIV = view.findViewById<ImageView>(R.id.back_IV)
        return view
    }

    override fun onRelease() {
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}