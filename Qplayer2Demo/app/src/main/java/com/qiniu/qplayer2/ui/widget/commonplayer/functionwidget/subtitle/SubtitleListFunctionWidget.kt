package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.subtitle

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.QIPlayerSubtitleListener
import com.qiniu.qmedia.component.player.getSubtitleList
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.measure.DpUtils
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerVideoSwitcher
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig

class SubtitleListFunctionWidget(context: Context) :
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context) {

    private lateinit var mSubtitlesRV: RecyclerView
    private lateinit var mSubtitleListAdapter: SubtitleListAdapter
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mSubtitleListener = object : QIPlayerSubtitleListener {
        override fun on_subtitle_text_change(text: String) {
        }

        override fun on_subtitle_name_change(name: String) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateSubtitles(it)
            }
        }

        override fun on_subtitle_close() {
        }

        override fun on_subtitle_loaded(name: String, result: Boolean) {
        }

        override fun on_subtitle_decoded(name: String, result: Boolean) {
        }
    }

    private val mVideoPlayEventListener = object :
        ICommonPlayerVideoSwitcher.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {
        override fun onVideoParamsStart(videoParams: LongVideoParams) {
        }

        override fun onPlayableParamsStart(
            playableParams: LongPlayableParams,
            videoParams: LongVideoParams
        ) {
            super.onPlayableParamsStart(playableParams, videoParams)
            updateSubtitles(playableParams)
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
        get() = "SubtitleListFunctionWidget"

    private fun updateSubtitles(playableParams: LongPlayableParams) {
        mSubtitleListAdapter.setSubtitles(playableParams.mediaModel.getSubtitleList())
        if (!PlayerSettingRespostory.subtitleEnable) {
            mSubtitleListAdapter.setSelectedSubtitle("关闭")
        } else {
            mSubtitleListAdapter.setSelectedSubtitle(mPlayerCore.mPlayerContext.getPlayerControlHandler().subtitleName)
        }
    }

    override fun onWidgetShow() {

        mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
            updateSubtitles(it)
        }

        mPlayerCore.mCommonPlayerVideoSwitcher.addVideoPlayEventListener(mVideoPlayEventListener)
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .addPlayerSubtitleListener(mSubtitleListener)
    }

    override fun onWidgetDismiss() {
        mPlayerCore.mCommonPlayerVideoSwitcher.removeVideoPlayEventListener(mVideoPlayEventListener)
        mPlayerCore.mPlayerContext.getPlayerControlHandler()
            .removePlayerSubtitleListener(mSubtitleListener)

    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_subtitle_list, null)
        mSubtitlesRV = view.findViewById(R.id.subtitles_RV)
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val margin = DpUtils.dpToPx(16)
        mSubtitlesRV.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
        mSubtitlesRV.layoutManager = layoutManager


        mSubtitleListAdapter = SubtitleListAdapter(mContext)
        mSubtitlesRV.adapter = mSubtitleListAdapter
        mSubtitleListAdapter.setItemClickListener(object : SubtitleListAdapter.OnItemClickListener {

            override fun onItemClick(subtitleName: String) {
                if (subtitleName == "关闭") {
                    PlayerSettingRespostory.subtitleEnable = false
                    mPlayerCore.mPlayerContext.getPlayerControlHandler().setSubtitleEnable(false)
                } else {
                    PlayerSettingRespostory.subtitleEnable = true
                    mPlayerCore.mPlayerContext.getPlayerControlHandler().setSubtitle(subtitleName)
                    mPlayerCore.mPlayerContext.getPlayerControlHandler().setSubtitleEnable(true)

                }
                mPlayerCore.playerFunctionWidgetContainer?.hideWidget(token)
            }
        })

        return view
    }

    override fun onRelease() {

    }

    override fun onConfigurationChanged(configuration: BaseFunctionWidget.Configuration) {

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
}