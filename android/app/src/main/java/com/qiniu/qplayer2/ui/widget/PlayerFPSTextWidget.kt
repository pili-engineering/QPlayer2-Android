package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qplayer2.logic.PlayerFPSVM

class PlayerFPSTextWidget : AppCompatTextView {
    private val mPlayerFPSVM = PlayerFPSVM()

    private val mObserver:Observer<Int>  = Observer<Int> { it -> text = "FPS:${it}" }

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

    fun setPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        mPlayerFPSVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        mPlayerFPSVM.playerFPSLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        mPlayerFPSVM.playerFPSLiveData.removeObserver(mObserver)
    }
}