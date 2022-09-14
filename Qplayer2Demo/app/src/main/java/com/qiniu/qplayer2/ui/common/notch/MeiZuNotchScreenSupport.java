/*
 * Copyright (c) 2015-2018 BiliBili Inc.
 */

package com.qiniu.qplayer2.ui.common.notch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

final class MeiZuNotchScreenSupport extends DefaultNotchScreenSupport {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        // 判断刘海设备
        boolean fringeDevice = false;
        try {
            Class clazz = Class.forName("flyme.config.FlymeFeature");
            Field field = clazz.getDeclaredField("IS_FRINGE_DEVICE");
            fringeDevice = (boolean) field.get(null);
        } catch (Exception e) {
        }

        if (fringeDevice) {
            // 判断隐藏刘海开关(默认关)
            boolean isFringeHidden = Settings.Global.getInt(window.getContext().getContentResolver(),
                    "mz_fringe_hide", 0) == 1;
            return !isFringeHidden;
        }

        return false;
    }

    @NonNull
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        List<Rect> result = new ArrayList<>();
        Context context = window.getContext();
        Rect rect = new Rect();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        // 获取刘海⾼度（51px）
        int notchHeight = 0;
        int fhid = context.getResources().getIdentifier("fringe_height", "dimen", "android");
        if (fhid > 0) {
            notchHeight = context.getResources().getDimensionPixelSize(fhid);
        }
        // 获取刘海宽度（534px,）
        int notchWidth = 0;
        int fwid = context.getResources().getIdentifier("fringe_width", "dimen", "android");
        if (fwid > 0) {
            notchWidth = context.getResources().getDimensionPixelSize(fwid);
        }

        rect.left = (displayMetrics.widthPixels - notchWidth) / 2;
        rect.right = rect.left + notchWidth;
        rect.top = 0;
        rect.bottom = notchHeight;
        result.add(rect);

        return result;
    }

    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
        // Android P中已增加适配刘海屏的原⽣接⼝⽀持。 设置此⽅式只在Android P以下版本有效。
        if (Build.VERSION.SDK_INT < 28) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= 0x00000080;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
        //注意： 此逻辑的关键在于为systemUiFlag添加 0x00000080， 请应⽤在操作时请不要将此字段覆盖。
    }

    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
        if (Build.VERSION.SDK_INT < 28) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility &= ~0x00000080;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }
}
