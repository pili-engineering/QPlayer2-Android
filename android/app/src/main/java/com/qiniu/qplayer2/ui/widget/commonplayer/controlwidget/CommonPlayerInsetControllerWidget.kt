package com.qiniu.qplayer2.ui.widget.commonplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore

import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams


class CommonPlayerInsetControllerWidget : ConstraintLayout,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {
    private var mTopBackground: Drawable? = null
    private var mBottomBackground: Drawable? = null
    private var mTopBackgroundHeight: Int = 0
    private var mBottomBackgroundHeight: Int = 0
    private var mContentTopPadding: Int = 0
    private var mContentBottomPadding: Int = 0
    private var mContentLeftPadding: Int = 0
    private var mContentRightPadding: Int = 0

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

//    private val mWindowInsetObserver = object : IWindowInsetObserver {
//        override fun onWindowInsetChanged(windowInset: WindowInset) {
//            val left = if (windowInset.leftPadding > 0) {
//                if (windowInset.leftPadding > mContentLeftPadding) windowInset.leftPadding - mContentLeftPadding else windowInset.leftPadding
//            } else {
//                0
//            }
//            val top = if (windowInset.topPadding > 0) {
//                if (windowInset.topPadding > mContentTopPadding) windowInset.topPadding - mContentTopPadding else windowInset.topPadding
//            } else {
//                0
//            }
//            val right = if (windowInset.rightPadding > 0) {
//                if (windowInset.rightPadding > mContentRightPadding) windowInset.rightPadding - mContentRightPadding else windowInset.rightPadding
//            } else {
//                0
//            }
//            val bottom = if (windowInset.bottomPadding > 0) {
//                if (windowInset.bottomPadding > mContentBottomPadding) windowInset.bottomPadding - mContentBottomPadding else windowInset.bottomPadding
//            } else {
//                0
//            }
//            setPadding(left, top, right, bottom)
//        }
//    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CommonPlayerWindowInset, defStyleAttr, 0)
        mTopBackground = a.getDrawable(R.styleable.CommonPlayerWindowInset_topBackground)
        mBottomBackground = a.getDrawable(R.styleable.CommonPlayerWindowInset_bottomBackground)
        if (mTopBackground != null) {
            mTopBackgroundHeight = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_topBackgroundHeight, 0)
        }
        if (mBottomBackground != null) {
            mBottomBackgroundHeight = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_bottomBackgroundHeight, 0)
        }
        mContentTopPadding = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_contentTopPadding, 0)
        mContentBottomPadding = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_contentBottomPadding, 0)
        mContentLeftPadding = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_contentLeftPadding, 0)
        mContentRightPadding = a.getDimensionPixelOffset(R.styleable.CommonPlayerWindowInset_contentRightPadding, 0)

        a.recycle()
    }

    override fun onWidgetActive() {
//        mPlayerContainer?.getActivityStateService()?.registerWindowInset(mWindowInsetObserver)
//        val windowInset = mPlayerContainer?.getActivityStateService()?.getWindowInset()
//        if (windowInset != null) {
//            mWindowInsetObserver.onWindowInsetChanged(windowInset)
//        } else {
//            mWindowInsetObserver.onWindowInsetChanged(WindowInset())
//        }
    }

    override fun onWidgetInactive() {
//        mPlayerContainer?.getActivityStateService()?.unregisterWindowInset(mWindowInsetObserver)
    }



    override fun draw(canvas: Canvas?) {
        drawBackground(canvas)
        super.draw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        drawBackground(canvas)
        super.dispatchDraw(canvas)
    }
    private fun drawBackground(canvas: Canvas?) {
        canvas ?: return
        val measureWidth = measuredWidth
        val measureHeight = measuredHeight
        if (measureHeight <= 0 || measureWidth <= 0) {
            return
        }
        if (mTopBackground != null && mTopBackgroundHeight > 0) {
            mTopBackground?.setBounds(0, 0, measureWidth, mTopBackgroundHeight)
            drawDrawable(mTopBackground!!, canvas)
        }

        if (mBottomBackground != null && mBottomBackgroundHeight > 0) {
            mBottomBackground?.setBounds(0, measureHeight - mBottomBackgroundHeight, measureWidth, measureHeight)
            drawDrawable(mBottomBackground!!, canvas)
        }
    }

    private fun drawDrawable(drawable: Drawable, canvas: Canvas) {
        val sX = scrollX
        val sY = scrollY
        if (sX or sY == 0) {
            drawable.draw(canvas)
        } else {
            canvas.translate(sX.toFloat(), sY.toFloat())
            drawable.draw(canvas)
            canvas.translate((-sX).toFloat(), (-sY).toFloat())
        }
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
    }
}
