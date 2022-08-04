//
//  QNURLListTableViewCell.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/10/11.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#import "QNURLListTableViewCell.h"

@implementation QNURLListTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.contentView.backgroundColor = PL_LINE_COLOR;
        
        self.cellBgView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, PL_SCREEN_WIDTH, 34)];
        self.cellBgView.backgroundColor = PL_BACKGROUND_COLOR;
        [self.contentView addSubview:_cellBgView];
        
        self.numberLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 2, 60, 30)];
        self.numberLabel.font = PL_FONT_LIGHT(13);
        self.numberLabel.textColor = PL_DARKRED_COLOR;
        self.numberLabel.textAlignment = NSTextAlignmentLeft;
        [self.cellBgView addSubview:_numberLabel];
        
        self.urlLabel = [[UILabel alloc] initWithFrame:CGRectMake(65, 2, PL_SCREEN_WIDTH - 41, 30)];
        self.urlLabel.numberOfLines = 0;
        self.urlLabel.font = PL_FONT_LIGHT(14);
        self.urlLabel.textColor = PL_DARK_COLOR;
        self.urlLabel.textAlignment = NSTextAlignmentLeft;
        [self.cellBgView addSubview:_urlLabel];
        
        self.deleteButton = [[UIButton alloc] initWithFrame:CGRectMake(PL_SCREEN_WIDTH - 31, 5, 26, 24)];
        self.deleteButton.userInteractionEnabled = YES;
        [self.deleteButton setImage:[UIImage imageNamed:@"pl_delete"] forState:UIControlStateNormal];
        [self.cellBgView addSubview:_deleteButton];
    }
    return self;
}

- (void)configureListURLString:(NSString *)urlString index:(NSInteger)index{
    self.numberLabel.text = [NSString stringWithFormat:@"No.%ld", index + 1];
    CGRect numberBounds = [self.numberLabel.text boundingRectWithSize:CGSizeMake(10000, 30) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(13) forKey:NSFontAttributeName] context:nil];
    self.urlLabel.text = urlString;
    CGRect bounds = [self.urlLabel.text boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 46 - numberBounds.size.width, 10000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_MEDIUM(14) forKey:NSFontAttributeName] context:nil];
    if (bounds.size.height > 30) {
        self.urlLabel.frame = CGRectMake(10 + numberBounds.size.width, 2, PL_SCREEN_WIDTH - 46 - numberBounds.size.width, bounds.size.height);
        self.numberLabel.frame = CGRectMake(5, bounds.size.height/2 - 13, numberBounds.size.width, 30);
        self.deleteButton.frame = CGRectMake(PL_SCREEN_WIDTH - 31, bounds.size.height/2 - 10, 26, 24);
        self.cellBgView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, bounds.size.height + 4);
    } else{
        self.urlLabel.frame = CGRectMake(10 + numberBounds.size.width, 2, PL_SCREEN_WIDTH - 46 - numberBounds.size.width, 30);
        self.numberLabel.frame = CGRectMake(5, 2, numberBounds.size.width, 30);
        self.deleteButton.frame = CGRectMake(PL_SCREEN_WIDTH - 31, 5, 26, 24);
        self.cellBgView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, 34);
    }
}

+ (CGFloat)configureListCellHeightWithURLString:(NSString *)urlString index:(NSInteger)index {
    NSString *numberString = [NSString stringWithFormat:@"No.%ld", index];
    CGRect numberBounds = [numberString boundingRectWithSize:CGSizeMake(10000, 30) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(13) forKey:NSFontAttributeName] context:nil];
    
    CGRect bounds = [urlString boundingRectWithSize:CGSizeMake(PL_SCREEN_WIDTH - 46 - numberBounds.size.width, 10000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_MEDIUM(14) forKey:NSFontAttributeName] context:nil];
    
    if (bounds.size.height > 30) {
        return bounds.size.height + 4.5;
    } else{
        return 34.5;
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
