
package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videolist

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2ext.common.measure.DpUtils

class VideoListAdapter(context: Context, private var mVideoParamsList: List<LongVideoParams>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater?

    private var mSelectedVideoId = -1L

    private var mItemClickListener: OnItemClickListener? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (mLayoutInflater == null) {
            throw AssertionError("LayoutInflater not found.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.createHolder(mLayoutInflater!!, mItemClickListener, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), mSelectedVideoId)
    }

    override fun getItemCount(): Int {
        return mVideoParamsList.size

    }

    fun getItem(position: Int): LongVideoParams? {
        return if (position >= 0 && position < mVideoParamsList.size) {
            mVideoParamsList[position]
        } else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setItems(paramsList: List<LongVideoParams>) {
        mVideoParamsList = paramsList
    }

    fun setSelectedVideoId(id: Long) {
        mSelectedVideoId = id
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    class ViewHolder(itemView: View, itemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title_TV)
        var container: LinearLayout = itemView.findViewById(R.id.container_LL)

        private var mVideoId: Long = -1
        init {
            itemView.setOnClickListener { v ->
                itemClickListener?.onItemClick(mVideoId)
            }
        }

        fun bind(item: LongVideoParams?, selectedVideoId: Long) {
            if (item == null) {
                return
            }
            mVideoId = item.id
            val maxWidth: Int = DpUtils.dpToPx(300)
            container.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            val selected = selectedVideoId == mVideoId
            itemView.isSelected = selected
            title.maxWidth = maxWidth
            title.text = item.title
        }

        companion object {

            internal fun createHolder(
                inflater: LayoutInflater,
                itemClickListener: OnItemClickListener?,
                parent: ViewGroup
            ): ViewHolder {
                return ViewHolder(inflater.inflate(R.layout.holder_common_player_video_list_item, parent, false), itemClickListener)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(id: Long)
    }
}
