package com.qiniu.qplayer2.ui.page.simplelongvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.*
import com.qiniu.qmedia.ui.QTexturePlayerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.logic.PlayerSettingVM
import com.qiniu.qplayer2.ui.widget.*

class SimpleLongVideoActivity : AppCompatActivity(), IVideoHolderClickListener {

    companion object {
        private final const val TAG = "LongVideoActivity"
    }

    lateinit var mQTexturePlayerView: QTexturePlayerView
    lateinit var mPausePlayWidget: PlayerPausePlayWidget
    lateinit var mProgressTextWidget: PlayerProgressTextWidget
    lateinit var mSeekWidget: PlayerSeekWidget
    lateinit var mFPSWidget: PlayerFPSTextWidget
    lateinit var mDownloadTextWidget: PlayerDownloadTextWidget
    lateinit var mBiteRateTextWidget: PlayerBiteRateTextWidget

    lateinit var mVideoListRecycleView: RecyclerView
    lateinit var mBufferingWidget: PlayerBufferingWidget

    private val mVideoList = ArrayList<Pair<String, QMediaModel>>()

    private lateinit var mSettingVM: PlayerSettingVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();//隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_simple_long_video)
        mQTexturePlayerView = findViewById(R.id.player_view)
        mPausePlayWidget = findViewById(R.id.pause_play_Btn)
        mProgressTextWidget = findViewById(R.id.progress_TV)
        mBufferingWidget = findViewById(R.id.buffering_TV)
        mSeekWidget = findViewById(R.id.seek_bar)
        mFPSWidget = findViewById(R.id.fps_TV)
        mBiteRateTextWidget = findViewById(R.id.biterate_TV)
        mDownloadTextWidget = findViewById(R.id.download_speed_TV)

        val videoListAdapter = VideoListAdapter()
        videoListAdapter.setVideoHolderClickListener(this)
        initMediaModel();
        videoListAdapter.setData(mVideoList)

        mVideoListRecycleView = findViewById(R.id.video_list_RECYCLER_VIEW)
        mVideoListRecycleView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        mVideoListRecycleView.adapter = videoListAdapter
        mPausePlayWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mProgressTextWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mSeekWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mFPSWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mBiteRateTextWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mDownloadTextWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mBufferingWidget.setPlayerControlHandler(mQTexturePlayerView.playerControlHandler)
        mSettingVM = PlayerSettingVM(this.lifecycle)

        mQTexturePlayerView.playerControlHandler.init(this)
        mQTexturePlayerView.playerControlHandler.setDecodeType(
            mSettingVM.decoderTypeLiveData.value?: QPlayerSetting.QPlayerDecoder.QPLAYER_DECODER_SETTING_AUTO)

        mQTexturePlayerView.playerControlHandler.setSeekMode(
            mSettingVM.seekTypeLiveData.value?: QPlayerSetting.QPlayerSeek.QPLAYER_SEEK_SETTING_NORMAL)

        mQTexturePlayerView.playerControlHandler.setStartAction(
            mSettingVM.startTypeLiveData.value?: QPlayerSetting.QPlayerStart.QPLAYER_START_SETTING_PLAYING)

        mQTexturePlayerView.playerControlHandler.setSpeed(
            mSettingVM.speedLiveData.value ?: 1.0f
        )

        mQTexturePlayerView.playerRenderHandler.setRenderRatio(
            mSettingVM.renderRatioLiveData.value?: QPlayerSetting.QPlayerRenderRatio.QPLAYER_RATIO_SETTING_AUTO
        )
        mVideoList[0].also {
            mQTexturePlayerView.playerControlHandler.playMediaModel(it.second, mSettingVM.startPositionLiveData.value ?: 0)
//            mQSurfacePlayerView.playerControlHandler.setSpeed(2.0f)
        }
    }

    private fun initMediaModel() {
        var builder = QMediaModelBuilder()
        var url = ""

        //音视频分开2个流的视频要用精准seek
        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/only-video-1080p-60fps.m4s"
        builder.addElement("", QURLType.QVIDEO, 0, url, true)
        url = "http://demo-videos.qnsdk.com/only-audio.m4s";
        builder.addElement("", QURLType.QAUDIO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/movies/qiniu.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/shortvideo/nike.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))



        builder = QMediaModelBuilder()
        url = "http://pili-hdl.qnsdk.com/sdk-live/timestamp-6M.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(true)))

        builder = QMediaModelBuilder()
        url = "http://pili-hls.qnsdk.com/sdk-live/timestamp-6M.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(true)))

        builder = QMediaModelBuilder()
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/timestamp"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(true)))


        builder = QMediaModelBuilder()
        url = "https://ms-shortvideo-dn.eebbk.net/bbk-n002/stream/2021/07/30/1911/68/357c1b03fa454a9c6b3198c9c6a3b49a.mp4?sign=13eb41043c62c8f462dcb6226fd1a5a6&t=613852bf"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true, "http://video.eebbk.net")
        mVideoList.add(Pair("http403", builder.build(false)))
        builder = QMediaModelBuilder()
        url = "https://ms-shortvideo-dn.eebbk.net/bbk-n002/stream/2021/12/27/1143/14571/e3d3dc6b29afcccb6ff01c8f23e4018b.mp4?sign=6a1d95afe884f9654a6598b076affd00&t=61dd5fa6"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true, "http://video.eebbk.net")
        mVideoList.add(Pair("http referer", builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/song.mp3"
        builder.addElement("", QURLType.QAUDIO, 0, url, true)
        mVideoList.add(Pair("纯音频", builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/only-video-1080p-60fps.m4s"
        builder.addElement("", QURLType.QVIDEO, 0, url, true)
        mVideoList.add(Pair("纯视频", builder.build(false)))



        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/bbk-bt709.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/bbk-H265-50fps.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/Sync-Footage-V1-H264.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair("音画同步-H264-60FPS", builder.build(false)))

        builder = QMediaModelBuilder()
        url = "https://img.qunliao.info:443/4oEGX68t_9505974551.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
        mVideoList.add(Pair(url, builder.build(false)))

//        builder = QMediaModelBuilder()
//        url = "https://media.w3.org/2010/05/sintel/trailer.mp4"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
//        mVideoList.add(Pair(url, builder.build()))
//
//        builder = QMediaModelBuilder()
//        url = "http://demo-videos.qnsdk.com/Sony%20Swordsmith%20HDR%20UHD%204K%20Demo.mp4"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
//        mVideoList.add(Pair("4K-HDR-yuv420p10le(tv, bt2020nc/bt2020/smpte2084)", builder.build()))
    }


    override fun onDestroy() {
        mQTexturePlayerView.playerControlHandler.release()
        super.onDestroy()

    }

    override fun onClick(mediaModel: QMediaModel?) {
        mediaModel?.also {

            mQTexturePlayerView.playerControlHandler.playMediaModel(it, mSettingVM.startPositionLiveData.value ?: 0)
//            mBufferingTV.visibility = View.VISIBLE
        }
    }
}