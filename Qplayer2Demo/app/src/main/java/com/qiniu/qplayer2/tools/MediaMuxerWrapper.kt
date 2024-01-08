package com.qiniu.qplayer2.tools

import android.media.MediaCodec
import android.media.MediaMuxer
import android.util.Log
import java.io.IOException
import java.nio.ByteBuffer

class MediaMuxerWrapper(
    private val mAudioEnable: Boolean,
    private val mVideoEnable: Boolean,
    private val mPath: String
) {
    companion object {
        const val INVALID_TRACK_INDEX = -1
        const val TAG = "MediaMuxerWrapper"
    }


    private var mIsVideoTrackAdded = false
    private var mIsAudioTrackAdded = false

    private var mMediaMuxer: MediaMuxer? = null
    private var mIsStart = false
    fun init(): Boolean {
        Log.d(TAG, "init")
        mIsStart = false
        try {
            mMediaMuxer = MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        } catch (e: IOException) {
            return false
        }
        return true
    }

    fun unint() {
        Log.d(TAG, "unint")
        if (mIsStart) {
            mIsStart = false
            mMediaMuxer?.stop()
            mMediaMuxer?.release()
        }

    }

    @Synchronized
    fun addTrackToMediaMuxer(mediaCodec: MediaCodec, type: MediaType): Int {
        if (type == MediaType.AUDIO && !mIsAudioTrackAdded) {
            mIsAudioTrackAdded = true
            Log.d(TAG, "addTrackToMediaMuxer audio")
            return mMediaMuxer?.addTrack(mediaCodec.outputFormat) ?: INVALID_TRACK_INDEX
        } else if (type == MediaType.VIDEO && !mIsVideoTrackAdded) {
            mIsVideoTrackAdded = true
            Log.d(TAG, "addTrackToMediaMuxer video")
            return mMediaMuxer?.addTrack(mediaCodec.outputFormat) ?: INVALID_TRACK_INDEX
        }
        return INVALID_TRACK_INDEX
    }

    fun writeSampleData(trackIndex: Int, byteBuf: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        Log.d(TAG, "writeSampleData index=$trackIndex")
        mMediaMuxer?.writeSampleData(trackIndex, byteBuf, bufferInfo)
    }

    @Synchronized
    fun startIfReady(): Boolean {
        val isReady = mAudioEnable == mIsAudioTrackAdded && mVideoEnable && mIsVideoTrackAdded
        if (isReady) {
            Log.d(TAG, "media muxer start ready")
            mIsStart = true
            mMediaMuxer?.start()
            return true
        } else {
            Log.d(TAG, "media muxer start but not ready")
        }
        return false
    }

    @Synchronized
    fun isStart(): Boolean {
        return mIsStart
    }
}