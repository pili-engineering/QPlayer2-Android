//
//  QNConfigInputTableViewCell.h
//  QPlayerKitDemo
//
//  Created by 孙慕 on 2022/6/9.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QNClassModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface QNConfigInputTableViewCell : UITableViewCell<UITextFieldDelegate>
@property (nonatomic, strong) UILabel *configLabel;
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, assign) NSInteger index;

@property (nonatomic, assign) PLConfigureModel *configureModel;


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

NS_ASSUME_NONNULL_END
