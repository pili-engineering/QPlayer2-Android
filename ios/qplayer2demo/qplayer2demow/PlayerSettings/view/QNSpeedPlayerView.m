//
//  SpeedPlayerView.m
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/8.
//

#import "QNSpeedPlayerView.h"

@implementation QNSpeedPlayerView{
    UIColor *selectColor;
    UIColor *notSelectColor;
    NSMutableArray *buttonArray;
}
-(instancetype)initWithFrame:(CGRect)frame backgroudColor:(UIColor*)color{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = color;
        selectColor = [UIColor redColor];
        notSelectColor = [UIColor whiteColor];
        buttonArray = [NSMutableArray array];
    }
    return self;
}

-(void)addButtonText:(NSString *)text frame:(CGRect)frame type:(SpeedUIButtonType)type target:(id)target selector:(SEL)selector{
    UIButton *btn = [[UIButton alloc]initWithFrame:frame];
    [btn setTitle:text forState:UIControlStateNormal];
    [btn setTitleColor:notSelectColor forState:UIControlStateNormal];
    btn.backgroundColor = [UIColor clearColor];
    btn.tag = type;
    
    [btn addTarget:self action:@selector(click:) forControlEvents:UIControlEventTouchUpInside];
    [btn addTarget:target action:selector forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:btn];
    [buttonArray addObject:btn];
}
-(void)click:(UIButton *)btn{
    for (UIButton *midbtn in buttonArray) {
        if (midbtn.tag == btn.tag) {
            [midbtn setTitleColor:selectColor forState:UIControlStateNormal];
        }
        else{
            [midbtn setTitleColor:notSelectColor forState:UIControlStateNormal];
        }
    }
}

-(void)setDefault:(SpeedUIButtonType)type{
    for (UIButton *midbtn in buttonArray) {
        if (midbtn.tag == type) {
            [midbtn setTitleColor:selectColor forState:UIControlStateNormal];
        }
        else{
            [midbtn setTitleColor:notSelectColor forState:UIControlStateNormal];
        }
    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
