//
//  SoundSliderView.h
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/14.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "QNPlayerSettingsView.h"
NS_ASSUME_NONNULL_BEGIN

@interface QNSoundSliderView : UIView
///初始化
///@param frame 添加的View的frame
///@param player 播放器
///@param playerFrame 播放器的frame
-(instancetype)initWithFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame;

///更新声音图标
- (void)updateVoulmeSliderImage;

@end

NS_ASSUME_NONNULL_END
