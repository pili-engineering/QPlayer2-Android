# 1.1.4 ReleaseNote
#### 新增能力

- 新增TexturePlayerView 用TextureView作为渲染view

#### 修复问题

- 修复内存泄漏
- 以mediamodel的urltype来筛选流数据
- 音频数据声道数为0的 认为没有音频数据
- 修复预预加载实例拉流过程中无法中断
- 针对异常资源的抖底处理，  保证不崩溃
- 修复android10 机型偶现 seek 后 卡死的问题

