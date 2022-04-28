package com.qiniu.qplayer2.ui.page.shortvideo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.*
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.repository.shortvideo.ModelFactory
import com.qiniu.qplayer2.repository.shortvideo.VideoItem
import java.util.*

class ShortVideoActivity : AppCompatActivity() {

    private lateinit var mShortVideoRV: RecyclerView
    private lateinit var mVideoView: QSurfacePlayerView
    private lateinit var mShortVideoListAdapter: ShortVideoListAdapter
    private var mCurrentPosition = -1
    private var mCurrentVideoHolder: ShortVideoHolder? = null
    private val mPlayItemList = ArrayList<PlayItem>()
    private val mMediaItemContextManager = MediaItemContextManager()
    private var mFirstPlay = true


    companion object {
        private const val TAG = "ShortVideoActivity"
        private const val SHORT_VIDEO_PATH_PREFIX =
            "https://api-demo.qnsdk.com/v1/kodo/bucket/demo-videos?prefix=shortvideo"

        private const val LOAD_FORWARD_POS = 1
        private const val LOAD_BACKWARD_POS = 1

    }

    private val mOnChildAttachStateChangeListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            startPlay()
        }

        override fun onChildViewDetachedFromWindow(view: View) {
        }
    }

    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                changeVideo()
            }
        }
    }

    private val mPlayerStateChangeListener: QIPlayerStateChangeListener = object : QIPlayerStateChangeListener {
        override fun onStateChanged(state: QPlayerState) {
            if (state == QPlayerState.COMPLETED) {
                //循环播放
                mVideoView.playerControlHandler.seek(0)
            }
        }
    }

    private val  mPlayerRenderListener : QIPlayerRenderListener = object : QIPlayerRenderListener {
        override fun onFirstFrameRendered(elapsedTime: Long) {
                updateMediaItemContext()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_short_video)
        mShortVideoRV = findViewById(R.id.short_video_RV)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mShortVideoRV.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mShortVideoRV)

        mVideoView = QSurfacePlayerView(this)
        mVideoView.playerControlHandler.addPlayerStateChangeListener(mPlayerStateChangeListener)
        mVideoView.playerRenderHandler.addPlayerRenderChangeListener(mPlayerRenderListener)
        mVideoView.playerControlHandler.start()
        fetchVideoList()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaItemContextManager.discardAllMediaItemContexts()
        mVideoView.playerControlHandler.stop()
        mVideoView.playerControlHandler.release()
    }

    private fun fetchVideoList() {
        ModelFactory.createVideoItemListByURL(SHORT_VIDEO_PATH_PREFIX, object : ModelFactory.OnResultListener {
            override fun onSuccess(statusCode: Int, data: Any) {
                val items = data as ArrayList<VideoItem>
                items.forEach { videoItem ->
                    videoItem?.let {
                        val builder = QMediaModelBuilder()
                        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, it.videoPath, true)

                        val playItem = PlayItem(it.videoPath.hashCode(),
                            builder.build(),
                            it.coverPath)

                        mPlayItemList.add(playItem)
                    }
                }

                runOnUiThread(Runnable {
                    initView()
                })

            }

            override fun onFailure() {
                runOnUiThread(Runnable {
                    Toast.makeText(
                        this@ShortVideoActivity,
                        "获得短视频列表失败",
                        Toast.LENGTH_LONG
                    ).show()
                })
            }
        })
    }

    private fun initView() {
        mShortVideoListAdapter = ShortVideoListAdapter(mPlayItemList, mVideoView)
        mShortVideoRV.adapter = mShortVideoListAdapter
        mShortVideoRV.addOnScrollListener(mOnScrollListener)
        mShortVideoRV.addOnChildAttachStateChangeListener(mOnChildAttachStateChangeListener)
    }

    private fun startPlay() {
        if (mFirstPlay) {
            mFirstPlay = false
            changeVideo()
        }
    }


    private fun changeVideo() {
        val layoutManager = mShortVideoRV.layoutManager as LinearLayoutManager
        val visibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        if (visibleItemPosition >= 0 && mCurrentPosition != visibleItemPosition) {
            mCurrentVideoHolder?.stopVideo()
            mCurrentPosition = visibleItemPosition
            mShortVideoRV.findViewWithTag<View>(mCurrentPosition)?.let {
                mCurrentVideoHolder = mShortVideoRV.getChildViewHolder(it) as ShortVideoHolder
                val mediaItemContext = mMediaItemContextManager.fetchMediaItemContext(mPlayItemList.getOrNull(mCurrentPosition)?.id ?: 0)
                mCurrentVideoHolder?.startVideo(mediaItemContext)
            }
        }
    }

    private fun updateMediaItemContext() {

        //当前pos的视频 不加载
        for (i: Int in 0 .. (mCurrentPosition - LOAD_FORWARD_POS)) {
            mPlayItemList.getOrNull(i)?.let {
                mMediaItemContextManager.load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, this.getExternalFilesDir(null)?.path ?: "")
            }
        }

        val start = mCurrentPosition + 1
        val end = if((mCurrentPosition + LOAD_BACKWARD_POS) < mPlayItemList.size) mCurrentPosition + LOAD_BACKWARD_POS else mPlayItemList.size
        for (i: Int in start .. end) {
            mPlayItemList.getOrNull(i)?.let {
                mMediaItemContextManager.load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, this.getExternalFilesDir(null)?.path ?: "")
            }
        }
    }
}