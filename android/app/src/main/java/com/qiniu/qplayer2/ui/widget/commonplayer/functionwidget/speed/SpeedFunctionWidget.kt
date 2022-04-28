package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.speed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class SpeedFunctionWidget(context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context)  {

    private lateinit var mRecyclerView: RecyclerView
    private val mSpeedAdapter = SpeedListAdapter()
    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>

    override val tag: String
        get() = "SpeedFunctionWidget"

    override val functionWidgetConfig: PlayerFunctionContainer.FunctionWidgetConfig
        get() {
            val builder = PlayerFunctionContainer.FunctionWidgetConfig.Builder()
            builder.dismissWhenActivityStop(true)
            builder.dismissWhenScreenModeChange(true)
            builder.dismissWhenVideoChange(true)
            builder.dismissWhenVideoCompleted(true)
            builder.persistent(true)
            builder.changeOrientationDisableWhenShow(true)
            return builder.build()
        }

    override fun onWidgetShow() {
        mRecyclerView.adapter = mSpeedAdapter
        mSpeedAdapter.setItemSelectListener(object : SpeedListAdapter.OnItemSelectListener {
            override fun onItemSelected(speed: Float) {
                mPlayerCore.mPlayerContext.getPlayerControlHandler().setSpeed(speed)
                mPlayerCore.playerFunctionWidgetContainer?.hideWidget(token)
                PlayerSettingRespostory.playSpeed = speed
            }
        })
        val speed = mPlayerCore.mPlayerContext.getPlayerControlHandler().speed
        mSpeedAdapter.setData(speed)
        mSpeedAdapter.notifyDataSetChanged()
    }

    override fun onWidgetDismiss() {
    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_speed_list, null)
        mRecyclerView = view.findViewById(R.id.recycler)
        val layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        mRecyclerView.layoutManager = layoutManager
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