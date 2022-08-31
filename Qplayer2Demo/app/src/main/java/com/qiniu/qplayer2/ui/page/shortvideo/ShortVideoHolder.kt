package com.qiniu.qplayer2.ui.page.shortvideo

import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.qiniu.qmedia.component.player.QIPlayerRenderListener
import com.qiniu.qmedia.component.player.QMediaItemContext
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.widget.*

class ShortVideoHolder(
    private val mItemView: View,
    private val mVideoPlayerView: QSurfacePlayerView
): RecyclerView.ViewHolder(mItemView) {

    private val mVideoContainerFL: FrameLayout = mItemView.findViewById(R.id.video_container_FL)
    private val mCoverIV: ImageView = mItemView.findViewById(R.id.cover_IV)

    private val mFPSWidget: PlayerFPSTextWidget = mItemView.findViewById(R.id.fps_TV)
    private val mDownloadTextWidget: PlayerDownloadTextWidget = mItemView.findViewById(R.id.download_speed_TV)
    private val mBiteRateTextWidget: PlayerBiteRateTextWidget = mItemView.findViewById(R.id.biterate_TV)
    private var mSeekWidget: PlayerSeekWidget = mItemView.findViewById(R.id.seek_bar)
    private var mProgressTextWidget: PlayerProgressTextWidget = mItemView.findViewById(R.id.progress_TV)
    private var mPlayWidget: PlayerPlayWidget = mItemView.findViewById(R.id.player_play_IV)
    private var mFullScreenPlayClickWidget: PlayerFullScreenPlayClickWidget = mItemView.findViewById(R.id.player_play_click_FL)
    private var mBufferingWidget: PlayerBufferingWidget = mItemView.findViewById(R.id.buffering_TV)


    private var mPlayItem: PlayItem? = null

    private val  mPlayerRenderListener : QIPlayerRenderListener = object : QIPlayerRenderListener {
        override fun onFirstFrameRendered(elapsedTime: Long) {
            mCoverIV.visibility = View.GONE
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

            if (mVideoPlayerView.parent == null) {
                mVideoContainerFL.addView(mVideoPlayerView, lp)
            }

            mFPSWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mDownloadTextWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mBiteRateTextWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mSeekWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mProgressTextWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mPlayWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mFullScreenPlayClickWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)
            mBufferingWidget.setPlayerControlHandler(mVideoPlayerView.playerControlHandler)

            mVideoPlayerView.playerRenderHandler.addPlayerRenderListener(mPlayerRenderListener)
            if (mediaItem != null) {
                mVideoPlayerView.playerControlHandler.playMediaItem(mediaItem)
            } else {
                mVideoPlayerView.playerControlHandler.playMediaModel(pi.mediaModel, 0)
            }
        }
    }

    fun stopVideo() {
        mCoverIV.visibility = View.VISIBLE
        mVideoPlayerView.playerRenderHandler.removePlayerRenderListener(mPlayerRenderListener)
        mVideoPlayerView.playerControlHandler.stop()

        mFPSWidget.setPlayerControlHandler(null)
        mDownloadTextWidget.setPlayerControlHandler(null)
        mBiteRateTextWidget.setPlayerControlHandler(null)
        mSeekWidget.setPlayerControlHandler(null)
        mProgressTextWidget.setPlayerControlHandler(null)
        mPlayWidget.setPlayerControlHandler(null)
        mFullScreenPlayClickWidget.setPlayerControlHandler(null)
        mBufferingWidget.setPlayerControlHandler(null)

        mVideoContainerFL.removeView(mVideoPlayerView)
    }
}