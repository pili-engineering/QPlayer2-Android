//
//  QNBottomView.h
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/15.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QNPlayerSettingsView.h"

NS_ASSUME_NONNULL_BEGIN
@interface QNButtonView : UIView

@property (nonatomic, weak) QPlayerContext *player;
///是否是直播
@property (nonatomic, assign) BOOL isLiving;
///初始化
///@param frame 添加的buttomView的frame
///@param player 播放器
///@param playerFrame 播放器的frame
///@param isLiving 是否是直播
-(instancetype)initWithFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame isLiving:(BOOL)isLiving;

///短视频初始化
///@param frame 添加的buttomView的frame
///@param player 播放器
///@param playerFrame 播放器的frame
///@param isLiving 是否是直播
-(instancetype)initWithShortVideoFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame isLiving:(BOOL)isLiving;
    
///修改播放状态  -->暂停/继续
-(void)setPlayState;

///点击播放按钮的回调
-(void)playButtonClickCallBack:(void (^) (BOOL selectedState))callback;

///全屏按钮的回调
-(void)changeScreenSizeButtonClickCallBack:(void (^) (BOOL selectedState))callback;

///全屏按钮的点击状态
-(BOOL)getFullButtonState;

///修改全屏按钮的点击状态
-(void)setFullButtonState:(BOOL)state;

///修改播放按钮的点击状态
-(void)setPlayButtonState:(BOOL)state;

///是否全屏的frame变化
- (void)changeFrame:(CGRect)frame isFull:(BOOL)isFull;

///进度条开始拖动回调
-(void)sliderStartCallBack:(void (^)(BOOL seeking))callBack;
///进度条结束拖动回调
-(void)sliderEndCallBack:(void (^)(BOOL seeking))callBack;
@end

NS_ASSUME_NONNULL_END
