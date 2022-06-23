package com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.speed

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qplayer2.R
import kotlin.math.abs

class SpeedListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var mItemListener: OnItemSelectListener? = null
    private var mItemList = mutableListOf<SpeedItem>()
    private val PLAYBACK_SPEED = floatArrayOf(2.00f, 1.50f, 1.25f, 1.00f, 0.75f, 0.50f)

    fun setData(currentSpeed: Float) {
        mItemList.clear()

        for (i in PLAYBACK_SPEED.indices) {
            val speed = PLAYBACK_SPEED[i]
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && speed >= 1.99f) {
                continue
            }

            val qualityItem = SpeedItem()
            qualityItem.speed = speed
            qualityItem.isSelected = abs(currentSpeed - speed) < 0.1
            mItemList.add(qualityItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return SpeedItemHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val speedItem = mItemList[position]

        holder.itemView.tag = speedItem
        holder.itemView.setOnClickListener(this)

        when (holder) {
            is SpeedItemHolder -> holder.bind(speedItem)
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_NORMAL
    }

    override fun onClick(v: View) {
        mItemListener ?: return
        if (v.tag !is SpeedItem) return

        val speedItem = v.tag as SpeedItem
        val pos = mItemList.indexOf(speedItem)

        if (!speedItem.isSelected) {
            var speed = speedItem.speed
            mItemListener?.onItemSelected(speed)
            mItemList.forEachIndexed { index, item ->
                item.isSelected = index == pos
            }
            notifyDataSetChanged()
        }
    }

    fun setItemSelectListener(l: OnItemSelectListener) {
        mItemListener = l
    }

    class SpeedItemHolder private constructor(private val mTextView: TextView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mTextView) {

        @SuppressLint("SetTextI18n")
        fun bind(speedItem: SpeedItem?) {
            speedItem ?: return
            mTextView.text = speedItem.speed.toString() + "X"
            mTextView.isSelected = speedItem.isSelected
        }

        companion object {
            fun create(parent: ViewGroup): SpeedItemHolder {
                val textView = LayoutInflater.from(parent.context).inflate(R.layout.holder_common_player_list_item, parent, false) as TextView
                return SpeedItemHolder(textView)
            }
        }
    }

    class SpeedItem {
        var speed: Float = 1.0f
        var isSelected: Boolean = false
    }

    interface OnItemSelectListener {
        fun onItemSelected(speed: Float)
    }

    companion object {
        const val TYPE_NORMAL = 0
    }
}