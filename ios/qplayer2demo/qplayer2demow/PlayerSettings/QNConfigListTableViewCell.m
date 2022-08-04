//
//  QNConfigListTableViewCell.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/27.
//  Copyright © 2017年 0dayZh. All rights reserved.
//

#import "QNConfigListTableViewCell.h"

@implementation QNConfigListTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        _configLabel = [[UILabel alloc] initWithFrame:CGRectMake(28, 15, PL_SCREEN_WIDTH - 56, 30)];
        _configLabel.numberOfLines = 0;
        _configLabel.textAlignment = NSTextAlignmentLeft;
        _configLabel.font = PL_FONT_LIGHT(14);
        [self.contentView addSubview:_configLabel];
        
        _listButton = [[UIButton alloc] initWithFrame:CGRectMake(28, 60, PL_SCREEN_WIDTH - 56, 30)];
        _listButton.layer.cornerRadius =4;
        _listButton.layer.borderColor = PL_COLOR_RGB(16, 169, 235, 1).CGColor;
        _listButton.layer.borderWidth = 0.5;
        _listButton.backgroundColor = [UIColor whiteColor];
        _listButton.titleLabel.font = PL_FONT_MEDIUM(13);
        [_listButton setTitleColor:PL_COLOR_RGB(16, 169, 235, 1) forState:UIControlStateNormal];
        _listButton.titleLabel.textAlignment = NSTextAlignmentLeft;
        [self.contentView addSubview:_listButton];
        
        _lineView = [[UIView alloc] initWithFrame:CGRectMake(28, 98, PL_SCREEN_WIDTH - 56, 0.5)];
        _lineView.backgroundColor = PL_LINE_COLOR;
        [self.contentView addSubview:_lineView];
    }
    return self;
}

- (void)configureListArrayCellWithConfigureModel:(PLConfigureModel *)configureModel {
    _configLabel.text = configureModel.configuraKey;
    NSInteger index = [configureModel.selectedNum integerValue];
    [_listButton setTitle:configureModel.configuraValue[index] forState:UIControlStateNormal];

    CGRect bounds = [configureModel.configuraKey boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 56, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(14) forKey:NSFontAttributeName] context:nil];
    if (bounds.size.height > 30) {
        _configLabel.frame = CGRectMake(28, 15, PL_SCREEN_WIDTH - 40, bounds.size.height);
        _listButton.frame = CGRectMake(28, 60 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 30);
        _lineView.frame = CGRectMake(28, 98 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 0.5);
    }
}

+ (CGFloat)configureListArrayCellHeightWithString:(NSString *)string {
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
