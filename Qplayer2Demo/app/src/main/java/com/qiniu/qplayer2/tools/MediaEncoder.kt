package com.qiniu.qplayer2.tools

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MediaEncoder<T:BaseFrame>(
    private val mFormat: MediaFormat,
    private val mEncoderType: String,
    private val mMediaType: MediaType,
    private val mFrameQueue: LinkedBlockingQueue<T>,
    private val mMuxerWrapper:MediaMuxerWrapper) {


    companion object {
        private const val WAIT_TIME = 100L
        const val TAG = "MediaEncoder"
    }

    private var mRecordInputThread: Thread? = null
    private var mIsRecordInputThreadStop = AtomicBoolean(true)

    private var mRecordOutputThread: Thread? = null
    private var mIsRecordOutputThreadStop = AtomicBoolean(true)

    private var mMediaCodec: MediaCodec? = null

     private fun createMediaCodec(): Boolean{
        try {
            mMediaCodec = MediaCodec.createEncoderByType(mEncoderType)
            mMediaCodec?.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mMediaCodec?.start()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    fun start():Boolean {
        if (!createMediaCodec()) {
            return false
        }
        startCodecInputThread()
        startCodecOutputThread()

        return true
    }

    fun stop() {
        stopCodecInputThread()
        putEndOfStreamFlag()
        stopCodecOutputThread()
        destroyMediaCodec()
    }

    private fun putEndOfStreamFlag() {
        putDataIntoMediaCodec(ByteArray(0), 0)

    }

    private fun destroyMediaCodec(){
        mMediaCodec?.stop()
        mMediaCodec?.release()
    }

    private fun startCodecInputThread() {
        mIsRecordInputThreadStop.set(false)
        mRecordInputThread = Thread {
            while (!mIsRecordInputThreadStop.get()) {
                mFrameQueue.poll(WAIT_TIME, TimeUnit.MILLISECONDS)?.also { frame ->
                    Log.d(TAG, "poll frame type=${mMediaType} size=${mFrameQueue.size}")
                    putDataIntoMediaCodec(frame.data, frame.presentationTimeUs)
                }
            }
        }
        mRecordInputThread?.start()
    }

    private fun stopCodecInputThread() {
        mIsRecordInputThreadStop.set(true)
        mRecordInputThread?.join()
    }

    private fun startCodecOutputThread() {
        mIsRecordOutputThreadStop.set(false)
        mRecordOutputThread = Thread {
            var isThreadError = false
            val bufferInfo = MediaCodec.BufferInfo()
            var trackIndex = MediaMuxerWrapper.INVALID_TRACK_INDEX
            while (!mIsRecordOutputThreadStop.get()) {

                mMediaCodec?.also { mediaCodec ->
                    //获得输出
                    val encoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo,
                        WAIT_TIME
                    )
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                        // this status indicate the output format of codec is changed
                        // this should come only once before actual encoded data
                        // but this status never come on Android4.3 or less
                        // and in that case, you should treat when MediaCodec.BUFFER_FLAG_CODEC_CONFIG come.
                        trackIndex = mMuxerWrapper.addTrackToMediaMuxer(mediaCodec, mMediaType)
                        mMuxerWrapper.startIfReady()
                        if (trackIndex == MediaMuxerWrapper.INVALID_TRACK_INDEX) {
                            //format change twitce or add track failed
                            isThreadError = true
                            return@Thread
                        }
                    } else if (encoderStatus < 0){

                    } else {
                        val encodeBuffer = mediaCodec.outputBuffers[encoderStatus]
                        if (encodeBuffer == null) {
                            //this never should come...may be a MediaCodec internal error
                            isThreadError = true
                            // return buffer to encoder
                            mediaCodec.releaseOutputBuffer(encoderStatus, false)
                            return@Thread
                        }else {
                            if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                                // You shoud set output format to muxer here when you target Android4.3 or less
                                // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
                                // therefor we should expand and prepare output format from buffer data.
                                // This sample is for API>=18(>=Android 4.3), just ignore this flag here
                                bufferInfo.size = 0
                            }

                            if (trackIndex == MediaMuxerWrapper.INVALID_TRACK_INDEX) {
                                isThreadError = true
                                mediaCodec.releaseOutputBuffer(encoderStatus, false)
                                return@Thread
                            }

                            if (bufferInfo.size != 0) {
                                while (!mMuxerWrapper.isStart()) {
                                    if (mIsRecordOutputThreadStop.get()) {
                                        break
                                    } else {
                                        Thread.sleep(200)
                                    }
                                }
                                mMuxerWrapper.writeSampleData(trackIndex, encodeBuffer, bufferInfo)
                            }
                            mediaCodec.releaseOutputBuffer(encoderStatus, false)

                        }
                    }
                }
            }
            if (isThreadError) {
                stop()
            }
        }

        mRecordOutputThread?.start()
    }

    private fun stopCodecOutputThread() {
        mIsRecordOutputThreadStop.set(true)
        mRecordOutputThread?.join()
    }

    private fun putDataIntoMediaCodec(data: ByteArray, presentationTimeUs: Long) {
        mMediaCodec?.let {
            var putSize = 0
            val dataSize = data.size
            while (putSize < dataSize || dataSize == 0) {
                val index = it.dequeueInputBuffer(WAIT_TIME)
                Log.d(TAG, "dequeueInputBuffer index=$index percent=$putSize/$dataSize")
                if (index >= 0) {
                    it.inputBuffers[index]?.let { inputBuffer ->
                        inputBuffer.clear()
                        if (dataSize == 0) {
                            it.queueInputBuffer(
                                index,
                                0,
                                0,
                                presentationTimeUs,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            return
                        } else {
                            val remaining = inputBuffer.remaining()
                            val currentPutSize: Int
                            if (remaining >= (dataSize - putSize)) {
                                currentPutSize = dataSize - putSize
                                inputBuffer.put(data, putSize, currentPutSize)
                            } else {
                                currentPutSize = remaining
                                inputBuffer.put(data, putSize, remaining)
                            }
                            putSize += currentPutSize
                            it.queueInputBuffer(
                                index,
                                0,
                                currentPutSize,
                                presentationTimeUs,
                                0
                            )
                        }

                    }
                } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER){

                }
            }
        }
    }
}