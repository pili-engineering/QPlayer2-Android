/*
 * Copyright (c) 2015-2018 BiliBili Inc.
 */

package com.qiniu.qplayer2.ui.common.notch;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * author : maji
 * date   : 18/12/04
 * mail   : maji@bilibili.com
 */
final class SamsungNotchScreenSupport extends DefaultNotchScreenSupport {

    private static final String TAG = "SamsungNotchScreenSupport";

    private WindowInsetsWrapper mWindowInsetsWrapper;

    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        try {
            checkInit(window);
            return getNotchSize(window).size() > 0;
        } catch (Exception ignore) {}
        return super.hasNotchInScreen(window);
    }

    @Override
    public boolean hasNotchInScreenHardware(@NonNull Window window) {
        try {
            checkInit(window);
            final Resources res = window.getContext().getResources();
            final int resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android");
            final String spec = resId > 0 ? res.getString(resId) : null;
            return spec != null && !TextUtils.isEmpty(spec);
        } catch (Exception ignore) {
            return super.hasNotchInScreenHardware(window);
        }
    }

    @NonNull
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        checkInit(window);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //setWindowLayoutBlockNotch之后 DisplayCutout 可能为null
            //获取当前屏幕 notch size 需要重新建立一个新的 WindowInsetsWrapper 来实时获取 屏幕状态
            WindowInsetsWrapper tempWrapper = new WindowInsetsWrapper(window.getDecorView().getRootWindowInsets());
            if (tempWrapper.getDisplayCutoutWrapper() != null) {
                return tempWrapper.getDisplayCutoutWrapper().getBoundingRects();
            }
        }
        return super.getNotchSize(window);
    }

    @Override
    public List<Rect> getNotchSizeHardware(@NonNull Window window) {
        checkInit(window);
        //获取硬件层面上的 notch size
        //利用 setWindowLayoutBlockNotch() 执行之前 获取的 notch size 然后保存起来，当做硬件层面上 notch size
        if (mWindowInsetsWrapper != null
                && mWindowInsetsWrapper.getDisplayCutoutWrapper() != null) {
            return mWindowInsetsWrapper.getDisplayCutoutWrapper().getBoundingRects();
        }
        return super.getNotchSizeHardware(window);
    }

    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
        checkInit(window);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams lp = window.getAttributes();
            try {
                Field field = lp.getClass().getField("layoutInDisplayCutoutMode");
                field.setAccessible(true);
                field.setInt(lp, 1);//LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.setAttributes(lp);
            } catch (Exception ignore) {}
        }
    }

    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
        checkInit(window);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            WindowManager.LayoutParams lp = window.getAttributes();
            try {
                Field field = lp.getClass().getField("layoutInDisplayCutoutMode");
                field.setAccessible(true);
                field.setInt(lp, 2);//LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                window.setAttributes(lp);
            } catch (Exception ignore) {}
        }
    }


    /**
     *
     * hasNotchInScreen()
     * getNotchSizeHardware()
     * getNotchSize()
     * setWindowLayoutAroundNotch()
     * setWindowLayoutBlockNotch()
     *
     * 调用以上方法时，必须调用当前方法
     */
    private void checkInit(@NonNull Window window) {
        if (mWindowInsetsWrapper != null
                && mWindowInsetsWrapper.getDisplayCutoutWrapper() != null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final View decorView = window.getDecorView();
            final WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets == null) {
                return;
            }
            mWindowInsetsWrapper = new WindowInsetsWrapper(windowInsets);
        }
    }


    static class WindowInsetsWrapper {


        private WindowInsets mInner;
        private DisplayCutoutWrapper mDisplayCutoutWrapper;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        public WindowInsetsWrapper(WindowInsets windowInsets) {
            mInner = windowInsets;

            mDisplayCutoutWrapper = new DisplayCutoutWrapper(mInner);
        }

        /**
         * Returns the display cutout if there is one.
         *
         * @return the display cutout or null if there is none
         */
        @Nullable
        public DisplayCutoutWrapper getDisplayCutoutWrapper() {
            return mDisplayCutoutWrapper;
        }
    }


    static class DisplayCutoutWrapper {

        private static final String GET_DISPLAY_CUTOUT = "getDisplayCutout";
        private static final String GET_SAFE_INSET_TOP = "getSafeInsetTop";
        private static final String GET_SAFE_INSET_BOTTOM = "getSafeInsetBottom";
        private static final String GET_SAFE_INSET_LEFT = "getSafeInsetLeft";
        private static final String GET_SAFE_INSET_RIGHT = "getSafeInsetRight";
        private final Rect mSafeInsets = new Rect();
        private final List<Rect> mBoundingRects = new ArrayList<>();

        @SuppressLint("PrivateApi")
        @SuppressWarnings("unchecked")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
        DisplayCutoutWrapper(WindowInsets windowInsets) {

            try {
                Method method = WindowInsets.class.getDeclaredMethod(GET_DISPLAY_CUTOUT);
                Object displayCutoutInstance = method.invoke(windowInsets);
                Class cls = displayCutoutInstance.getClass();
                int top = (int) cls.getDeclaredMethod(GET_SAFE_INSET_TOP).invoke(displayCutoutInstance);
                int bottom = (int) cls.getDeclaredMethod(GET_SAFE_INSET_BOTTOM).invoke(displayCutoutInstance);
                int left = (int) cls.getDeclaredMethod(GET_SAFE_INSET_LEFT).invoke(displayCutoutInstance);
                int right = (int) cls.getDeclaredMethod(GET_SAFE_INSET_RIGHT).invoke(displayCutoutInstance);
                mSafeInsets.set(left, top, right, bottom);
                mBoundingRects.add(mSafeInsets);

            } catch (Exception e) {
                Log.e(TAG, "DisplayCutoutWrapper init exception: " + e.getMessage());
            }
        }

        /** Returns the inset from the top which avoids the display cutout inpixels. */
        public int getSafeInsetTop() {
            return mSafeInsets.top;
        }

        /** Returns the inset from the bottom which avoids the display cutoutin pixels. */
        public int getSafeInsetBottom() {
            return mSafeInsets.bottom;
        }

        /** Returns the inset from the left which avoids the display cutout inpixels. */
        public int getSafeInsetLeft() {
            return mSafeInsets.left;
        }

        /** Returns the inset from the right which avoids the display cutoutin pixels. */
        public int getSafeInsetRight() {
            return mSafeInsets.right;
        }

        /**
         * Returns a list of {@code Rect}s, each of which is the bounding
         rectangle for a non-functional
         * area on the display.
         *
         * There will be at most one non-functional area per short edge of the
         device, and none on
         * the long edges.
         *
         * @return a list of bounding {@code Rect}s, one for each display cutout
        area.
         */
        public List<Rect> getBoundingRects() {
            return mBoundingRects;
        }
    }

}


