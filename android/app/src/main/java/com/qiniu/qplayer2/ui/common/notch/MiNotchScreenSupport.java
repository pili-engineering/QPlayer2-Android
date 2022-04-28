package com.qiniu.qplayer2.ui.common.notch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.qiniu.qplayer2.common.system.SystemProperties;
import com.qiniu.qplayer2.ui.common.statusbar.StatusBarCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feifan on 2018/6/12.
 * Contacts me:404619986@qq.com
 * https://dev.mi.com/console/doc/detail?pId=1293
 */
final class MiNotchScreenSupport extends DefaultNotchScreenSupport {
    //绘制到刘海区域
    private static final int FLAG_NOTCH_IMMERSIVE = 0x00000100;
    //竖屏绘制到刘海区域
    private static final int FLAG_NOTCH_PORTRAIT = 0x00000200;
    //横屏绘制刘海区域
    private static final int FLAG_NOTCH_LANDSCAPE = 0x00000400;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        try {
            return "1".equals(SystemProperties.get("ro.miui.notch", null));
        } catch (Exception ignored) {
            return false;
        }
    }

    //小米的状态栏高度会略高于刘海屏的高度，因此通过获取状态栏的高度来间接避开刘海屏,宽度未知，直接返回了屏幕宽度
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        List<Rect> result = new ArrayList<>();

        Context context = window.getContext();
        Rect rect = new Rect();
        rect.top = 0;
        rect.bottom = StatusBarCompat.getStatusBarHeight(context);
        rect.left = 0;
        rect.right = context.getResources().getDisplayMetrics().widthPixels;
        result.add(rect);

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
        final int flag = FLAG_NOTCH_IMMERSIVE | FLAG_NOTCH_PORTRAIT | FLAG_NOTCH_LANDSCAPE;
        try {
            Method method = Window.class.getMethod("addExtraFlags", int.class);
            method.invoke(window, flag);
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
        final int flag = FLAG_NOTCH_IMMERSIVE | FLAG_NOTCH_PORTRAIT | FLAG_NOTCH_LANDSCAPE;
        try {
            Method method = Window.class.getMethod("clearExtraFlags", int.class);
            method.invoke(window, flag);
        } catch (Exception ignored) {
        }
    }
}
