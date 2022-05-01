package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams

class CommonPlayerFullScreenWidget: AppCompatImageView, View.OnClickListener,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>


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
        contentDescription = "bbplayer_halfscreen_expand"
        val drawableCompat = ContextCompat.getDrawable(context, R.drawable.qmedia_ic_player_fullscreen_vector)
        if (drawableCompat != null) {
            setImageDrawable(drawableCompat)
        }

    }

    override fun onClick(v: View?) {
        mPlayerCore.getCommonPlayerScreenHandler()?.switchScreenType(ScreenType.FULL_SCREEN)
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
}