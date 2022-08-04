//
//  QNOptionListView.h
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/29.
//  Copyright © 2017年 0dayZh. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QNClassModel.h"

@protocol QNOptionListViewDelegate<NSObject>
- (void)optionListViewSelectedWithIndex:(NSInteger)index configureModel:(PLConfigureModel *)configureModel classModel:(QNClassModel *)classModel;

@end

@interface QNOptionListView : UIView
@property (nonatomic, weak) id<QNOptionListViewDelegate> delegate;
@property (nonatomic, strong) NSArray *optionsArray;
@property (nonatomic, strong) PLConfigureModel *configureModel;
@property (nonatomic, strong) QNClassModel *classModel;

@property (nonatomic, copy) NSString *listStr;


/**
 QNOptionListView 初始化方法

 @param frame 在父视图上的位置及大小
 @param superView 父视频
 @return QNOptionListView 实例
 */
- (instancetype)initWithFrame:(CGRect)frame optionsArray:(NSArray *)optionsArray superView:(UIView *)superView;
@end
