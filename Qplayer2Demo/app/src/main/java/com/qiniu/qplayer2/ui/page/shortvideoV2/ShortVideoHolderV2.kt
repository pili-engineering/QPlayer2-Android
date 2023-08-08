package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.qiniu.qmedia.component.player.QIPlayerRenderListener
import com.qiniu.qmedia.component.player.QIPlayerStateChangeListener
import com.qiniu.qmedia.component.player.QPlayerState
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.shortvideo.PlayItem
import com.qiniu.qplayer2.ui.widget.PlayerBiteRateTextWidget
import com.qiniu.qplayer2.ui.widget.PlayerBufferingWidget
import com.qiniu.qplayer2.ui.widget.PlayerDownloadTextWidget
import com.qiniu.qplayer2.ui.widget.PlayerFPSTextWidget
import com.qiniu.qplayer2.ui.widget.PlayerFullScreenPlayClickWidget
import com.qiniu.qplayer2.ui.widget.PlayerPlayWidget
import com.qiniu.qplayer2.ui.widget.PlayerProgressTextWidget
import com.qiniu.qplayer2.ui.widget.PlayerSeekWidget

class ShortVideoHolderV2(
    private val mShortVideoPlayerViewCacheRecycler: IShortVideoPlayerViewCacheRecycler,
    private val mItemView: View) : RecyclerView.ViewHolder(mItemView) {

    companion object {
        private const val TAG = "ShortVideoHolderV2"
    }

    private var mVideoPlayerView: QSurfacePlayerView? = null

    private val mVideoContainerFL: FrameLayout = mItemView.findViewById(R.id.video_container_FL)
    private val mCoverIV: ImageView = mItemView.findViewById(R.id.cover_IV)

    private val mFPSWidget: PlayerFPSTextWidget = mItemView.findViewById(R.id.fps_TV)
    private val mDownloadTextWidget: PlayerDownloadTextWidget =
        mItemView.findViewById(R.id.download_speed_TV)
    private val mBiteRateTextWidget: PlayerBiteRateTextWidget =
        mItemView.findViewById(R.id.biterate_TV)
    private var mSeekWidget: PlayerSeekWidget = mItemView.findViewById(R.id.seek_bar)
    private var mProgressTextWidget: PlayerProgressTextWidget =
        mItemView.findViewById(R.id.progress_TV)
    private var mPlayWidget: PlayerPlayWidget = mItemView.findViewById(R.id.player_play_IV)
    private var mFullScreenPlayClickWidget: PlayerFullScreenPlayClickWidget =
        mItemView.findViewById(
            R.id.player_play_click_FL
        )
    private var mBufferingWidget: PlayerBufferingWidget = mItemView.findViewById(R.id.buffering_TV)


    private var mPlayItem: PlayItem? = null

    public val playItemId: Int
        get() = mPlayItem?.id ?: 0

    private val mPlayerStateChangeListener: QIPlayerStateChangeListener = object :
        QIPlayerStateChangeListener {
        override fun onStateChanged(state: QPlayerState) {
            if (state == QPlayerState.COMPLETED) {
                //循环播放
                mVideoPlayerView?.playerControlHandler?.seek(0)
            } else if (state == QPlayerState.PLAYING) {
                mCoverIV.visibility = View.GONE
            }
        }
    }

    private val mPlayerRenderListener: QIPlayerRenderListener = object : QIPlayerRenderListener {
        override fun onFirstFrameRendered(elapsedTime: Long) {
            mCoverIV.visibility = View.GONE
            Log.d(TAG, "onFirstFrameRendered: $elapsedTime")
        }
    }

    fun bind(playItem: PlayItem?, postion: Int) {
        mPlayItem = playItem
        mItemView.tag = postion
        mPlayItem?.let {
            mCoverIV.load(it.coverUrl) {
                listener(onError = { request, _ ->
                    //设置点击事件，点击重新加载
                    Log.i("AAA", "ERROR ")

                }, onSuccess = { _, _ ->
                    Log.i("AAA", "SUCESS ")
                })
            }
        }
    }

    fun startVideo(surfacePlayerView: QSurfacePlayerView) {
        mVideoPlayerView = surfacePlayerView
        mVideoPlayerView?.let {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.gravity = Gravity.CENTER

            if (it.playerControlHandler.currentPlayerState == QPlayerState.PLAYING) {
                mCoverIV.visibility = View.GONE
            }
            Log.d(TAG, "startVideo player-state=${it.playerControlHandler.currentPlayerState}")

            if (it.parent == null) {
                mVideoContainerFL.addView(it, lp)
            }

            mFPSWidget.setPlayerControlHandler(it.playerControlHandler)
            mDownloadTextWidget.setPlayerControlHandler(it.playerControlHandler)
            mBiteRateTextWidget.setPlayerControlHandler(it.playerControlHandler)
            mSeekWidget.setPlayerControlHandler(it.playerControlHandler)
            mProgressTextWidget.setPlayerControlHandler(it.playerControlHandler)
            mPlayWidget.setPlayerControlHandler(it.playerControlHandler)
            mFullScreenPlayClickWidget.setPlayerControlHandler(it.playerControlHandler)
            mBufferingWidget.setPlayerControlHandler(it.playerControlHandler)

            it.playerRenderHandler.addPlayerRenderListener(mPlayerRenderListener)
            it.playerControlHandler.addPlayerStateChangeListener(mPlayerStateChangeListener)
        }
    }

    fun stopVideo() {
        mCoverIV.visibility = View.VISIBLE
        mVideoPlayerView?.also {
            it.playerRenderHandler.removePlayerRenderListener(mPlayerRenderListener)
            it.playerControlHandler.removePlayerStateChangeListener(mPlayerStateChangeListener)

            it.playerControlHandler.stop()
            mVideoContainerFL.removeView(it)
            mShortVideoPlayerViewCacheRecycler.recycleSurfacePlayerView(it)
        }
        mVideoPlayerView = null
        mFPSWidget.setPlayerControlHandler(null)
        mDownloadTextWidget.setPlayerControlHandler(null)
        mBiteRateTextWidget.setPlayerControlHandler(null)
        mSeekWidget.setPlayerControlHandler(null)
        mProgressTextWidget.setPlayerControlHandler(null)
        mPlayWidget.setPlayerControlHandler(null)
        mFullScreenPlayClickWidget.setPlayerControlHandler(null)
        mBufferingWidget.setPlayerControlHandler(null)
    }
}