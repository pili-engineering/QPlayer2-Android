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
import com.qiniu.qmedia.component.player.QMediaItemContext
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
    private val mItemView: View,
    private val mSurfacePlayerViewManager: SurfacePlayerViewManager
): RecyclerView.ViewHolder(mItemView) {

    companion object {
        private const val TAG = "ShortVideoHolderV2"
    }
    
    private var mVideoPlayerView: QSurfacePlayerView? = null

    private val mVideoContainerFL: FrameLayout = mItemView.findViewById(R.id.video_container_FL)
    private val mCoverIV: ImageView = mItemView.findViewById(R.id.cover_IV)

    private val mFPSWidget: PlayerFPSTextWidget = mItemView.findViewById(R.id.fps_TV)
    private val mDownloadTextWidget: PlayerDownloadTextWidget = mItemView.findViewById(R.id.download_speed_TV)
    private val mBiteRateTextWidget: PlayerBiteRateTextWidget = mItemView.findViewById(R.id.biterate_TV)
    private var mSeekWidget: PlayerSeekWidget = mItemView.findViewById(R.id.seek_bar)
    private var mProgressTextWidget: PlayerProgressTextWidget = mItemView.findViewById(R.id.progress_TV)
    private var mPlayWidget: PlayerPlayWidget = mItemView.findViewById(R.id.player_play_IV)
    private var mFullScreenPlayClickWidget: PlayerFullScreenPlayClickWidget = mItemView.findViewById(
        R.id.player_play_click_FL)
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
            }
        }
    }

    private val  mPlayerRenderListener : QIPlayerRenderListener = object : QIPlayerRenderListener {
        override fun onFirstFrameRendered(elapsedTime: Long) {
            mCoverIV.visibility = View.GONE
            Log.d(TAG, "onFirstFrameRendered: $elapsedTime")
        }
    }

    fun bind(playItem: PlayItem, postion: Int) {
        mPlayItem = playItem
        mCoverIV.load(playItem.coverUrl){
            listener(onError = { request, _ ->
                //设置点击事件，点击重新加载
                Log.i("AAA", "ERROR ")

            }, onSuccess = { _, _ ->
                Log.i("AAA", "SUCESS ")
            })
        }
        mItemView.tag = postion
    }

    fun startVideo(mediaItem: QMediaItemContext?){
        mPlayItem?.let { pi ->
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.gravity = Gravity.CENTER
            val playerView = mSurfacePlayerViewManager.fetchSurfacePlayerView()
            mVideoPlayerView = playerView
            if (playerView?.parent == null) {
                mVideoContainerFL.addView(playerView, lp)
            }

            mFPSWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mDownloadTextWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mBiteRateTextWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mSeekWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mProgressTextWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mPlayWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mFullScreenPlayClickWidget.setPlayerControlHandler(playerView.playerControlHandler)
            mBufferingWidget.setPlayerControlHandler(playerView.playerControlHandler)

            playerView.playerRenderHandler.addPlayerRenderListener(mPlayerRenderListener)
            playerView.playerControlHandler.addPlayerStateChangeListener(mPlayerStateChangeListener)
            if (mediaItem != null) {
                playerView.playerControlHandler.playMediaItem(mediaItem)
            } else {
                playerView.playerControlHandler.playMediaModel(pi.mediaModel, 0)
            }
        }
    }

    fun stopVideo() {
        mCoverIV.visibility = View.VISIBLE
        mVideoPlayerView?.also {
            it.playerRenderHandler.removePlayerRenderListener(mPlayerRenderListener)
            it.playerControlHandler.removePlayerStateChangeListener(mPlayerStateChangeListener)

            it.playerControlHandler.stop()
            mVideoContainerFL.removeView(it)
            mSurfacePlayerViewManager.recycleSurfacePlayerView(it)
        }

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