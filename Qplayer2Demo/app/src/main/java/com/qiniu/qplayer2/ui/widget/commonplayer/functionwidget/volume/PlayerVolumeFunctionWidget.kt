//package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.volume
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.ImageView
//import androidx.appcompat.widget.AppCompatSeekBar
//import com.qiniu.qmedia.component.player.QIPlayerAudioListener
//import com.qiniu.qplayer2.R
//import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
//import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
//import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
//import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
//import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
//import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig
//
//internal class PlayerVolumeFunctionWidget(context: Context) :
//    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context),
//QIPlayerAudioListener{
//
//    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
//    private lateinit var mImageView: ImageView
//    private lateinit var mSeekBar: AppCompatSeekBar
//    override val tag: String
//        get() = "PlayerShootVideoFunctionWidget"
//
//    override val functionWidgetConfig: FunctionWidgetConfig
//        get() {
//            val builder = FunctionWidgetConfig.Builder()
//            builder.dismissWhenActivityStop(true)
//            builder.dismissWhenScreenTypeChange(true)
//            builder.dismissWhenVideoChange(true)
//            builder.dismissWhenVideoCompleted(true)
//            builder.persistent(true)
//            builder.changeOrientationDisableWhenShow(true)
//            return builder.build()
//        }
//
//    override fun onWidgetShow() {
////        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerAudioListener(this)
////        mSeekBar.progress = mPlayerCore.mPlayerContext.getPlayerControlHandler().volume
//
//    }
//
//    override fun onWidgetDismiss() {
////        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerAudioListener(this)
//    }
//
//    override fun createContentView(context: Context): View {
//        val view = LayoutInflater.from(mContext).inflate(R.layout.function_audio_volume, null)
//        mImageView = view.findViewById(R.id.volume_IV)
//        mSeekBar = view.findViewById(R.id.volume_SB)
//        mSeekBar.max = 100
//        return view
//    }
//
//    override fun onRelease() {
//    }
//
//    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
//        mPlayerCore = playerCore
//    }
//
//    override fun onMuteChanged(volume: Int) {
//        mSeekBar.progress = volume
//    }
//
//    override fun onConfigurationChanged(configuration: Configuration) {
//
//    }
//}