package com.qiniu.qplayer2.ui.page.longvideo

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.qiniu.qplayer2.common.system.RomUtils
import com.qiniu.qplayer2ext.commonplayer.screen.ICommonPlayerScreenChangedListener
import com.qiniu.qplayer2ext.commonplayer.screen.ScreenType
import com.qiniu.qplayer2.ui.common.notch.NotchCompat
import com.qiniu.qplayer2ext.common.measure.DpUtils

class LongCommonPlayerScreenChangedListener(
    private val mActivity: ComponentActivity,
    private val mVideoContainer: ViewGroup
) : ICommonPlayerScreenChangedListener {
    override fun onScreenTypeChanged(screenType: ScreenType) {

        if (screenType == ScreenType.HALF_SCREEN) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                mActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                mActivity.window.insetsController?.show(WindowInsetsCompat.Type.statusBars())
            }
            mVideoContainer.layoutParams.height = DpUtils.dpToPx(200)
            mVideoContainer.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            fitSystemWindow(false)
            NotchCompat.blockDisplayCutout(mActivity.window)
            mVideoContainer.requestLayout()
            restoreVideoContainer()
            if (NotchCompat.hasDisplayCutout(mActivity.window)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P && !RomUtils.isSamsungRom()) { //samsuang with cutout device excluded
                    setStatusBarColor(Color.BLACK)
//                  tintFakeStatusBar(ContextCompat.getColor(mActivity, android.R.color.black))
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                mActivity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            } else {
                mActivity.window.insetsController?.hide(WindowInsetsCompat.Type.statusBars())
            }
            mVideoContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            mVideoContainer.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            fitSystemWindow(false)
            NotchCompat.immersiveDisplayCutout(mActivity.window)
            mVideoContainer.requestLayout()

            changeVideoContainerToTop()

            if (NotchCompat.hasDisplayCutout(mActivity.window) && !RomUtils.isSamsungRom()) {
                setStatusBarColor(Color.TRANSPARENT)
//                tintFakeStatusBar(ContextCompat.getColor(mActivity, android.R.color.transparent))
            }
        }
    }

    private fun changeVideoContainerToTop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setElevation(mVideoContainer, 100f)
        } else {
            mVideoContainer.bringToFront()
        }
    }

    private fun restoreVideoContainer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setElevation(mVideoContainer, 0f)
        } else {
            val parent = mVideoContainer.parent
            if (parent is ViewGroup) {
                val mOldVideoPageIndex = 0
                if (parent.indexOfChild(mVideoContainer) != mOldVideoPageIndex) {
                    parent.removeView(mVideoContainer)
                    parent.addView(mVideoContainer, mOldVideoPageIndex)
                }
            }
        }
    }

    //    private fun tintFakeStatusBar(@ColorInt color: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val window = mActivity.window
//            val decorView = window.decorView as ViewGroup
//            var statusBarView: View? = mActivity.findViewById(R.id.bili_status_bar_view)
//            if (statusBarView == null) {
//                statusBarView = View(mActivity)
//                statusBarView.id = R.id.bili_status_bar_view
//                val lp = ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, StatusBarCompat.getStatusBarHeight(mActivity))
//                decorView.addView(statusBarView, lp)
//            }
//
//            statusBarView.setBackgroundColor(color)
//            statusBarView.visibility = View.VISIBLE
//        }
//    }
    private fun setStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.window.statusBarColor = color
        }
    }

    private fun fitSystemWindow(landscape: Boolean) {
        var rootView: View? = mVideoContainer
        while (rootView != null && rootView.id != android.R.id.content) {
            if (rootView is ViewGroup) {
                rootView.clipToPadding = !landscape
                rootView.clipChildren = !landscape
            }
            rootView = rootView.parent as View
        }
    }
}