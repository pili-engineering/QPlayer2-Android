package com.qiniu.qplayer2.ui.page.longvideo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.RawRes
import com.qiniu.qmedia.component.player.QMediaModelBuilder
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.common.system.FileUtils
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayerDataSource
import com.qiniu.qplayer2ext.commonplayer.data.DisplayOrientation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object LongPlayerDataSourceFactory {




    fun create(context: Context): CommonPlayerDataSource<LongPlayableParams, LongVideoParams> {


        val path = FileUtils.copyFromResToFile(context, R.raw.qiniu_2023_1080p, "qiniu_2023_1080p.mp4")

        val dataSourceBuilder =
            CommonPlayerDataSource.DataSourceBuilder<LongPlayableParams, LongVideoParams>()
        var videoParams: LongVideoParams

        var builder = QMediaModelBuilder()
        var url = ""
        var name = ""

        builder = QMediaModelBuilder()
//        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.qiniu_2023_1080p)

//        val path = ("android.resource://$packageName").toString() + "/raw/qiniu_2023_1080p.mp4"

        path?.let {
            builder.addStreamElement(
                "", QURLType.QAUDIO_AND_VIDEO, 1080,
                it, true
            )
        }


        name = "1-点播-http-mp4-30fps"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    )
            )
        )

        return dataSourceBuilder.build()
    }
}