package com.qiniu.qplayer2.ui.page.shortvideoV2

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.qiniu.qmedia.component.player.QMediaModelBuilder
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.common.system.FileUtils
import com.qiniu.qplayer2.repository.shortvideo.ModelFactory
import com.qiniu.qplayer2.repository.shortvideo.VideoItem


class ShortVideoActivityV2 : AppCompatActivity(), IAllPlayerStateEndListener {

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
            this.getExternalFilesDir(null)?.path ?: "",
            this
        )
        Log.d(TAG, "onCreate")

        fetchVideoList()
        Log.d(TAG, "onCreate fetchVideoList")

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mShortVideoListAdapterV2.clear()
            }
        })


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState")

    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.d(TAG, "onCreate two params")

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun recreate() {
        super.recreate()
        Log.d(TAG, "recreate")

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

        super.onDestroy()
        Log.d(TAG, "onDestroy")

    }

    private fun fetchVideoList() {

        FileUtils.copyFromResToFile(this, R.raw.nike, "nike.mp4")?.let {
            val playItems = ArrayList<PlayItem>()
            var count = 10
            while (count> 0) {
                val builder = QMediaModelBuilder()
                Log.d(TAG, "video path=${it}")
                builder.addStreamElement(
                    "",
                    QURLType.QAUDIO_AND_VIDEO,
                    0,
                    it,
                    true,
                    "",
                    it
                )
                count -= 1

                val playItem = PlayItem(
                    it.hashCode(),
                    builder.build(false),
                    this.resources.getDrawable(R.drawable.nike)

                )

                playItems.add(playItem)
            }

            mPlayItemManager.refresh(playItems)
            initView()

        }

    }

    private fun startPlay() {
        if (mFirstPlay) {
            mFirstPlay = false
            changeVideo(0)
        }
    }

    override fun onAllPlayerStateEnd() {
        Log.d(TAG, "onAllPlayerStateEnd")
        finish()
    }
}