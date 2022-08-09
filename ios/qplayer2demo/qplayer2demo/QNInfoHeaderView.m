//
//  QNInfoHeaderView.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/11/29.
//  Copyright © 2017年 Aaron. All rights reserved.
//

#import "QNInfoHeaderView.h"

@implementation QNInfoHeaderView

- (id)initWithTopMargin:(CGFloat)topMargin titleArray:(NSArray *)titleArray infoArray:(NSArray *)infoArray {
    self = [super initWithFrame: CGRectMake(0, topMargin, PL_SCREEN_WIDTH, 36 + 26 * titleArray.count)];
    if (self) {
        [self layoutInfoHeaderViewWithTitleArray:titleArray infoArray:infoArray];
    }
    return self;
}

- (void)layoutInfoHeaderViewWithTitleArray:(NSArray *)titleArray infoArray:(NSArray *)infoArray {
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, PL_SCREEN_WIDTH, 36)];
    headerView.backgroundColor = PL_COLOR_RGB(212, 220, 240, 1);
    [self addSubview:headerView];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 5, 179, 26)];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.font = PL_FONT_MEDIUM(14);
    titleLabel.text = @"PLPlayer 点播相关信息";
    [headerView addSubview:titleLabel];
    
    [titleArray enumerateObjectsUsingBlock:^(NSString*str, NSUInteger idx, BOOL *stop) {
        UILabel *hintLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 36 + 26 * idx, PL_SCREEN_WIDTH - 10, 26)];
        hintLabel.textColor = [UIColor colorWithRed:96/255.0 green:111/255.0 blue:168/255.0 alpha:1];
        hintLabel.textAlignment = NSTextAlignmentLeft;
        hintLabel.font = PL_FONT_LIGHT(14);
        hintLabel.text = str;
        hintLabel.textColor = PL_DARK_COLOR;
        hintLabel.tag = 100 + idx;
        CGRect hintBounds = [str boundingRectWithSize:CGSizeMake(10000, 26) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_LIGHT(14) forKey:NSFontAttributeName] context:nil];
        hintLabel.frame = CGRectMake(5, 36 + 26 * idx, CGRectGetWidth(hintBounds), 26);
        [self addSubview:hintLabel];
        
        UILabel *infoLabel = [[UILabel alloc] initWithFrame:CGRectMake(10 + CGRectGetWidth(hintBounds), 36 + 26 * idx, PL_SCREEN_WIDTH - 10 - CGRectGetWidth(hintBounds), 26)];
        infoLabel.textColor = [UIColor colorWithRed:96/255.0 green:111/255.0 blue:168/255.0 alpha:1];
        infoLabel.textAlignment = NSTextAlignmentLeft;
        infoLabel.font = PL_FONT_LIGHT(14);
        infoLabel.text = infoArray[idx];
        infoLabel.textColor = PL_SELECTED_BLUE;
        infoLabel.tag = 10 + idx;
        [self addSubview:infoLabel];
    }];
}

- (UIView *)updateInfoWithInfoArray:(NSArray *)infoArray {
    for (UILabel *label in self.subviews) {
        if (label.tag >= 10 && label.tag < 100) {
            NSInteger index = label.tag - 10;
            label.text = infoArray[index];
        }
    }
    return self;
}

@end
