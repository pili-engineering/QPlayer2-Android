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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition,

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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
                )
            )
        )


//        builder = QMediaModelBuilder()
//        builder.addElement(
//            "", QURLType.QAUDIO_AND_VIDEO, 720,
//            "http://pili-hdl.qnsdk.com/sdk-live/timestamp-6M.flv", true
//        )
//
//        name = "4-直播-http-flv-60fps"
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(true),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L
//                )
//            )
//        )

//        builder = QMediaModelBuilder()
//        name = "5-直播-http-m3u8-60fps"
//        builder.addElement(
//            "", QURLType.QAUDIO_AND_VIDEO, 720,
//            "http://pili-hls.qnsdk.com/sdk-live/timestamp-6M.m3u8", true
//        )
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(true),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L
//                )
//            )
//        )

//        builder = QMediaModelBuilder()
//        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/timestamp-6M"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720, url, true)
//        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/timestamp"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 480, url, false)
//        name = "6-直播-rtmp-多清晰度"
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(true),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    PlayerSettingRespostory.startPosition
                )
            )
        )
        builder = QMediaModelBuilder()
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/testf11@480p"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "13-1-直播-rtmp://pili-rtmp.qnsdk.com/sdk-live/testf11@480p"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 720,
            "rtmp://pili-publish.qnsdk.com/sdk-live/6666", true)

        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 480,
            "rtmp://pili-publish.qnsdk.com/sdk-live/6666@480p", false)

        name = "13-0-直播@-多码率-直播-rtmp://pili-publish.qnsdk.com/sdk-live/6666"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hls.qnsdk.com/sdk-live/6666.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "13-2-直播-http://pili-hls.qnsdk.com/sdk-live/6666.m3u8"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hdl.qnsdk.com/sdk-live/6666.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "13-3-直播-http://pili-hdl.qnsdk.com/sdk-live/6666.flv"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )




        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/zeng.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "14-点播-hhtp-m3u8-30fps"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.VERTICAL,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

//        builder = QMediaModelBuilder()
//        name = "24-点播m3u8-加密"
//        builder.addElement(
//            "", QURLType.QAUDIO_AND_VIDEO, 720,
//            "http://cdn.qiniushawn.top/timeshift3.m3u8", true
//        )
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L,
//
//                    true
//                )
//            )
//        )


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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
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
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/long_movie.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "27-点播-http-电影"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

//        builder = QMediaModelBuilder()
//        url = "https://sdk-release.qnsdk.com/4K_25_21514.mp4"
//        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 3840, url, true)
//        name = "27-点播-https-4K-25FPS-22937kb"
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(false),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L
//                )
//            )
//        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/2K_60_6040.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 2560, url, true)
        name = "28-点播-https-2K-60FPS-6333kb"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/2K_25_11700.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 2560, url, true)
        name = "29-点播-https-2K-25FPS-12053kb"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/1080_60_5390.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "30-点播-https-1080-60FPS-5656kb"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "https://sdk-release.qnsdk.com/4K_3840%2A1920.m4s"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 3840, url, true)
        name = "31-点播-https-4k-30FPS-8534kb"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "https://demo-qnrtc-files.qnsdk.com/six_second.mp4"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 480, url, true)
        name = "32-点播-https-480-30FPS-6秒视频"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )


        builder = QMediaModelBuilder()
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/audioonly"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "33-1-直播-纯音频-rtmp://pili-rtmp.qnsdk.com/sdk-live/audioonly"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hls.qnsdk.com/sdk-live/audioonly.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "33-2-直播-纯音频-http://pili-hls.qnsdk.com/sdk-live/audioonly.m3u8"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hdl.qnsdk.com/sdk-live/audioonly.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "33-3-直播-纯音频-http://pili-hdl.qnsdk.com/sdk-live/audioonly.flv"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "rtmp://pili-rtmp.qnsdk.com/sdk-live/videoonly"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "34-1-直播-纯视频-rtmp://pili-rtmp.qnsdk.com/sdk-live/videoonly"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hls.qnsdk.com/sdk-live/videoonly.m3u8"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "34-2-直播-纯视频-http://pili-hls.qnsdk.com/sdk-live/videoonly.m3u8"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://pili-hdl.qnsdk.com/sdk-live/videoonly.flv"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "34-3-直播-纯视频-http://pili-hdl.qnsdk.com/sdk-live/videoonly.flv"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/only-audio-wav.wav"
        builder.addElement("", QURLType.QAUDIO, 1080, url, true)
        name = "35-点播-纯音频-wav-http://demo-videos.qnsdk.com/only-audio-wav.wav"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

        builder = QMediaModelBuilder()
        url = "http://demo-videos.qnsdk.com/audio-only-flac.flac"
        builder.addElement("", QURLType.QAUDIO, 1080, url, true)
        name = "36-点播-纯音频-flac-http://demo-videos.qnsdk.com/audio-only-flac.flac"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(false),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )

//        builder = QMediaModelBuilder()
//        url = "https://live-vod.huxiu.com/20220817_10582_playback_759.mp3"
//        builder.addElement("", QURLType.QAUDIO, 1080, url, true)
//        name = "38-点播-mp3-长 -https://live-vod.huxiu.com/20220817_10582_playback_759.mp3"
//        videoParams = LongVideoParams(name, name.hashCode().toLong())
//        dataSourceBuilder.addVideo(
//            videoParams,
//            arrayListOf<LongPlayableParams>(
//                LongPlayableParams(
//                    builder.build(false),
//                    LongControlPanelType.Normal.type,
//                    DisplayOrientation.LANDSCAPE,
//                    LongEnviromentType.LONG.type,
//                    0L
//                )
//            )
//        )



        builder = QMediaModelBuilder()
        url = "srt://180.101.136.81:1935?streamid=#!::h=pilidemo/timestamp,m=request,domain=live-pilidemo.cloudvdn.com"
        builder.addElement("", QURLType.QAUDIO_AND_VIDEO, 1080, url, true)
        name = "39-直播-SRT-srt://180.101.136.81:1935?streamid=#!::h=pilidemo/timestamp,m=request,domain=live-pilidemo.cloudvdn.com"
        videoParams = LongVideoParams(name, name.hashCode().toLong())
        dataSourceBuilder.addVideo(
            videoParams,
            arrayListOf<LongPlayableParams>(
                LongPlayableParams(
                    builder.build(true),
                    LongControlPanelType.Normal.type,
                    DisplayOrientation.LANDSCAPE,
                    LongEnviromentType.LONG.type,
                    0L
                )
            )
        )


        return dataSourceBuilder.build()
    }
}