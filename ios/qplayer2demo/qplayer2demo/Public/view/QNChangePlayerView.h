//
//  StretchingPlayerView2.h
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/6.
//

#import <UIKit/UIKit.h>
typedef NS_ENUM(NSInteger, ChangeButtonType){
    UIButtonTypeStreAutomatic = 0,      //自动
    UIButtonTypeStretching,             //拉伸
    UIButtonTypeSpreadOver,             //铺满
    UIButtonType16and9,                 //16:9
    UIButtonType4and3,                  //4:3
    
    UIButtonTypeFilterNone = 100,             //无滤镜
    UIButtonTypeFilterRedAndGreen,      //红/绿滤镜
    UIButtonTypeFilterGreenAndRed,      //绿/红滤镜
    UIButtonTypeFilterBlueAndYellow,    //蓝/黄滤镜
    
    UIButtonTypeDectorAutomatic = 200,        //自动
    UIButtonTypeDectorHard,             //硬解
    UIButtonTypeDectorSoft,             //软解
    
    UIButtonTypeSeekKey = 300,                //关键帧seek
    UIButtonTypeSeekAccurate,           //精准seek
    
    UIButtonTypeActionPlay = 400,             //起播播放
    UIButtonTypeActionPause,            //起播暂停
    
    
    UIButtonTypeSEIData = 500,            //sei
    
    UIButtonTypeAuthentication = 600,            //鉴权
    
    UIButtonTypeBackgroundPlay = 700            //后台播放
    
};
NS_ASSUME_NONNULL_BEGIN

@interface QNChangePlayerView : UIView

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
///@param selectorTag 响应事件
-(void)addButtonText:(NSString *)text frame:(CGRect)frame type:(ChangeButtonType)type target:(id)target selector:(SEL)selector selectorTag:(SEL)selectorTag;

///修改button的frame
///@param frame 修改后的frame
///@param type 需要修改哪一类的button
-(void)setButtonFrame:(CGRect)frame type:(ChangeButtonType)type;


///修改button后方显示的文本
///@param title 修改后的text
///@param type 需要修改哪一类的button
-(void)setButtonTitle:(NSString *)title type:(ChangeButtonType)type;


///修改button后方显示的未被选中文本颜色
///@param titleColor 修改后的文本颜色
-(void)setButtonNotSelectedTitleColor:(UIColor *)titleColor;

///修改button后方显示的被选中的文本颜色
///@param titleColor 修改后的文本颜色
-(void)setButtonSelectedTitleColor:(UIColor *)titleColor;

///删除已添加的button
///@param type 需要删除哪一类的button
-(void)deleteButton:(ChangeButtonType)type;


///添加/修改最上方的标题文本
///@param text 添加/修改后的text
///@param frame label的frame
///@param textColor 字体颜色
-(void)setTitleLabelText:(NSString *)text frame:(CGRect)frame textColor:(UIColor *)textColor;

///设置字体大小
///@param myfont 修改后的font
-(void)setButtonFont:(UIFont *)myfont;

///设置未被选中的图标
///@param Image 未被选中的图标
-(void)setButtonNotSelectedImage:(UIImage *)Image;


///设置被选中的图标
///@param Image 被选中的图标
-(void)setButtonSelectedImage:(UIImage *)Image;

///设置默认被选中的button
///@param type 被选中的button类型
-(void)setDefault:(ChangeButtonType)type;

//获取button是否被选择
///@param type 被选中的button类型
-(BOOL)getButtonSelected:(ChangeButtonType)type;
@end

NS_ASSUME_NONNULL_END
