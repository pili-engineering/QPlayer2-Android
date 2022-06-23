package com.qiniu.qplayer2.ui.page.simplelongvideo

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiniu.qmedia.component.player.QMediaModel
import com.qiniu.qplayer2.R

class VideoHolder : RecyclerView.ViewHolder {


    private var mMediaModel: QMediaModel? = null
    private var mName = ""
    private lateinit var mNameTV: TextView;
    private var mClickListener: IVideoHolderClickListener? = null


    constructor(itemView: View, clickListener: IVideoHolderClickListener?):super(itemView) {
        mClickListener = clickListener;
        mNameTV = itemView.findViewById(R.id.name)
        mNameTV.setOnClickListener {
            mClickListener?.onClick(mMediaModel)
        }
    }

    fun bind(name: String, media_model: QMediaModel) {
        mName = name
        mMediaModel = media_model


        mNameTV.text = name

    }
}