package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videolist

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerVideoSwitcher
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.measure.DpUtils
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig

class VideoListFunctionWidget(context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context) {

    private lateinit var mTitleTV: TextView
    private lateinit var mVideosRV: RecyclerView
    private lateinit var mVideoListAdapter: VideoListAdapter
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private  val mVideoPlayEventListener = object :
        ICommonPlayerVideoSwitcher.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {
        override fun onVideoParamsStart(videoParams: LongVideoParams) {
            mVideoListAdapter.setSelectedVideoId(mPlayerCore.mCommonPlayerVideoSwitcher.getSwitchVideoParams()?.id?: -1L)
        }

        override fun onVideoParamsSetChanged() {
            super.onVideoParamsSetChanged()
            mVideoListAdapter.setItems(mPlayerCore.playerDataSource.getVideoParamsList())
            mVideoListAdapter.setSelectedVideoId(mPlayerCore.mCommonPlayerVideoSwitcher.getSwitchVideoParams()?.id?: -1L)

        }
        }

    override val functionWidgetConfig: FunctionWidgetConfig
        get() {
            val builder = FunctionWidgetConfig.Builder()
            builder.dismissWhenActivityStop(true)
            builder.dismissWhenScreenTypeChange(true)
            builder.dismissWhenVideoChange(true)
            builder.dismissWhenVideoCompleted(true)
            builder.persistent(true)
            builder.changeOrientationDisableWhenShow(true)
            return builder.build()
        }

    override val tag: String
        get() = "VideoListFunctionWidget"

    override fun onWidgetShow() {
        mVideoListAdapter.setItems(mPlayerCore.playerDataSource.getVideoParamsList())
        mVideoListAdapter.setSelectedVideoId(mPlayerCore.mCommonPlayerVideoSwitcher.getSwitchVideoParams()?.id?: -1L)

        mPlayerCore.mCommonPlayerVideoSwitcher.addVideoPlayEventListener(mVideoPlayEventListener)
    }

    override fun onWidgetDismiss() {
        mPlayerCore.mCommonPlayerVideoSwitcher.removeVideoPlayEventListener(mVideoPlayEventListener)
    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_video_list, null)
        mTitleTV = view.findViewById(R.id.title_TV)
        mVideosRV = view.findViewById(R.id.videos_RV)
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val margin = DpUtils.dpToPx(16)
        mVideosRV.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val layoutParams = view.layoutParams as RecyclerView.LayoutParams
                layoutParams.setMargins(margin, margin / 2, margin, margin / 2)
            }
        })
        mVideosRV.layoutManager = layoutManager
        mTitleTV.text = context.getString(R.string.common_player_video_list_title)


        mVideoListAdapter = VideoListAdapter(mContext, mPlayerCore.playerDataSource.getVideoParamsList())
        mVideosRV.adapter = mVideoListAdapter
        mVideoListAdapter.setItemClickListener(object : VideoListAdapter.OnItemClickListener {
            override fun onItemClick(id: Long) {
                mPlayerCore.mCommonPlayerVideoSwitcher.switchVideo(id)
//                mPlayerCore.playerFunctionWidgetContainer?.hideWidget(token)
            }
        })

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