package com.qiniu.qplayer2.ui.page.shortvideoV2

class PlayItemManager() {

    private var mPlayItemList = ArrayList<PlayItem>()

    interface IPlayItemListRefreshListener {
        fun onRefresh(itemList: List<PlayItem>)
    }

    interface IPlayItemAppendListener {
        fun onAppend(appendItems: List<PlayItem>)
    }

    interface IPlayItemDeleteListener {
        fun onDelete(position: Int, deletePlayItem: PlayItem)
    }

    interface IPlayItemReplaceListener {
        fun onReplace(position: Int, oldPlayItem: PlayItem, newPlayItem: PlayItem)
    }

    private val mPlayItemAppendListeners = ArrayList<IPlayItemAppendListener>()

    private val mPlayItemDeleteListeners = ArrayList<IPlayItemDeleteListener>()

    private val mPlayItemReplaceListeners = ArrayList<IPlayItemReplaceListener>()

    private val mPlayItemRefreshListeners = ArrayList<IPlayItemListRefreshListener>()

    fun refresh(playItemList: ArrayList<PlayItem>): Boolean {
        mPlayItemList = playItemList
        mPlayItemRefreshListeners.forEach {
            it.onRefresh(playItemList)
        }
        return true
    }

    fun append(playItems: List<PlayItem>): Boolean {
        mPlayItemList.addAll(playItems)
        mPlayItemAppendListeners.forEach {
            it.onAppend(playItems)
        }
        return true
    }

    fun delete(position: Int): Boolean {
        return if (position >= 0 && position < mPlayItemList.size) {
            val deletePlayItem = mPlayItemList[position]
            mPlayItemList.removeAt(position)
            mPlayItemDeleteListeners.forEach {
                it.onDelete(position, deletePlayItem)
            }
            true
        } else {
            false
        }
    }

    fun replace(position: Int, playItem: PlayItem): Boolean {
        return if (position >= 0 && position < mPlayItemList.size) {
            val replacePlayItem = mPlayItemList[position]
            mPlayItemList.removeAt(position)
            mPlayItemReplaceListeners.forEach {
                it.onReplace(position, replacePlayItem, playItem)
            }
            true
        } else {
            false
        }
    }

    fun getOrNullByPosition(position: Int): PlayItem? {

        return if (position >= 0 && position < mPlayItemList.size) {
            mPlayItemList[position]
        } else {
            null
        }
    }

    fun getOrNullById(id: Int): PlayItem? {
        return mPlayItemList.find { id == it.id }
    }

    fun count() : Int {
        return mPlayItemList.size
    }

    fun addPlayItemAppendListener(listener: IPlayItemAppendListener) {
        if (mPlayItemAppendListeners.contains(listener))
            mPlayItemAppendListeners.add(listener)
    }

    fun removePlayItemAppendListener(listener: IPlayItemAppendListener) {
        mPlayItemAppendListeners.remove(listener)
    }

    fun addPlayItemDeleteListener(listener: IPlayItemDeleteListener) {
        if (mPlayItemDeleteListeners.contains(listener))
            mPlayItemDeleteListeners.add(listener)
    }

    fun removePlayItemDeleteListener(listener: IPlayItemDeleteListener) {
        mPlayItemDeleteListeners.remove(listener)

    }

    fun addPlayItemReplaceListener(listener: IPlayItemReplaceListener) {
        if (mPlayItemReplaceListeners.contains(listener))
            mPlayItemReplaceListeners.add(listener)
    }

    fun removePlayItemReplaceListener(listener: IPlayItemReplaceListener) {
        mPlayItemReplaceListeners.remove(listener)

    }

    fun addPlayItemListRefreshListener(listener: IPlayItemListRefreshListener) {
        if (mPlayItemRefreshListeners.contains(listener))
            mPlayItemRefreshListeners.add(listener)
    }

    fun removePlayItemRefreshListener(listener: IPlayItemListRefreshListener) {
        mPlayItemRefreshListeners.remove(listener)

    }
}