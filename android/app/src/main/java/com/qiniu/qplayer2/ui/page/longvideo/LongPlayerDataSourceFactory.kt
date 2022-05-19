package com.qiniu.qplayer2.ui.page.longvideo

import com.qiniu.qmedia.component.player.QMediaModelBuilder
import com.qiniu.qmedia.component.player.QURLType
import com.qiniu.qmedia.component.player.QVideoRenderType
import com.qiniu.qplayer2ext.commonplayer.data.DisplayOrientation
import com.qiniu.qplayer2ext.commonplayer.data.CommonPlayerDataSource
import com.qiniu.qplayer2.repository.setting.PlayerSettingRespostory

object LongPlayerDataSourceFactory {

    fun create(): CommonPlayerDataSource<LongPlayableParams, LongVideoParams> {
        val dataSourceBuilder =
            CommonPlayerDataSource.DataSourceBuilder<LongPlayableParams, LongVideoParams>()
        var videoParams: LongVideoParams

        var builder = QMediaModelBuilder()
        var url = ""
        var name = ""
        builder = QMediaModelBuilder()

        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 1080,
            "http://demo-videos.qnsdk.com/qiniu-1080p.mp4", true
        )
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 720,
            "http://demo-videos.qnsdk.com/qiniu-720p.mp4", false
        )
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 480,
            "http://demo-videos.qnsdk.com/qiniu-480p.mp4", false
        )
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 360,
            "http://demo-videos.qnsdk.com/qiniu-360p.mp4", false
        )
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 240,
            "http://demo-videos.qnsdk.com/qiniu-240p.mp4", false
        )
        name = "1-点播-http-mp4-30fps-多清晰度"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,
                    false
                )
            )
        )

        //音视频分开2个流的视频要用精准seek
        builder = QMediaModelBuilder()
        builder.addElement(
            "", QURLType.QAUDIO, 100,
            "http://demo-videos.qnsdk.com/only-audio.m4s", true
        )
        builder.addElement(
            "", QURLType.QVIDEO, 1080,
            "http://demo-videos.qnsdk.com/only-video-1080p-60fps.m4s", true
        )

        name = "2-点播-http-m4s-60fps-音视流分离"
        videoParams = LongVideoParams(name, name.hashCode().toLong())

        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 960,
            "http://demo-videos.qnsdk.com/shortvideo/nike.mp4", true
        )
        name = "3-点播-http-mp4-28ps-竖屏"

        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,
                    false
                )
            )
        )


        builder = QMediaModelBuilder()
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 720,
            "http://pili-hdl.qnsdk.com/sdk-live/timestamp-6M.flv", true
        )

        name = "4-直播-http-flv-60fps"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    true
                )
            )
        )

        builder = QMediaModelBuilder()
        name = "5-直播-http-m3u8-60fps"
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 720,
            "http://pili-hls.qnsdk.com/sdk-live/timestamp-6M.m3u8", true
        )
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,

                    true
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/timestamp-6M"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/timestamp"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 480, url, false)
        name = "6-直播-rtmp-多清晰度"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,

                    true
                )
            )
        )


        builder = QMediaModelBuilder()
        url =
            "https://ms-shortvideo-dn.eebbk.net/bbk-n002/stream/2021/07/30/1911/68/357c1b03fa454a9c6b3198c9c6a3b49a.mp4?sign=13eb41043c62c8f462dcb6226fd1a5a6&t=613852bf"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true, "http://video.eebbk.net")
        name = "7-点播-https-mp4-50fps-referer"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )


//        builder = QMediaModelBuilder()
//        url =
//            "https://ms-shortvideo-dn.eebbk.net/bbk-n002/stream/2021/12/27/1143/14571/e3d3dc6b29afcccb6ff01c8f23e4018b.mp4?sign=6a1d95afe884f9654a6598b076affd00&t=61dd5fa6"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true, "http://video.eebbk.net")
//        videoParams = LongVideoParams("1080P-50FPS-referer-2", url.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    false
//                )
//            )
//        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/song.mp3"
        builder.addElement("", QURLType.QAUDIO, 100, url, true)
        name = "8-点播-http-mp3-纯音频"

        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/only-video-1080p-60fps.m4s"
        builder.addElement("", QURLType.QVIDEO, 1080, url, true)
        name = "9-点播-http-m4s-纯视频"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )



        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/bbk-bt709.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "10-点播-http-mp4-50fps"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )


//        builder = QMediaModelBuilder()
//        url = "http://demo-videos.qnsdk.com/bbk-H265-50fps.mp4"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 0, url, true)
//        videoParams = LongVideoParams(url, url.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    false
//                )
//            )
//        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/Sync-Footage-V1-H264.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "11-点播-http-mp4-60fps-音画同步测试"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "https://img.qunliao.info:443/4oEGX68t_9505974551.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 360, url, true)
        name = "12-点播-https-mp4-25fps-端口443"

        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "rtmp://pili-publish.qnsdk.com/sdk-live/6666"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "13-直播-rtmp://pili-publish.qnsdk.com/sdk-live/6666"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    true
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "http://ojpjb7lbl.bkt.clouddn.com/h265/2000k/265_test.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "14-点播-hhtp-m3u8-25fps-H265"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/H264-flv.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        name = "15-点播-https-flv-30fps-H264"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/10701032_194625-hd%20%281%29.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "16-点播-https-mp4-30fps-竖屏"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://app.modelbook.xyz:5001/video/21_756_ENH0NN430O3E8MH.mp4/index.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 360, url, true)
        name = "17-点播-https-m3u8-30fps-h264-端口5001"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/1599039859854_9242359.mp3"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "18-点播-https-mp3-纯音频-有封面"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/VID_20220207_144828.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "19-点播-https-mp4-30fps-旋转角度(180度)"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/10037108_065355-hd%20%281%29.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        name = "20-点播-https-mp4-30fps-非正常比例"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )




        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/test1.wma"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        name = "21-点播-https-wma-30fps-不支持的封装格式"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/flv.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        name = "22-点播-https-flv-FLV1-不支持的编码格式"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/video1643265479033.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
        name = "23-点播-https-mp4-不支持的像素格式-yuvj420p"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.VERTICAL,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        name = "24-点播m3u8-加密"
        builder.addElement(
            "", QURLType.QAUDIO_AND_VIDEO, 720,
            "http://cdn.qiniushawn.top/timeshift3.m3u8", true
        )
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,

                    true
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/VR-Panorama-Equirect-Angular-4500k.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 4000, url, true, "","",
            QVideoRenderType.PANORAMA_EQUIRECT_ANGULAR)
        name = "25-点播-http-vr-Equirect-Angular-mp4"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-playback.qnsdk.com/recordings/z1.sdk-live.6666/1652355051_1652355244.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "26-点播-http-m3u8-SEI"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L,
                    false
                )
            )
        )



        return dataSourceBuilder.build()
    }
}