package com.qiniu.qplayer2.ui.page.longvideo.service.shoot

import android.provider.MediaStore
import android.view.ViewGroup
import androidx.core.content.contentValuesOf
import com.qiniu.qmedia.component.player.QIPlayerShootVideoListener
import com.qiniu.qplayer2.ui.page.longvideo.LongLogicProvider
import com.qiniu.qplayer2.ui.page.longvideo.LongPlayableParams
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoParams
import com.qiniu.qplayer2.ui.widget.commonplayer.functionwidget.shoot.PlayerShootVideoFunctionWidget
import com.qiniu.qplayer2ext.commonplayer.CommonPlayerCore
import com.qiniu.qplayer2ext.commonplayer.layer.function.FunctionWidgetLayoutParams
import com.qiniu.qplayer2ext.commonplayer.layer.function.PlayerFunctionContainer
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToast
import com.qiniu.qplayer2ext.commonplayer.layer.toast.PlayerToastConfig
import com.qiniu.qplayer2ext.commonplayer.service.IPlayerService

class PlayerShootVideoService :
    IPlayerService<LongLogicProvider, LongPlayableParams, LongVideoParams>,
    IPlayerShootVideoService, QIPlayerShootVideoListener {

    private lateinit var mPlayerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>
    private var mToken: PlayerFunctionContainer.FunctionWidgetToken<LongLogicProvider, LongPlayableParams, LongVideoParams>? =
        null

    override fun onStart() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().addPlayerShootVideoListener(this)
    }

    override fun onStop() {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().removePlayerShootVideoListener(this)

    }

    override fun bindPlayerCore(playerCore: CommonPlayerCore<LongLogicProvider, LongPlayableParams, LongVideoParams>) {
        mPlayerCore = playerCore
    }

    override fun shootVideo(source: Boolean) {
        mPlayerCore.mPlayerContext.getPlayerControlHandler().shootVideo( source)
    }

    override fun onShootSuccessful(
        image: ByteArray,
        width: Int,
        height: Int,
        type: QIPlayerShootVideoListener.ShootVideoType
    ) {


        //保存截图
        val contentValues = contentValuesOf(
            MediaStore.MediaColumns.DISPLAY_NAME to System.currentTimeMillis().toString(),
            MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
            MediaStore.MediaColumns.RELATIVE_PATH to "Pictures/QPlayer2VideoShoots/"
        )

        mPlayerCore.mAndroidContext?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )?.apply {
            mPlayerCore.mAndroidContext?.contentResolver?.openOutputStream(this)?.use {
                it.write(image, 0, image.size)
            }
        }

        mToken?.also {
            mPlayerCore.playerFunctionWidgetContainer?.hideWidget(it)
        }
        //展示截图
        val layoutParams = FunctionWidgetLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.layoutType = FunctionWidgetLayoutParams.LayoutAlignType.CENTER
        layoutParams.functionType = FunctionWidgetLayoutParams.FunctionType.DIALOG
        layoutParams.enterAnim = FunctionWidgetLayoutParams.NO_ANIMATION
        layoutParams.exitAnim = FunctionWidgetLayoutParams.NO_ANIMATION
        layoutParams.touchOutsideDismiss(true)
        val configuration =
            PlayerShootVideoFunctionWidget.PlayerShootVideoFunctionWidgetConfiguration(image)
        mToken = mPlayerCore.playerFunctionWidgetContainer?.showWidget(
            PlayerShootVideoFunctionWidget::class.java, layoutParams, configuration
        )

    }

    override fun onShootFailed() {
        val toast = PlayerToast.Builder()
            .toastItemType(PlayerToastConfig.TYPE_NORMAL)
            .location(PlayerToastConfig.LOCAT_LEFT_SIDE)
            .setExtraString(PlayerToastConfig.EXTRA_TITLE, "截图失败...")
            .duration(PlayerToastConfig.DURATION_3)
            .build()

        mPlayerCore.playerToastContainer?.showToast(toast)
    }
}