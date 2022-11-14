package com.qiniu.qplayer2.ui.widget.commonplayer.controlwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import com.qiniu.qmedia.component.player.QIPlayerShootVideoListener
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.page.longvideo.service.ServiceOwnerType
import com.qiniu.qplayer2.ui.page.longvideo.service.shoot.IPlayerShootVideoService
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.control.IControlWidget
import java.io.ByteArrayInputStream
import java.io.FileInputStream

class CommonPlayerShootVideoWidget: AppCompatImageView, View.OnClickListener,
    IControlWidget<LongLogicProvider, LongPlayableParams, LongVideoParams> {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)

    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val drawableCompat = ContextCompat.getDrawable(context, R.drawable.qmedia_ic_picture_bold)
        if (drawableCompat != null) {
            setImageDrawable(drawableCompat)
        }
    }

    override fun onClick(v: View?) {
        val service = mPlayerCore.getPlayerEnviromentServiceManager().getPlayerService<IPlayerShootVideoService>(
            ServiceOwnerType.PLAYER_SHOOT_VIDEO_SERVICE.type)
        service?.also {
            it.shootVideo(true)
        }
    }

    override fun onWidgetActive() {
        setOnClickListener(this)
    }

    override fun onWidgetInactive() {
        setOnClickListener(null)
    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onShooted(pixels: ByteArray, width: Int, height: Int, GLformat: Int, GLType: Int) {
//        val memStream = ByteArrayInputStream(pixels)
//        val drawable = Drawable.createFromStream(memStream, "")
//        memStream.close()
//        if (drawable != null) {
//            setImageDrawable(drawable)
//        }
//        val opts: BitmapFactory.Options = BitmapFactory.Options()
//        opts.inPreferredConfig = Bitmap.Config.RGB_565
//        val bitmap = BitmapFactory.decodeByteArray(pixels, 0, pixels.size, opts)
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixels))
//        bitmap?.also { bm->
//            val contentValues = contentValuesOf(
//                MediaStore.MediaColumns.DISPLAY_NAME to System.currentTimeMillis().toString(),
//                MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
//                MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/QPlayer2VideoShoots/"
//            )
//            this.context.contentResolver.insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            )?.apply {
//                this@CommonPlayerShootVideoWidget.context.contentResolver.openOutputStream(this)
//                    .use {
//                        bm.compress(Bitmap.CompressFormat.JPEG, 100, it)
////                            it?.write(pixels, 0, pixels.size)
//                    }
//            }
//        }

//    }
}