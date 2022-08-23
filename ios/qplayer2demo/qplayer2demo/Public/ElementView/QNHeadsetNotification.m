//
//  HeadsetNotification.m
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/15.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import "QNHeadsetNotification.h"

@implementation QNHeadsetNotification{
    QPlayerContext* myPlayer;
}
- (void)addNotificationsPlayer:(QPlayerContext*)player{
    // 监听耳机插入和拔掉通知
    myPlayer = player;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioRouteChangeListenerCallback:) name:AVAudioSessionRouteChangeNotification object:nil];
}

/**
 *  耳机插入、拔出事件
 */
- (void)audioRouteChangeListenerCallback:(NSNotification*)notification {
    NSDictionary *interuptionDict = notification.userInfo;
    NSInteger routeChangeReason = [[interuptionDict valueForKey:AVAudioSessionRouteChangeReasonKey] integerValue];
    switch (routeChangeReason) {
        case AVAudioSessionRouteChangeReasonNewDeviceAvailable:
            // 耳机插入
            if (myPlayer.controlHandler.currentPlayerState == QPLAYER_STATE_PLAYING) {
//                [myPlayer resume_render];
                [myPlayer.controlHandler resumeRender];
            }
            break;
            
        case AVAudioSessionRouteChangeReasonOldDeviceUnavailable:
        {
            // 耳机拔掉
            // 拔掉耳机继续播放
            if (myPlayer.controlHandler.currentPlayerState == QPLAYER_STATE_PLAYING) {
                [myPlayer.controlHandler pauseRender];
            }
        }
            break;
            
        case AVAudioSessionRouteChangeReasonCategoryChange:
            // called at start - also when other audio wants to play
            NSLog(@"AVAudioSessionRouteChangeReasonCategoryChange");
            break;
    }
}
@end
