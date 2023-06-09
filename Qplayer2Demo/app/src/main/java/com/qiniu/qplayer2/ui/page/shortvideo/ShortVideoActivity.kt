package com.qiniu.qplayer2.ui.page.shortvideo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
        private const val LOAD_BACKWARD_POS = 2

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
//                updateMediaItemContext()
        }
    }

    private val mPlayerSubtitleListener: QIPlayerSubtitleListener = object : QIPlayerSubtitleListener {
        override fun on_subtitle_text_change(text: String) {
            Log.d(TAG, "on_subtitle_text_change $text")
        }

        override fun on_subtitle_name_change(name: String) {
            Log.d(TAG, "on_subtitle_render_change $name")
        }

        override fun on_subtitle_close() {
            Log.d(TAG, "on_subtitle_close")
        }

        override fun on_subtitle_loaded(name: String, result: Boolean) {
            Log.d(TAG, "on_subtitle_loaded $name $result")
        }

        override fun on_subtitle_decoded(name: String, result: Boolean) {
            Log.d(TAG, "on_subtitle_decoded $name $result")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_short_video)
        mShortVideoRV = findViewById(R.id.short_video_RV)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mShortVideoRV.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mShortVideoRV)

        mVideoView = QSurfacePlayerView(this)
        mVideoView.playerControlHandler.addPlayerStateChangeListener(mPlayerStateChangeListener)
        mVideoView.playerRenderHandler.addPlayerRenderListener(mPlayerRenderListener)
        mVideoView.playerControlHandler.init(this)
        mVideoView.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO)
//        mVideoView.playerControlHandler.setDecodeType(QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_SOFT_PRIORITY)
        mVideoView.playerControlHandler.setLogLevel(QLogLevel.LOG_INFO)
//        mVideoView.playerControlHandler.setSubtitleEnable(true)
//        mVideoView.playerControlHandler.addPlayerSubtitleListener(mPlayerSubtitleListener)
        fetchVideoList()

    }

    override fun onDestroy() {
        mMediaItemContextManager.discardAllMediaItemContexts()
        mVideoView.playerControlHandler.release()
        super.onDestroy()
    }

    private fun fetchVideoList() {
        ModelFactory.createVideoItemListByURL(SHORT_VIDEO_PATH_PREFIX, object : ModelFactory.OnResultListener {
            override fun onSuccess(statusCode: Int, data: Any) {
                val items = data as ArrayList<VideoItem>
                items.forEach { videoItem ->
                    videoItem?.let {
                        val builder = QMediaModelBuilder()
                        builder.addStreamElement("", QURLType.QAUDIO_AND_VIDEO, 0, it.videoPath, true)
                        builder.addSubtitleElement("中文", "http://demo-videos.qnsdk.com/qiniu-short-video.srt", true)
                        val playItem = PlayItem(it.videoPath.hashCode(),
                            builder.build(false),
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
                updateMediaItemContext()
                val mediaItemContext = mMediaItemContextManager.fetchMediaItemContext(mPlayItemList.getOrNull(mCurrentPosition)?.id ?: 0)
                mCurrentVideoHolder?.startVideo(mediaItemContext)
            }
        }
    }

    private fun updateMediaItemContext() {

        var start = mCurrentPosition - LOAD_FORWARD_POS
        var end = mCurrentPosition - 1

        //当前pos的视频 不加载
        for (i: Int in start .. end) {
            mPlayItemList.getOrNull(i)?.let {
                mMediaItemContextManager.load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, this.getExternalFilesDir(null)?.path ?: "")
            }
        }

        start = mCurrentPosition + 1
        end = mCurrentPosition + LOAD_BACKWARD_POS
        for (i: Int in start .. end) {
            mPlayItemList.getOrNull(i)?.let {
                mMediaItemContextManager.load(it.id, it.mediaModel, 0, QLogLevel.LOG_VERBOSE, this.getExternalFilesDir(null)?.path ?: "")
            }
        }
    }
}