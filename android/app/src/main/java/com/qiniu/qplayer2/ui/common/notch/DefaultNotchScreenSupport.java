package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
class DefaultNotchScreenSupport implements INotchScreenSupport {
    @Override
    public boolean hasNotchInScreen(@NonNull Window window) {
        return false;
    }

    @Override
    public boolean hasNotchInScreenHardware(@NonNull Window window) {
        return hasNotchInScreen(window);
    }

    @NonNull
    @Override
    public List<Rect> getNotchSize(@NonNull Window window) {
        return new ArrayList<>();
    }

    @Override
    public List<Rect> getNotchSizeHardware(@NonNull Window window) {
        return getNotchSize(window);
    }

    @Override
    public void setWindowLayoutAroundNotch(@NonNull Window window) {
    }

    @Override
    public void setWindowLayoutBlockNotch(@NonNull Window window) {
    }

    @Override
    public void setWindowLayoutNotchDefault(@NonNull Window window) {

    }

    /**
     * 折叠屏
     * @param window
     */
    @Override
    public void onWindowConfigChanged(@NonNull Window window) {

    }
}
