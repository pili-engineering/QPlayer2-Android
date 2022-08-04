//
//  PLCellPlayerTableViewCell.h
//  PLPlayerKitCellDemo
//
//  Created by 冯文秀 on 2018/3/12.
//  Copyright © 2018年 Hera. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QNPlayerModel.h"

@interface PLCellPlayerTableViewCell : UITableViewCell

@property (nonatomic, copy) NSString *url;
@property (nonatomic, strong) QPlayer *player;
@property (nonatomic, strong) UILabel *URLLabel;
@property (nonatomic, strong) UILabel *stateLabel;
@property (nonatomic, strong) UIView *playerView;
@property (nonatomic, strong) QNPlayerModel *model;

@end
