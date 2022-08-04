//
//  QNConfigInputTableViewCell.m
//  QPlayerKitDemo
//
//  Created by 孙慕 on 2022/6/9.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import "QNConfigInputTableViewCell.h"

@implementation QNConfigInputTableViewCell

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
    _configureModel = configureModel;
    _configLabel.text = configureModel.configuraKey;
    
    [_textField removeFromSuperview];
    UITextField *tf = [[UITextField alloc] init];
    tf.backgroundColor = [UIColor whiteColor];
    tf.tintColor = PL_COLOR_RGB(16, 169, 235, 1);
    tf.keyboardType = UIKeyboardTypeNumberPad;
    tf.delegate = self;
    _textField = tf;
    _textField.text = [NSString stringWithFormat:@"%d",[configureModel.configuraValue[0] intValue]];
    [self.contentView addSubview:_textField];

    CGRect bounds = [configureModel.configuraKey boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 56, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(14) forKey:NSFontAttributeName] context:nil];
    if (bounds.size.height > 30) {
        _configLabel.frame = CGRectMake(28, 15, PL_SCREEN_WIDTH - 56, bounds.size.height);
        _textField.frame = CGRectMake(28, 60 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 30);
        _lineView.frame = CGRectMake(28, 98 + bounds.size.height - 30, PL_SCREEN_WIDTH - 56, 0.5);
    } else{
        _textField.frame = CGRectMake(28, 60, PL_SCREEN_WIDTH - 56, 30);
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


-(BOOL)textFieldShouldReturn:(UITextField *)textField
{

    [_configureModel.configuraValue replaceObjectAtIndex:0 withObject:@([textField.text intValue])];
    
[textField resignFirstResponder]; //回收键盘
return YES;
}

-(void)textFieldDidEndEditing:(UITextField *)textField
{
    [_configureModel.configuraValue replaceObjectAtIndex:0 withObject:@([textField.text intValue])];
}


@end
