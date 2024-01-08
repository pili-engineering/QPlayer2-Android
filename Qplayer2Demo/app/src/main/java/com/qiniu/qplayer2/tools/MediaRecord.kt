package com.qiniu.qplayer2.tools

import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import com.qiniu.qmedia.component.player.QIPlayerAudioDataListener
import com.qiniu.qmedia.component.player.QIPlayerVideoDataListener
import java.util.concurrent.LinkedBlockingQueue


class MediaRecord(private val mExternalFilesDir: String) {


    companion object {
        const val TAG = "MediaRecord"
        private const val BPP = 0.25f

    }
    private var mAudioEncoder: MediaEncoder<AudioFrame>? = null
    private var mVideoEncoder: MediaEncoder<VideoFrame>? = null
    private var mMuxerWrapper: MediaMuxerWrapper? = null


    private var mStartTime: Long = 0

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var mVideoDataType = QIPlayerVideoDataListener.QVideoDataType.NONE

    private var mAudioSampleRate: Int = 0
    private var mAudioChannelCount: Int = 0
    private var mAudioChannelLayout = QIPlayerAudioDataListener.QChannelLayout.NONE
    private var mAudioFormat = QIPlayerAudioDataListener.QSampleFormat.NONE


    private var mIsStartAudioRecord: Boolean = false
    private var mIsStartVideoRecord: Boolean = false

    private val mAudioFrameQueue = LinkedBlockingQueue<AudioFrame>()
    private val mVideoFrameQueue = LinkedBlockingQueue<VideoFrame>()

    private var mIsStarted = false
    private var mPath = ""

    private fun getPTSUs(): Long {
        val result = (System.nanoTime() - mStartTime) / 1000L
        Log.d(TAG, "pts=$result")
        return result
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
//        if (result < prevOutputPTSUs) result = prevOutputPTSUs - result + result
    }

    fun start(
        videoEnable: Boolean,
        audioEnable: Boolean
    ):Boolean {

        mIsStarted = true
        mStartTime = System.nanoTime()
        mPath = mExternalFilesDir + System.currentTimeMillis().toString() + ".mp4"
        mMuxerWrapper = MediaMuxerWrapper(audioEnable, videoEnable, mPath)
        if (mMuxerWrapper?.init() == true) {
            return true
        }
        mPath = ""
        return false

    }

    fun stop(): String {
        mIsStarted = false
        stopAudioRecord()
        stopVideoRecord()
        mMuxerWrapper?.unint()
        mMuxerWrapper = null
        mAudioFrameQueue.clear()
        mVideoFrameQueue.clear()
        return mPath
    }

    fun putVideoData(
        width: Int,
        height: Int,
        type: QIPlayerVideoDataListener.QVideoDataType,
        data: ByteArray
    ): Boolean {

        if (!mIsStarted) {
            return false
        }

        if (!mIsStartVideoRecord) {
            mVideoWidth = width
            mVideoHeight = height
            mVideoDataType = type

            startVideoRecord()
            mIsStartVideoRecord = true
        } else if (isVideoFormatChanged(width, height, type)) {
            stop()
            return false
        }

        mVideoFrameQueue.add(
            VideoFrame(
                width, height,
                type, data, getPTSUs()
            )
        )
        Log.d(TAG, "putVideoData add video frame size=${mVideoFrameQueue.size}")
        return true
    }


    fun putAudioData(
        sampleRate: Int,
        channelCount: Int,
        format: QIPlayerAudioDataListener.QSampleFormat,
        channelLayout: QIPlayerAudioDataListener.QChannelLayout,
        data: ByteArray
    ): Boolean {

        if (!mIsStarted) {
            return false
        }

        if (!mIsStartAudioRecord) {
            mAudioSampleRate = sampleRate
            mAudioFormat = format
            mAudioChannelCount = channelCount
            mAudioChannelLayout = channelLayout

            startAudioRecord()
            mIsStartAudioRecord = true
        } else if (isAudioFormatChanged(sampleRate, channelCount, format, channelLayout)) {
            stop()
            return false
        }
        mAudioFrameQueue.add(
            AudioFrame(
                sampleRate, channelCount,
                format, channelLayout, data, getPTSUs()
            )
        )
        Log.d(TAG, "putAudioData add audio frame size=${mAudioFrameQueue.size}")

        return true
    }

    private fun isAudioFormatChanged(
        sampleRate: Int,
        channelCount: Int,
        format: QIPlayerAudioDataListener.QSampleFormat,
        channelLayout: QIPlayerAudioDataListener.QChannelLayout
    ): Boolean {
        return !(mAudioSampleRate == sampleRate &&
                mAudioChannelCount == channelCount &&
                mAudioFormat == format &&
                mAudioChannelLayout == channelLayout)

    }


    private fun isVideoFormatChanged(
        width: Int,
        height: Int,
        type: QIPlayerVideoDataListener.QVideoDataType
    ): Boolean {
        return !(mVideoWidth == width &&
                mVideoHeight == height &&
                mVideoDataType == type)

    }


    private fun startVideoRecord() {

        val format =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mVideoWidth, mVideoHeight)
        //设置色彩
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )
        //设置码率，码率就是数据传输单位时间传递的数据位数
        val frameRate = 25
        val IFrameInterval = 10
        val maxInputSize = 4000000
        val bitRate = (BPP * frameRate * mVideoWidth * mVideoHeight).toInt()

        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        //设置帧率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        //设置关键帧间隔
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFrameInterval)
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxInputSize)

        mVideoEncoder = MediaEncoder<VideoFrame>(
            format,
            MediaFormat.MIMETYPE_VIDEO_AVC,
            MediaType.VIDEO,
            mVideoFrameQueue,
            mMuxerWrapper!!
        )
        mVideoEncoder?.start()

    }

    private fun stopVideoRecord() {
        mVideoEncoder?.stop()
        mVideoEncoder = null
    }

    private fun startAudioRecord() {

        val format = MediaFormat.createAudioFormat(
            MediaFormat.MIMETYPE_AUDIO_AAC,
            mAudioSampleRate,
            mAudioChannelCount
        )
        format.setInteger(
            MediaFormat.KEY_AAC_PROFILE,
            MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 8192)


        mAudioEncoder = MediaEncoder<AudioFrame>(
            format,
            MediaFormat.MIMETYPE_AUDIO_AAC,
            MediaType.AUDIO,
            mAudioFrameQueue, mMuxerWrapper!!)
        mAudioEncoder?.start()
    }

    private fun stopAudioRecord() {
        mAudioEncoder?.stop()
        mAudioEncoder = null
    }


}