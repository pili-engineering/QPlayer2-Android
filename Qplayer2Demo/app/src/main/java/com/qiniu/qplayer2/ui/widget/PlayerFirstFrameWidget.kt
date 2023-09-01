package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerRenderHandler
import com.qiniu.qplayer2.logic.PlayerFirstFrameVM

class PlayerFirstFrameWidget  : AppCompatTextView {
    private val PlayerFirstFrameVM = PlayerFirstFrameVM()

    private val mObserver: Observer<Long> = Observer<Long> {
            it -> text = "first-frame:${it}"
    }


    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    fun setPlayerRenderHandler(renderHandler: QPlayerRenderHandler?) {
        PlayerFirstFrameVM.playerRenderHandler = renderHandler
        if (renderHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        PlayerFirstFrameVM.playerFirstFrameElapsedTimeLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        PlayerFirstFrameVM.playerFirstFrameElapsedTimeLiveData.removeObserver(mObserver)
    }
}