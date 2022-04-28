package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qplayer2.logic.PlayerBufferingVM
import com.qiniu.qplayer2.logic.PlayerProgressVM

class PlayerBufferingWidget : AppCompatTextView {

    private val mPlayerBufferingVM = PlayerBufferingVM()
    private val mObserver: Observer<Boolean> = Observer<Boolean> { it ->
        visibility = if (it) View.VISIBLE else View.INVISIBLE
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

    fun setPlayerControlHandler(controlHandler: QPlayerControlHandler?) {
        mPlayerBufferingVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        mPlayerBufferingVM.playerBufferingLiveData.observeForever(mObserver)
    }

    private fun uninit() {
        mPlayerBufferingVM.playerBufferingLiveData.removeObserver(mObserver)

    }
}