//
//  HeadsetNotification.h
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/15.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QNPlayerSettingsView.h"

NS_ASSUME_NONNULL_BEGIN

@interface QNHeadsetNotification : NSObject
///添加监听
///@param player 播放器
- (void)addNotificationsPlayer:(QPlayerContext*)player;
@end

NS_ASSUME_NONNULL_END
