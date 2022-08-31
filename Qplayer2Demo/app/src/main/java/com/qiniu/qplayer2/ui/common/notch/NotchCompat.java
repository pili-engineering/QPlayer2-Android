/*
 * Copyright (c) 2015-2018 BiliBili Inc.
 */

package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.NonNull;
import android.view.Window;


import com.qiniu.qplayer2.common.system.RomUtils;

import java.util.List;

/**
 * 凹口屏适配工具类
 * 默认Android O以上才有刘海屏,Android P及以上调用官方API判断,Android O利用厂商API判断
 * Created by feifan on 2018/5/11.
 * Contacts me:404619986@qq.com
 */
public class NotchCompat {
    private static INotchScreenSupport mNotchScreenSupport = null;

    /**
     * 判断屏幕是否为凹口屏
     * WindowInsets在View Attach到Window上之后才会创建
     * 因此想要获得正确的结果，方法的调用时机应在DecorView Attach之后
     */
    public static boolean hasDisplayCutout(@NonNull Window window) {
        checkScreenSupportInit();
        return mNotchScreenSupport.hasNotchInScreen(window);
    }

    public static boolean hasDisplayCutoutHardware(@NonNull Window window) {
        checkScreenSupportInit();
        return mNotchScreenSupport.hasNotchInScreenHardware(window);
    }

    /**
     * 获取凹口屏大小
     */
    @NonNull
    public static List<Rect> getDisplayCutoutSize(@NonNull Window window) {
        checkScreenSupportInit();
        return mNotchScreenSupport.getNotchSize(window);
    }

    @NonNull
    public static List<Rect> getDisplayCutoutSizeHardware(@NonNull Window window) {
        checkScreenSupportInit();
        return mNotchScreenSupport.getNotchSizeHardware(window);
    }

    /**
     * 设置始终使用凹口屏区域
     */
    public static void immersiveDisplayCutout(@NonNull Window window) {
        checkScreenSupportInit();
        mNotchScreenSupport.setWindowLayoutAroundNotch(window);
    }

    /**
     * 设置始终不使用凹口屏区域
     */
    public static void blockDisplayCutout(Window window) {
        checkScreenSupportInit();
        mNotchScreenSupport.setWindowLayoutBlockNotch(window);
    }

    /**
     * 将刘海屏 flag 重置
     */
    public static void resetDisplayCutout(Window window) {
        checkScreenSupportInit();
        mNotchScreenSupport.setWindowLayoutNotchDefault(window);
    }

    public static void onWindowConfigChanged(Window window) {
        checkScreenSupportInit();
        mNotchScreenSupport.onWindowConfigChanged(window);
    }

    private static void checkScreenSupportInit() {
        if (mNotchScreenSupport != null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mNotchScreenSupport = new DefaultNotchScreenSupport();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mNotchScreenSupport = new PNotchScreenSupport();
        } else if (RomUtils.isMiuiRom()) {
            mNotchScreenSupport = new MiNotchScreenSupport();
        } else if (RomUtils.isHuaweiRom()) {
            mNotchScreenSupport = new HwNotchScreenSupport();
        } else if (RomUtils.isOppoRom()) {
            mNotchScreenSupport = new OppoNotchScreenSupport();
        } else if (RomUtils.isVivoRom()) {
            mNotchScreenSupport = new VivoNotchScreenSupport();
        }  else if (RomUtils.isMeizuRom()) {
            mNotchScreenSupport = new MeiZuNotchScreenSupport();
        } else if (RomUtils.isSamsungRom()) {
            mNotchScreenSupport = new SamsungNotchScreenSupport();
        } else if (RomUtils.isOnePlusRom()) {
            mNotchScreenSupport = new OnePLusNotchScreenSupport();
        } else {
            mNotchScreenSupport = new DefaultNotchScreenSupport();
        }
    }
}
