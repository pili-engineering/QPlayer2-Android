//
//  SpeedPlayerView.h
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/8.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSInteger, SpeedUIButtonType){
    UIButtonTypeFor05 = 0,                    //0.5倍速
    UIButtonTypeFor075,                  //0.75倍速
    UIButtonTypeFor10,                   //1.0倍速
    UIButtonTypeFor125,                  //1.25倍速
    UIButtonTypeFor15,                   //1.5倍速
    UIButtonTypeFor20               //2.0倍速
};
@interface QNSpeedPlayerView : UIView

///初始化
///@param frame 背景view的frame
///@param color 背景view的背景颜色
-(instancetype)initWithFrame:(CGRect)frame backgroudColor:(UIColor*)color;

///添加button
///@param text button后方显示的文本
///@param frame button的frame
///@param type button的类型
///@param target selector所在类
///@param selector 响应事件
-(void)addButtonText:(NSString *)text frame:(CGRect)frame type:(SpeedUIButtonType)type target:(id)target selector:(SEL)selector;


///设置默认被选中的button
///@param type 被选中的button类型
-(void)setDefault:(SpeedUIButtonType)type;
@end

NS_ASSUME_NONNULL_END
