package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.qiniu.qmedia.component.player.QMediaModelBuilder
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.repository.shortvideo.ModelFactory
import com.qiniu.qplayer2.repository.shortvideo.VideoItem
import com.qiniu.qplayer2.ui.page.shortvideo.PlayItem

class ShortVideoActivityV2 : AppCompatActivity() {

    private lateinit var mShortVideoViewPager2: ViewPager2
    private lateinit var mShortVideoListAdapterV2: ShortVideoListAdapterV2
    private var mCurrentPosition = -1
    private var mFirstPlay = true

    private val mPlayItemManager = PlayItemManager()


    companion object {
        private const val TAG = "ShortVideoActivityV2"
        private const val SHORT_VIDEO_PATH_PREFIX =
            "https://api-demo.qnsdk.com/v1/kodo/bucket/demo-videos?prefix=shortvideo"
    }

    private val mOnAttachStateChangeListener =
        object : View.OnAttachStateChangeListener {

            override fun onViewAttachedToWindow(v: View) {
                startPlay()
            }

            override fun onViewDetachedFromWindow(v: View) {
            }
        }


    private fun findItemViewByPosition(position: Int): RecyclerView.ViewHolder? {
        val recyclerView = mShortVideoViewPager2.getChildAt(0) as RecyclerView
        return recyclerView.findViewHolderForAdapterPosition(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setContentView(R.layout.activity_short_video2)
        mShortVideoViewPager2 = findViewById(R.id.short_video_VP2)
        mShortVideoViewPager2.offscreenPageLimit = 1
        Log.d(TAG, "mShortVideoViewPager2.offscreenPageLimit=${mShortVideoViewPager2.offscreenPageLimit}")

        mShortVideoViewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        mShortVideoListAdapterV2 = ShortVideoListAdapterV2(
            this,
            mPlayItemManager,
            this.getExternalFilesDir(null)?.path ?: ""
        )

        fetchVideoList()

    }

    fun initView() {

        mShortVideoViewPager2.adapter = mShortVideoListAdapterV2

        mShortVideoViewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                changeVideo(position)
            }
        })

        mShortVideoViewPager2.addOnAttachStateChangeListener(mOnAttachStateChangeListener)
    }

    private fun changeVideo(position: Int) {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return
        }

        val holder = findItemViewByPosition(position) as ShortVideoHolderV2?

        holder?.let {
            mShortVideoListAdapterV2.onPageSelected(
                position,
                it
            )
        }
        Log.d("INIT-STEP", "onPageSelected: position:$position ${holder?.playItemId}")
    }

    override fun onDestroy() {
        mShortVideoListAdapterV2.clear()

        super.onDestroy()
    }

    private fun fetchVideoList() {
        ModelFactory.createVideoItemListByURL(
            SHORT_VIDEO_PATH_PREFIX,
            object : ModelFactory.OnResultListener {
                override fun onSuccess(statusCode: Int, data: Any) {
                    val items = data as ArrayList<VideoItem>
                    val playItems = ArrayList<PlayItem>()
                    items.forEach { videoItem ->
                        videoItem.let {
                            val builder = QMediaModelBuilder()
                            Log.d(TAG, "video path=${it.videoPath}")
                            builder.addStreamElement(
                                "",
                                QURLType.QAUDIO_AND_VIDEO,
                                0,
                                MikuClientManager.getInstance().makeProxyURL(it.videoPath),
                                true,
                                "",
                                it.videoPath
                            )
                            val playItem = PlayItem(
                                it.videoPath.hashCode(),
                                builder.build(false),
                                it.coverPath
                            )

                            playItems.add(playItem)
                        }
                    }

                    mPlayItemManager.refresh(playItems)

                    runOnUiThread(Runnable {

                        initView()
                    })

                }

                override fun onFailure() {
                    runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ShortVideoActivityV2,
                            "获得短视频列表失败",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                }
            })
    }

    private fun startPlay() {
        if (mFirstPlay) {
            mFirstPlay = false
            changeVideo(0)
        }
    }
}