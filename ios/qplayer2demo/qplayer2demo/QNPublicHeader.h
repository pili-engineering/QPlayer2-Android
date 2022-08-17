//
//  QNPublicHeader.h
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/23.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#ifndef QNPublicHeader_h
#define QNPublicHeader_h

/** 屏幕宽、高 **/
#define PL_SCREEN_WIDTH [UIScreen mainScreen].bounds.size.width
#define PL_SCREEN_HEIGHT [UIScreen mainScreen].bounds.size.height

/** 蒙版宽、高 **/
#define PLAYER_PORTRAIT_WIDTH PL_SCREEN_WIDTH
#define PLAYER_PORTRAIT_HEIGHT floor(PL_SCREEN_WIDTH * 9 / 16)

#define PL_HAS_NOTCH ({BOOL isPhoneX = NO;\
if (@available(iOS 13.0, *)) {\
    isPhoneX =  [UIApplication sharedApplication].windows.firstObject.safeAreaInsets.bottom > 0.0;\
    }\
else if (@available(iOS 11.0, *)) {\
    isPhoneX = [[UIApplication sharedApplication] delegate].window.safeAreaInsets.bottom > 0.0;\
}\
(isPhoneX);})

#define PL_iPhoneX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)
#define PL_iPhoneXR ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(828, 1792), [[UIScreen mainScreen] currentMode].size) : NO)
#define PL_iPhoneXSMAX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1242, 2688), [[UIScreen mainScreen] currentMode].size) : NO)

#define PL_HEIGHT_RATIO (PL_SCREEN_HEIGHT / 568.f)


/** 颜色值 **/
#define PL_COLOR_RGB(a,b,c,d) [UIColor colorWithRed:a/255.0 green:b/255.0 blue:c/255.0 alpha:d]
#define PL_LINE_COLOR PL_COLOR_RGB(195, 198, 198, 1)
#define PL_BUTTON_BACKGROUNDCOLOR PL_COLOR_RGB(54, 54, 54, 0.32)
#define PL_SELECTED_BLUE PL_COLOR_RGB(69, 169, 195, 1)
#define PL_DARK_COLOR PL_COLOR_RGB(55, 59, 64, 1)
#define PL_BACKGROUND_COLOR PL_COLOR_RGB(240, 243, 245, 1)
#define PL_DARKRED_COLOR PL_COLOR_RGB(181, 68, 68, 1)
#define PL_SEGMENT_BG_COLOR PL_COLOR_RGB(75, 164, 220, 1)


/** 字体 细、中 **/
#define PL_FONT_LIGHT(FontSize) [UIFont fontWithName:@"Avenir-Light" size:FontSize]
#define PL_FONT_MEDIUM(FontSize) [UIFont fontWithName:@"Avenir-Medium" size:FontSize]


/** 文件夹 **/

// 根文件夹
#define PL_PLAYER_ROOT_FOLDER @"PLPlayerRootFolder"
#define GET_PL_PLAYER_ROOT_FOLDER(folderName) [PL_PLAYER_ROOT_FOLDER stringByAppendingPathComponent:folderName]
// 子文件夹
#define PL_PLAYER_LIVING_LOG  GET_PL_PLAYER_ROOT_FOLDER(@"PL_PLAYER_LIVING_LOG")

#define PL_PLAYER_PLAYER_LOG  GET_PL_PLAYER_ROOT_FOLDER(@"PL_PLAYER_PLAYER_LOG")

// 强弱引用
#define PLWeakSelf(type)  __weak typeof(type) weak##type = type;
#define PLStrongSelf(type)  __strong typeof(type) type = weak##type;

# warning PLPlayerKit 播放器核心类
#import <AVFoundation/AVFoundation.h>
#import <qplayer2_core/QPlayerContext.h>
#import <qplayer2_core/RenderView.h>
#import <qplayer2_core/QMediaItemContext.h>
#import <qplayer2_core/QIOSCommon.h>
/** 第三方 **/

// 轻量级布局框架
#import <Masonry/Masonry.h>

#import <Bugly/Bugly.h>
#endif /* QNPublicHeader_h */
