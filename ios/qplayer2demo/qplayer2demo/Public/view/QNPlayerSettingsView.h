//
//  PlayerView.h
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/7.
//

#import <UIKit/UIKit.h>
#import "QNChangePlayerView.h"
#import "QNSpeedPlayerView.h"
NS_ASSUME_NONNULL_BEGIN

@interface QNPlayerSettingsView : UIScrollView <UITextFieldDelegate>




///“设置视频” 悬浮框
///type 当前点击事件对应的button
///startPosition 起播位置
-(instancetype)initChangePlayerViewCallBack:(void (^)(ChangeButtonType type , NSString * startPosition,BOOL selected) )callback;


///设置倍速悬浮框
///type 当前点击事件对应的button
-(instancetype)initSpeedViewCallBack:(void (^)(SpeedUIButtonType type) )back;

///设置悬浮窗默认的配置
///type 当前点击事件对应的button
-(void)setChangeDefault:(ChangeButtonType)type;

///倍速默认的配置
///type 当前点击事件对应的button
-(void)setSpeedDefault:(SpeedUIButtonType)type;

-(void)setPostioTittle:(int)value;
@end

NS_ASSUME_NONNULL_END
