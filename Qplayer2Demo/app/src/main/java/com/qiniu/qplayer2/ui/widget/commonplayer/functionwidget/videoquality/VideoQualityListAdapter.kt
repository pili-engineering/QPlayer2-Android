package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.videoquality

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.QQuality
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.common.measure.DpUtils

class VideoQualityListAdapter (context: Context, private var mQualityList: List<QQuality>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater?

    private lateinit var mSelectedQuality: QQuality

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
        (holder as ViewHolder).bind(getItem(position), mSelectedQuality)
    }

    override fun getItemCount(): Int {
        return mQualityList.size

    }

    fun getItem(position: Int): QQuality? {
        return if (position >= 0 && position < mQualityList.size) {
            mQualityList[position]
        } else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setQualities(qualities: List<QQuality>) {
        mQualityList = qualities
    }

    fun setSelectedQuality(quality: QQuality) {
        mSelectedQuality = quality
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    class ViewHolder(itemView: View, itemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title_TV)
        var container: LinearLayout = itemView.findViewById(R.id.container_LL)

        private lateinit var mQuality: QQuality
        init {
            itemView.setOnClickListener { v ->
                itemClickListener?.onItemClick(mQuality)
            }
        }

        fun bind(quality: QQuality?, selectedQuality: QQuality) {
            if (quality == null) {
                return
            }
            mQuality = quality
            val maxWidth: Int = DpUtils.dpToPx(300)
            container.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            val selected =
                (selectedQuality.quality == mQuality.quality)
            itemView.isSelected = selected
            title.maxWidth = maxWidth
            title.text = getQualityDesc(mQuality)
        }

        fun getQualityDesc(quality: QQuality): String {
            return when(quality.quality) {
                1080-> "1080P"
                720-> "720P"
                480-> "480P"
                360->"360P"
                240->"240P"
                else -> ""
            }
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
        fun onItemClick(quality: QQuality)
    }
}
