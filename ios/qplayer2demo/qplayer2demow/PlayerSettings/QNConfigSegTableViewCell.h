//
//  QNConfigSegTableViewCell.h
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/27.
//  Copyright © 2017年 0dayZh. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QNClassModel.h"

@interface QNConfigSegTableViewCell : UITableViewCell
@property (nonatomic, strong) UILabel *configLabel;
@property (nonatomic, strong) UISegmentedControl *segmentControl;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, assign) NSInteger index;


/**
 为 QNConfigSegTableViewCell 传递数据并显示
 
 @param configureModel 数据模型
 */

- (void)configureSegmentCellWithConfigureModel:(PLConfigureModel *)configureModel;

/**
 返回 QNConfigSegTableViewCell 的实时高度
 
 @param string 数据
 @return QNConfigSegTableViewCell 的实时高度
 */
+ (CGFloat)configureSegmentCellHeightWithString:(NSString *)string;
@end
