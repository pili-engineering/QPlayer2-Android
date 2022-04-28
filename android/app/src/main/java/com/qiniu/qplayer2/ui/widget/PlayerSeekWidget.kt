package com.qiniu.qplayer2.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.qiniu.qmedia.component.player.QPlayerControlHandler
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qplayer2.logic.PlayerProgressVM

class PlayerSeekWidget : AppCompatSeekBar,
    SeekBar.OnSeekBarChangeListener {

    companion object {
        const val TAG = "PlayerSeekWidget"
    }
    val mPlayerProgressVM = PlayerProgressVM()

    private val mProgressObserver: Observer<Long> = Observer<Long> { it ->
        if (mPlayerProgressVM.getDuration() > 0) {
            progress = (it * 1000 / mPlayerProgressVM.getDuration()).toInt()
//            Log.d(TAG, "play progress=${it.toInt()}")
        }

    }

    private val mBufferProgressObserver: Observer<Long> = Observer<Long> { it ->
        if (mPlayerProgressVM.getDuration() > 0) {
            secondaryProgress = (it * 1000 / mPlayerProgressVM.getDuration()).toInt()
            Log.d(TAG, "buffer progress=${secondaryProgress}")
        }

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
        mPlayerProgressVM.playerControlHandler = controlHandler
        if (controlHandler != null) {
            init()
        } else {
            uninit()
        }
    }

    private fun init() {
        mPlayerProgressVM.playerProgressLiveData.observeForever(mProgressObserver)
        mPlayerProgressVM.playBufferProgressLiveData.observeForever(mBufferProgressObserver)
        setOnSeekBarChangeListener(this)
    }

    private fun uninit() {
        mPlayerProgressVM.playerProgressLiveData.removeObserver(mProgressObserver)
        mPlayerProgressVM.playBufferProgressLiveData.removeObserver(mBufferProgressObserver)
        setOnSeekBarChangeListener(null)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        mPlayerProgressVM.seek(progress * mPlayerProgressVM.getDuration() / 1000 )
    }
}