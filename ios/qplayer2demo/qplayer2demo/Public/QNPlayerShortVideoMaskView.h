//
//  QNPlayerShortVideoMaskView.h
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/25.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class QNPlayerShortVideoMaskView;
@protocol QNPlayerShortVideoMaskViewDelegate <NSObject>
- (void)reOpenPlayPlayerMaskView:(QNPlayerShortVideoMaskView *)playerMaskView;

@end

@interface QNPlayerShortVideoMaskView : UIView


/**
 QNPlayerMaskView 短视频初始化方法

 @param frame QNPlayerMaskView 的位置及大小
 @param player PLPlayer 的实例
 @param isLiving 是否是直播
 @return QNPlayerMaskView 的实例子
 */

-(instancetype)initWithShortVideoFrame:(CGRect)frame player:(QPlayerContext *)player isLiving:(BOOL)isLiving;

//@property (nonatomic, weak) QPlayer *player;

@property (nonatomic, weak) QPlayerContext *player;
@property (nonatomic, assign) id<QNPlayerShortVideoMaskViewDelegate> delegate;
@property (nonatomic, assign) BOOL isLiving;


/**
 修改播放按钮的状态
 */
-(void)setPlayButtonState:(BOOL)state;
@end

NS_ASSUME_NONNULL_END
