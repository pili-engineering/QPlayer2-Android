# 1.3.1 ReleaseNote

#### 能力
- 直播支持追帧

#### 优化
- 引入MikuDelivery，通过三级缓存提升短视频首开时间，预渲染（25ms-80ms），预加载（200ms-350ms）


#### 修复问题
- 修复m3u8 start非0时 seek 到0 失败，跳回当前播放进度
- 修复URL带% 导致崩溃
- 修复m3u8视频断网后未正常buffering




