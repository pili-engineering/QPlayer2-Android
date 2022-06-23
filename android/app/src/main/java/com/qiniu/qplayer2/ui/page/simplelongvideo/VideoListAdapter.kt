package com.qiniu.qplayer2.ui.page.simplelongvideo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.qiniu.qmedia.component.player.QMediaModel
import com.qiniu.qplayer2.R

class VideoListAdapter : Adapter<VideoHolder>() {

    private var mVideoList: ArrayList<Pair<String, QMediaModel>>? = null
    private var mVideoHolderClickListener: IVideoHolderClickListener? = null


    fun setData(videoList: ArrayList<Pair<String, QMediaModel>>?) {
        mVideoList = videoList
    }

    fun setVideoHolderClickListener(videoHolderClickListener: IVideoHolderClickListener?) {
        mVideoHolderClickListener = videoHolderClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.holder_video_item, null, false
            ),
            mVideoHolderClickListener
        )
    }

    override fun getItemCount(): Int {
        return mVideoList?.size ?: 0
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        mVideoList?.get(position)?.also {
            holder.bind(it.first, it.second)
        }
    }
}