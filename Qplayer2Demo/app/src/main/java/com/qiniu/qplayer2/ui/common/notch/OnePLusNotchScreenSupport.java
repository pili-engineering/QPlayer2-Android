package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.os.Build;
import android.view.Window;

import androidx.annotation.NonNull;


import com.qiniu.qplayer2.ui.common.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nero
 * 一加的当前只有oneplus A6000是刘海屏的，根据型号判断的刘海屏，获取状态栏的高度
 */
final class OnePLusNotchScreenSupport extends DefaultNotchScreenSupport {

    private static final String ONE_PLUS_6 = "ONEPLUS A6000";

    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        return ONE_PLUS_6.equals(Build.MODEL);
    }

    // 刘海宽度未知，高度以实际状态栏为准
    @NonNull
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        Rect rect = new Rect();
        rect.left = 0;
        rect.right = 0;
        rect.top = 0;
        rect.bottom = StatusBarCompat.getStatusBarHeight(window.getContext());

        List<Rect> result = new ArrayList<>();
        result.add(rect);
        return result;
    }
}
