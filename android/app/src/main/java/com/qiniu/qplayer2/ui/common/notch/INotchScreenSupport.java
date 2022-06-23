package com.qiniu.qplayer2.ui.common.notch;

import android.graphics.Rect;
import android.view.Window;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
interface INotchScreenSupport {
    boolean hasNotchInScreen(@NonNull Window window);

    /**
     * 硬件上屏幕是否存在凹口，{@link #hasNotchInScreen(Window)}用判断当前状态是否存在凹口，
     * Android p在调用了{@link #setWindowLayoutBlockNotch(Window)}认为当前状态不存在凹口
     *
     * @param window window
     * @return false, 不存在凹口，true，存在凹口
     */
    boolean hasNotchInScreenHardware(@NonNull Window window);

    @NonNull
    List<Rect> getNotchSize(@NonNull Window window);

    /**
     * 获取硬件上屏幕凹口size，{@link #getNotchSize(Window)},获取当前状态凹口size
     * Android p在调用了{@link #setWindowLayoutBlockNotch(Window)}认为当前状态不存在凹口
     * 所以获取不到size
     *
     * @param window window
     * @return Rect
     */
    List<Rect> getNotchSizeHardware(@NonNull Window window);

    void setWindowLayoutAroundNotch(@NonNull Window window);

    void setWindowLayoutBlockNotch(@NonNull Window window);

    /**
     * 重置刘海屏 flag
     */
    void setWindowLayoutNotchDefault(@NonNull Window window);

    /**
     * 折叠屏 config change
     * @param window
     */
    void onWindowConfigChanged(@NonNull Window window);
}
