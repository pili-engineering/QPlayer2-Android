# 1.3.0 ReleaseNote

#### 接口变更

- `QMediaModelBuilder`的`addElement`修改成`addStreamElement`以区分新增接口`addSubtitleElement`

#### 能力

- 新增SRT字幕的加载并根据时间轴回调当前的文案给上层
- 支持HLS 私有加密/通用加密方案

#### 优化

- 重构渲染模块，提升模块健壮性/复用性
- 优化立即切换清晰度的速度
- 鉴权方式增加authorid的方式，方便无法确定包名的情况下使用


#### 修复问题

- 修复某些清晰度下，画面展示不全的问题
- 修复hls点播情况下  断网后直接回调complete状态，重新联网后不重试
- 针对某些时机硬解失败的情况，对硬解解码器进行重建

