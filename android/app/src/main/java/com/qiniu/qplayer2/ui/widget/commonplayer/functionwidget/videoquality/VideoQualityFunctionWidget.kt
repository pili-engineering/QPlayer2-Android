package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videoquality

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.*
import com.qiniu.qmedia.component.player.QPlayerControlHandler.Companion.INVALID_QUALITY_ID
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.controller.ICommonPlayerVideoSwitcher
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.measure.DpUtils
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig

class VideoQualityFunctionWidget (context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context) {

    private lateinit var mQualitysRV: RecyclerView
    private lateinit var mQualityListAdapter: VideoQualityListAdapter
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    private val mVideoQualityListener = object : QIPlayerQualityListener {
        override fun onQualitySwitchStart(
            userType: String,
            urlType: QURLType,
            newQuality: Int,
            oldQuality: Int
        ) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateQualitys(it)
            }
        }

        override fun onQualitySwitchComplete(
            userType: String,
            urlType: QURLType,
            newQuality: Int,
            oldQuality: Int
        ) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateQualitys(it)
            }
        }

        override fun onQualitySwitchCanceled(
            userType: String,
            urlType: QURLType,
            newQuality: Int,
            oldQuality: Int
        ) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateQualitys(it)
            }
        }

        override fun onQualitySwitchFailed(
            userType: String,
            urlType: QURLType,
            newQuality: Int,
            oldQuality: Int
        ) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateQualitys(it)
            }
        }

        override fun onQualitySwitchRetryLater(
            userType: String,
            urlType: QURLType,
            newQuality: Int
        ) {
            mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
                updateQualitys(it)
            }
        }

    }

    private  val mVideoPlayEventListener = object :
        ICommonPlayerVideoSwitcher.ICommonVideoPlayEventListener<LongPlayableParams, LongVideoParams> {
        override fun onVideoParamsStart(videoParams: LongVideoParams) {
        }

        override fun onPlayableParamsStart(
            playableParams: LongPlayableParams,
            videoParams: LongVideoParams
        ) {
            super.onPlayableParamsStart(playableParams, videoParams)
            updateQualitys(playableParams)
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
        get() = "QualityFunctionWidget"

    private fun updateQualitys(playableParams: LongPlayableParams) {
        mQualityListAdapter.setQualities(playableParams.mediaModel.getVideoQualityList())
        var selectedQualityId = mPlayerCore.mPlayerContext.getPlayerControlHandler().getCurrentQuality("", QURLType.QVIDEO)
        var switchingQualityId = INVALID_QUALITY_ID
        var urlType = QURLType.QVIDEO
        if (selectedQualityId == INVALID_QUALITY_ID) {
            selectedQualityId = mPlayerCore.mPlayerContext.getPlayerControlHandler().getCurrentQuality("", QURLType.QAUDIO_AND_VIDEO)
            switchingQualityId = mPlayerCore.mPlayerContext.getPlayerControlHandler().getSwitchingQuality("", QURLType.QAUDIO_AND_VIDEO)
            urlType = QURLType.QAUDIO_AND_VIDEO
        } else {
            mQualityListAdapter.setSelectedQuality(QQuality("", QURLType.QVIDEO, selectedQualityId))
            switchingQualityId = mPlayerCore.mPlayerContext.getPlayerControlHandler().getSwitchingQuality("", QURLType.QVIDEO)

        }

        if (switchingQualityId != INVALID_QUALITY_ID) {
            mQualityListAdapter.setSelectedQuality(QQuality("", urlType, switchingQualityId))
        } else if (selectedQualityId != INVALID_QUALITY_ID) {
            mQualityListAdapter.setSelectedQuality(QQuality("", urlType, selectedQualityId))
        }
    }
    override fun onWidgetShow() {

        mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.also {
            updateQualitys(it)
        }

        mPlayerCore.mCommonPlayerVideoSwitcher.addVideoPlayEventListener(mVideoPlayEventListener)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerQualityListener(mVideoQualityListener)
    }

    override fun onWidgetDismiss() {
        mPlayerCore.mCommonPlayerVideoSwitcher.removeVideoPlayEventListener(mVideoPlayEventListener)
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerQualityListener(mVideoQualityListener)

    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_video_quality_list, null)
        mQualitysRV = view.findViewById(R.id.videos_RV)
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val margin = DpUtils.dpToPx(16)
        mQualitysRV.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
        mQualitysRV.layoutManager = layoutManager


        mQualityListAdapter = VideoQualityListAdapter(mContext, ArrayList<QQuality>())
        mQualitysRV.adapter = mQualityListAdapter
        mQualityListAdapter.setItemClickListener(object : VideoQualityListAdapter.OnItemClickListener {

            override fun onItemClick(quality: QQuality) {
                mPlayerCore.mCommonPlayerVideoSwitcher.getCurrentPlayableParams()?.mediaModel?.isLive?.let { isLive->
                    mPlayerCore.mPlayerContext.getPlayerControlHandler().switchQuality(
                        quality.userType,
                        quality.urlType,
                        quality.quality, isLive)
                    mPlayerCore.playerFunctionWidgetContainer?.hideWidget(token)
                }
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