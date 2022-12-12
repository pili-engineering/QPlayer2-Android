package com.qiniu.qplayer2.common.system

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.PackageManagerCompat

object PermissionHelper {
    fun checkPhotoAlbumPermission(activity: Activity): Boolean {

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var result = ActivityCompat.checkSelfPermission(activity, permissions[0])
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 0)
            return false
        }
        return true
    }
}