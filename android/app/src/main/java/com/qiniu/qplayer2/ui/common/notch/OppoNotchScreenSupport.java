package com.qiniu.qplayer2.ui.common.notch;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feifan on 2018/5/15.
 * Contacts me:404619986@qq.com
 * https://open.oppomobile.com/wiki/doc#id=10159
 */
final class OppoNotchScreenSupport extends DefaultNotchScreenSupport {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        try {
            return window.getContext().getPackageManager()
                    .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception ignored) {
            return false;
        }
    }

    //目前Oppo刘海屏机型尺寸规格都是统一的,显示屏宽度为1080px，高度为2280px,刘海区域宽度为324px, 高度为80px
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        List<Rect> result = new ArrayList<>();
        Context context = window.getContext();
        Rect rect = new Rect();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final int notchWidth = 324;
        final int notchHeight = 80;
        rect.left = (displayMetrics.widthPixels - notchWidth) / 2;
        rect.right = rect.left + notchWidth;
        rect.top = 0;
        rect.bottom = notchHeight;
        result.add(rect);
        return result;
    }

}
