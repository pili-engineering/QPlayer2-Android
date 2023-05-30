package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.subtitle

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.common.measure.DpUtils

class SubtitleListAdapter(context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mLayoutInflater: LayoutInflater?
    private val mSubtitleList = ArrayList<String>()
    private lateinit var mSelectedSubtitle: String

    private var mItemClickListener: OnItemClickListener? = null


    init {
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (mLayoutInflater == null) {
            throw AssertionError("LayoutInflater not found.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.createHolder(mLayoutInflater!!, mItemClickListener, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), mSelectedSubtitle)
    }

    override fun getItemCount(): Int {
        return mSubtitleList.size

    }

    fun getItem(position: Int): String? {
        return if (position >= 0 && position < mSubtitleList.size) {
            mSubtitleList[position]
        } else null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setSubtitles(subtitles: List<String>) {
        mSubtitleList.clear()
        mSubtitleList.add("关闭")
        mSubtitleList.addAll(subtitles)
    }

    fun setSelectedSubtitle(name: String) {
        mSelectedSubtitle = name
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    class ViewHolder(itemView: View, itemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title_TV)
        var container: LinearLayout = itemView.findViewById(R.id.container_LL)

        private lateinit var mSubtitleName: String

        init {
            itemView.setOnClickListener { v ->
                itemClickListener?.onItemClick(mSubtitleName)
            }
        }

        fun bind(subtitleName: String?, selectedSubtitleName: String) {
            if (subtitleName == null) {
                return
            }
            mSubtitleName = subtitleName
            val maxWidth: Int = DpUtils.dpToPx(300)
            container.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            val selected =
                (selectedSubtitleName == subtitleName)
            itemView.isSelected = selected
            title.maxWidth = maxWidth
            title.text = mSubtitleName
        }

        companion object {

            internal fun createHolder(
                inflater: LayoutInflater,
                itemClickListener: OnItemClickListener?,
                parent: ViewGroup
            ): ViewHolder {
                return ViewHolder(
                    inflater.inflate(
                        R.layout.holder_common_player_video_list_item,
                        parent,
                        false
                    ), itemClickListener
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(subtitleName: String)
    }
}