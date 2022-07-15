# 0.0.7 ReleaseNote
#### 新增能力

- 支持flac 音频格式
- 支持wav(PCM_S24LE)音频格式

#### 修复问题

- 修复直播推流端 断网  拉流端闪buffering
- 修复预加载实例 参数没有设置 导致没有生效
- 纯视频 播放结束由video render 触发
- 修复buffering逻辑漏洞
- 修复内存泄漏
- 修复断网重连后，视频没有恢复播放