package com.qiniu.qplayer2.ui.common.notch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The ugly Huawei "Notch" screen
 *
 * @author yrom
 */
final class HwNotchScreenSupport extends DefaultNotchScreenSupport {
    /**
     * 华为刘海屏全屏显示FLAG
     */
    private static final int HW_FLAG_NOTCH_SUPPORT = 0x00010000;

    private static Class<?> hwNotchSizeUtil = null;
    private static Field hwLayoutParamsFlags = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        try {
            if (hwNotchSizeUtil == null) {
                ClassLoader cl = window.getContext().getClassLoader();  // get framework class' loader
                hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            }
            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(null);
        } catch (Exception ignored) {
            return false;
        }
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        List<Rect> result = new ArrayList<>();
        Rect rect = new Rect();
        try {
            Context context = window.getContext();
            if (hwNotchSizeUtil == null) {
                ClassLoader cl = context.getClassLoader();
                hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            }
            Method get = hwNotchSizeUtil.getMethod("getNotchSize");
            int[] ret = (int[]) get.invoke(null);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            rect.left = (metrics.widthPixels - ret[0]) / 2;
            rect.bottom = ret[1];
            rect.right = rect.left + ret[0];
            rect.top = 0;
            result.add(rect);
            return result;
        } catch (Exception ignored) {
            return result;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            if (hwLayoutParamsFlags == null) {
                hwLayoutParamsFlags = layoutParams.getClass().getDeclaredField("hwFlags");
                hwLayoutParamsFlags.setAccessible(true);
            }
            int old = (int) hwLayoutParamsFlags.get(layoutParams);
            hwLayoutParamsFlags.set(layoutParams, old | HW_FLAG_NOTCH_SUPPORT);
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            if (hwLayoutParamsFlags == null) {
                hwLayoutParamsFlags = layoutParams.getClass().getDeclaredField("hwFlags");
                hwLayoutParamsFlags.setAccessible(true);
            }
            int old = (int) hwLayoutParamsFlags.get(layoutParams);
            hwLayoutParamsFlags.set(layoutParams, old & ~HW_FLAG_NOTCH_SUPPORT);
        } catch (Exception ignored) {
        }
    }
}
