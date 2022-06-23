package com.qiniu.qplayer2.ui.page.shortvideo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.ui.QSurfacePlayerView
import com.qiniu.qplayer2.R
import java.util.*

class ShortVideoListAdapter(
    private val mPlayItemList: ArrayList<PlayItem>,
    private val mVideoPlayerView: QSurfacePlayerView
) : RecyclerView.Adapter<ShortVideoHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortVideoHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView: View = inflater.inflate(R.layout.holder_short_video, parent, false)
        return ShortVideoHolder(contactView, mVideoPlayerView)
    }

    override fun onBindViewHolder(holder: ShortVideoHolder, position: Int) {
        if (mPlayItemList.size > position) {
            holder.bind(mPlayItemList[position], position)
        }
    }

    override fun getItemCount() = mPlayItemList.size
}