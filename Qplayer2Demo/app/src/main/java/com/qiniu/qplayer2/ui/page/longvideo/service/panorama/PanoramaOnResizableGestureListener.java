package com.qiniu.qplayer2.ui.page.longvideo.service.panorama;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider;
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams;
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams;
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore;
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.IOnResizableGestureListener;
import com.qiniu.qplayer2ext.commonplayer.layer.gesture.detector.RotateGestureDetector;

public class PanoramaOnResizableGestureListener implements IOnResizableGestureListener {
    private CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams> mPlayerCore;
    private float mCurrentScale = 1F;

    PanoramaOnResizableGestureListener(CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>  playerCore) {
        mPlayerCore = playerCore;
    }

    @Override
    public void onResizableGestureStart(MotionEvent ev) {

    }

    @Override
    public void onResizableGestureEnd(MotionEvent ev) {

    }

    @Override
    public boolean onDown( MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp( MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) {
        mCurrentScale *= detector.getScaleFactor();
        Log.d("PanoramaScale", "mCurrentScale=$mCurrentScale scaleFactor=${p0.scaleFactor}");
//        return true
        if(mCurrentScale > 2) {
            mCurrentScale = 2.0f;
        }
        return mPlayerCore.getMPlayerContext().getPlayerRenderHandler().setPanoramaViewScale(mCurrentScale);
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

    }

    @Override
    public boolean onRotateBegin(RotateGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onRotate(RotateGestureDetector detector) {
        return false;
    }

    @Override
    public void onRotateEnd(RotateGestureDetector detector) {

    }
}
