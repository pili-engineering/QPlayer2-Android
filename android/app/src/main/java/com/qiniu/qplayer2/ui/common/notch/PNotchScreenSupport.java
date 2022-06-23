package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 * https://developer.android.com/guide/topics/display-cutout
 */
final class PNotchScreenSupport extends DefaultNotchScreenSupport {

    private boolean mHardwareHasNotch;
    private boolean mAlreadyObtainHardwareNotch;
    private List<Rect> mHardwareNotchSize = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        ensureHardwareNotch(window);
        final View decorView = window.getDecorView();
        final WindowInsets windowInsets = decorView.getRootWindowInsets();
        if (windowInsets == null) return false;
        final DisplayCutout dct = windowInsets.getDisplayCutout();
        return dct != null && (dct.getSafeInsetTop() != 0
                || dct.getSafeInsetBottom() != 0
                || dct.getSafeInsetLeft() != 0
                || dct.getSafeInsetRight() != 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean hasNotchInScreenHardware(@NonNull Window window) {
        ensureHardwareNotch(window);
        return mHardwareHasNotch;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        ensureHardwareNotch(window);
        List<Rect> result = new ArrayList<>();
        final View decorView = window.getDecorView();
        final WindowInsets windowInsets = decorView.getRootWindowInsets();
        if (windowInsets == null) return result;
        DisplayCutout dct = windowInsets.getDisplayCutout();
        if (dct != null) {
            result.addAll(dct.getBoundingRects());
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public List<Rect> getNotchSizeHardware(@NonNull Window window) {
        ensureHardwareNotch(window);
        return mHardwareNotchSize;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
        ensureHardwareNotch(window);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(attributes);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
        ensureHardwareNotch(window);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        window.setAttributes(attributes);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void setWindowLayoutNotchDefault(@NonNull Window window) {
        ensureHardwareNotch(window);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        window.setAttributes(attributes);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private synchronized void ensureHardwareNotch(Window window) {
        if (mAlreadyObtainHardwareNotch) {
            return;
        }
        final View decorView = window.getDecorView();
        final WindowInsets windowInsets = decorView.getRootWindowInsets();
        if (windowInsets == null) {
            return;
        }
        mAlreadyObtainHardwareNotch = true;
        DisplayCutout dct = windowInsets.getDisplayCutout();
        mHardwareHasNotch = dct != null && (dct.getSafeInsetTop() != 0
                || dct.getSafeInsetBottom() != 0
                || dct.getSafeInsetLeft() != 0
                || dct.getSafeInsetRight() != 0);
        if (mHardwareHasNotch) {
            mHardwareNotchSize.addAll(dct.getBoundingRects());
        }
    }
}
