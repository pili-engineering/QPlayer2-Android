package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feifan on 2018/5/15.
 * Contacts me:404619986@qq.com
 * https://dev.vivo.com.cn/documentCenter/doc/103
 */
final class VivoNotchScreenSupport extends DefaultNotchScreenSupport {
    private static Class<?> vivoFtFeature = null;
    //表示是否有凹槽
    private static final int VIVO_HAS_NOTCH_DISPLAY = 0x00000020;
    //表示是否有圆角
    private static final int VIVO_HAS_ROUND_DISPLAY = 0x00000008;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        try {
            if (vivoFtFeature == null) {
                ClassLoader cl = window.getContext().getClassLoader();
                vivoFtFeature = cl.loadClass("android.util.FtFeature");
            }
            Method get = vivoFtFeature.getMethod("isFeatureSupport", int.class);
            return (boolean) get.invoke(vivoFtFeature, VIVO_HAS_NOTCH_DISPLAY);
        } catch (Exception e) {
            return false;
        }
    }

    //vivo刘海大小固定
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        List<Rect> result = new ArrayList<>();
        Rect rect = new Rect();
        DisplayMetrics displayMetrics = window.getContext().getResources().getDisplayMetrics();
        final int notchWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, displayMetrics);
        final int notchHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27f, displayMetrics);
        rect.left = (displayMetrics.widthPixels - notchWidth) / 2;
        rect.right = rect.left + notchWidth;
        rect.top = 0;
        rect.bottom = notchHeight;
        result.add(rect);
        return result;
    }

}
