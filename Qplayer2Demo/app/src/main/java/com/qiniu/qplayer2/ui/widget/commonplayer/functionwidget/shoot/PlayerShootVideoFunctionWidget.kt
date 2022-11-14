package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.shoot

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.speed.SpeedListAdapter
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.BaseFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetConfig
import java.io.ByteArrayInputStream


class PlayerShootVideoFunctionWidget(context: Context):
    BaseFunctionWidget<LongLogicProvider, LongPlayableParams, LongVideoParams>(context)  {


    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private lateinit var mImageView: ImageView
    private var mDrawable: Drawable? = null
    override val tag: String
        get() = "PlayerShootVideoFunctionWidget"

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

    override fun onWidgetShow() {

        mDrawable.also {
            mImageView.visibility = View.VISIBLE
            mImageView.setImageDrawable(it)
        }

    }

    override fun onWidgetShow(configuration: Configuration?) {
        configuration?.also {
            onConfigurationChanged(it)
        }
        super.onWidgetShow(configuration)
    }

    override fun onWidgetDismiss() {
        mDrawable = null
    }

    override fun createContentView(context: Context): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.function_shoot_video, null)
        mImageView = view.findViewById(R.id.shoot_viodeo_IV)
        return view
    }

    override fun onRelease() {
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        val imageStream = ByteArrayInputStream((configuration as PlayerShootVideoFunctionWidgetConfiguration).image)
        mDrawable = Drawable.createFromStream(imageStream, "")
        imageStream.close()
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    class PlayerShootVideoFunctionWidgetConfiguration(public val image: ByteArray) : Configuration {

        override fun different(other: Configuration): Boolean {
            return true
        }

    }
}