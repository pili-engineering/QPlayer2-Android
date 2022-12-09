package com.qiniu.qplayer2.ui.page.longvideo

import com.qiniu.qplayer2.common.system.PermissionHelper
import com.qiniu.qplayer2ext.commonplayer.ILogicProvider

class LongLogicProvider(private val mActivity: LongVideoActivity): ILogicProvider {
    fun checkPhotoAlbumPermission(): Boolean {
        return PermissionHelper.checkPhotoAlbumPermission(mActivity)
    }
}