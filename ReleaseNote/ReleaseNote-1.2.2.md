# 1.2.2 ReleaseNote
#### 优化

- 介于很多客户的kotlin版本都比较低，导致在引入QPlayer2时报错，这个版本将qplayer2-core和qplayer2-ext的kotlin的版本进行了降级，降至1.4.20，避免了编译时报错


#### 修复问题

- 修复在软解情况下，收不到七牛推流端/RTC端发送的SEI数据
