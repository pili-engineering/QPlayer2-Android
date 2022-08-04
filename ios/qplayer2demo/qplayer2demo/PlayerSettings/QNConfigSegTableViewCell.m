//
//  QNConfigSegTableViewCell.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/27.
//  Copyright © 2017年 0dayZh. All rights reserved.
//

#import "QNConfigSegTableViewCell.h"

@implementation QNConfigSegTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        _configLabel = [[UILabel alloc] initWithFrame:CGRectMake(28, 15, PL_SCREEN_WIDTH - 56, 30)];
        _configLabel.numberOfLines = 0;
        _configLabel.textAlignment = NSTextAlignmentLeft;
        _configLabel.font = PL_FONT_LIGHT(14);
        [self.contentView addSubview:_configLabel];
        
        _lineView = [[UIView alloc] initWithFrame:CGRectMake(28, 98, PL_SCREEN_WIDTH - 56, 0.5)];
        _lineView.backgroundColor = PL_LINE_COLOR;
        [self.contentView addSubview:_lineView];
    }
    return self;
}

- (void)configureSegmentCellWithConfigureModel:(PLConfigureModel *)configureModel {
    _configLabel.text = configureModel.configuraKey;
    
    [_segmentControl removeFromSuperview];
    UISegmentedControl *seg = [[UISegmentedControl alloc] initWithItems:configureModel.configuraValue];
    seg.backgroundColor = [UIColor whiteColor];
    seg.tintColor = PL_COLOR_RGB(16, 169, 235, 1);
    NSInteger index = [configureModel.selectedNum integerValue];
    seg.selectedSegmentIndex = index;
    _segmentControl = seg;
    [self.contentView addSubview:_segmentControl];

    CGRect bounds = [configureModel.configuraKey boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 56, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(14) forKey:NSFontAttributeName] context:nil];
    if (bounds.size.height > 30) {
        _configLabel.frame = CGRectMake(28, 15, PL_SCREEN_WIDTH - 56, bounds.size.height);
        _segmentControl.frame = CGRectMake(28, 60 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 30);
        _lineView.frame = CGRectMake(28, 98 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 0.5);
    } else{
        _segmentControl.frame = CGRectMake(28, 60, PL_SCREEN_WIDTH - 56, 30);
    }
}

+ (CGFloat)configureSegmentCellHeightWithString:(NSString *)string {
    CGRect bounds = [string boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 56, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(14) forKey:NSFontAttributeName] context:nil];
    if (bounds.size.height > 30) {
        return 99 + bounds.size.height - 30;
    } else{
        return 99;
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
