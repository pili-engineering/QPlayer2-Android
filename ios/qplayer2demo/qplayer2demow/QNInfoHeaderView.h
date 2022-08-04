//
//  QNInfoHeaderView.h
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/11/29.
//  Copyright © 2017年 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QNInfoHeaderView : UIView

/**
 QNInfoHeaderView 初始化方法

 @param topMargin 上边距
 @param titleArray 标题数组
 @param infoArray 信息数组
 @return QNInfoHeaderView 实例
 */
- (id)initWithTopMargin:(CGFloat)topMargin titleArray:(NSArray *)titleArray infoArray:(NSArray *)infoArray;

/**
 刷新 QNInfoHeaderView 的显示信息

 @param infoArray 信息数组
 @return QNInfoHeaderView 实例
 */
- (UIView *)updateInfoWithInfoArray:(NSArray *)infoArray;

@end
