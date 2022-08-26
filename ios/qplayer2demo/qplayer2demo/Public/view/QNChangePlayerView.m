//
//  StretchingPlayerView2.m
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/6.
//

#import "QNChangePlayerView.h"

@implementation QNChangePlayerView{
    
    NSMutableArray * buttonArray;
    NSMutableArray * titleArray;
//    NSArray * buttonTypeArray;
    NSMutableArray * labelArray;
    UIImage * selectedImage;
    UIImage * notSelectedImage;
    UIColor * selectedColor;
    UIColor * notSelectedColor;
    UIFont * font;
    UILabel *titleLabel;
}
-(instancetype)initWithFrame:(CGRect)frame backgroudColor:(UIColor*)color{
    self = [super initWithFrame:frame];
    if (self) {
        buttonArray = [NSMutableArray array];
        labelArray = [NSMutableArray array];
        titleArray = [NSMutableArray array];
        selectedImage = [UIImage imageNamed:@"selected"];
        notSelectedImage = [UIImage imageNamed:@"notSelected"];
        notSelectedColor = [UIColor whiteColor];
        selectedColor = [UIColor redColor];
        font = [UIFont systemFontOfSize:12.0f];
        self.backgroundColor = color;
        
    }
    return self;
}
-(void)setTitleLabelText:(NSString *)text frame:(CGRect)frame textColor:(UIColor *)textColor{
    if (titleLabel) {
        titleLabel.frame = frame;
        titleLabel.text = text;
        titleLabel.textColor =textColor;
    }
    else{
        
        titleLabel = [[UILabel alloc]initWithFrame:frame];
        titleLabel.text = text;
        titleLabel.textColor =textColor;
        [self addSubview:titleLabel];
    }
}
-(void)setDefault:(ChangeButtonType)type{
    for (UIButton *btn in buttonArray) {
        if (btn.tag == type) {
            [btn setImage:selectedImage forState:UIControlStateNormal];
            btn.selected = YES;
        }
        else{
            [btn setImage:notSelectedImage forState:UIControlStateNormal];
            btn.selected = NO;
        }
    }
    for (UILabel *lab in labelArray) {
        if (lab.tag == type) {
            lab.textColor = selectedColor;
            
        }
        else{
            lab.textColor = notSelectedColor;
        }
    }
}
-(void)addButtonText:(NSString *)text frame:(CGRect)frame type:(ChangeButtonType)type target:(id)target selector:(SEL)selector selectorTag:(SEL)selectorTag{
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(frame.origin.x, frame.origin.y, frame.size.height, frame.size.height)];
    UILabel *lab = [[UILabel alloc] initWithFrame:CGRectMake(btn.frame.size.width +btn.frame.origin.x + 3, frame.origin.y, frame.size.width - frame.size.height -6, frame.size.height)];
    bool isExist = false;
    
    for (UIButton *arrbtn in buttonArray) {
        if (arrbtn.tag == type) {
            btn = arrbtn;
            isExist = true;
            break;
        }
    }
    if (isExist) {
        for (UILabel *arrlab in labelArray) {
            if(arrlab.tag == type){
                lab = arrlab;
                break;
            }
        }
    }
    
    [btn setBackgroundColor:[UIColor clearColor]];
    [btn setImage:notSelectedImage forState:UIControlStateNormal];
    btn.selected = NO;
    btn.tag = type;
    [btn addTarget:self action:@selector(Click:) forControlEvents:UIControlEventTouchUpInside];
    [btn addTarget:target action:selector forControlEvents:UIControlEventTouchUpInside];
    lab.backgroundColor = [UIColor clearColor];
    lab.text = text;
    lab.textColor = notSelectedColor;
    lab.tag = type;
    lab.font = font;
    lab.userInteractionEnabled =YES;
    UITapGestureRecognizer *tag = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(GestureClick:)];
    [tag addTarget:target action:selectorTag];
    
    if (!isExist) {
        [lab addGestureRecognizer:tag];
        
        [buttonArray addObject:btn];
        [labelArray addObject:lab];
        [self addSubview:btn];
        [self addSubview:lab];
    }
}
-(void)GestureClick:(UITapGestureRecognizer *)tap{

    for (int i = 0; i < buttonArray.count; i++) {
        UIButton *midBtn = buttonArray[i];
        UILabel *midlab = labelArray[i];
        if (midBtn.tag == tap.view.tag) {
            if ((ChangeButtonType)midBtn.tag == UIButtonTypeSEIData || (ChangeButtonType)midBtn.tag == UIButtonTypeAuthentication || (ChangeButtonType)midBtn.tag == UIButtonTypeBackgroundPlay) {
                if (midBtn.selected) {
                    midBtn.selected = NO;
                    [midBtn setImage:notSelectedImage forState:UIControlStateNormal];
                    midlab.textColor = notSelectedColor;
                }else{
                    midBtn.selected = YES;
                    [midBtn setImage:selectedImage forState:UIControlStateNormal];
                    midlab.textColor = selectedColor;
                }
                return;
            }
            else{
                
                [midBtn setImage:selectedImage forState:UIControlStateNormal];
                midlab.textColor = selectedColor;
                midBtn.selected = YES;
            }
        }
        else{
            [midBtn setImage:notSelectedImage forState:UIControlStateNormal];
            midlab.textColor = notSelectedColor;
            midBtn.selected = NO;
        }
    }
}
-(void)Click:(UIButton *)btn{
    for (int i = 0; i < buttonArray.count; i++) {
        UIButton *midBtn = buttonArray[i];
        UILabel *midlab = labelArray[i];
        if (midBtn.tag == btn.tag) {
            if ((ChangeButtonType)btn.tag == UIButtonTypeSEIData || (ChangeButtonType)btn.tag == UIButtonTypeAuthentication || (ChangeButtonType)btn.tag == UIButtonTypeBackgroundPlay) {
                if (midBtn.selected) {
                    midBtn.selected = NO;
                    [midBtn setImage:notSelectedImage forState:UIControlStateNormal];
                    midlab.textColor = notSelectedColor;
                }else{
                    midBtn.selected = YES;
                    [midBtn setImage:selectedImage forState:UIControlStateNormal];
                    midlab.textColor = selectedColor;
                }
                return;
            }

            [midBtn setImage:selectedImage forState:UIControlStateNormal];
            midlab.textColor = selectedColor;
            midBtn.selected = YES;
        }
        else{
            [midBtn setImage:notSelectedImage forState:UIControlStateNormal];
            midlab.textColor = notSelectedColor;
            midBtn.selected = NO;
        }
    }
}
-(void)setButtonFrame:(CGRect)frame type:(ChangeButtonType)type{
    BOOL success = false;
    for (UIButton *btn in buttonArray) {
        if (btn.tag == type) {
            success = true;
            btn.frame = CGRectMake(frame.origin.x, frame.origin.y, frame.size.height, frame.size.height);
        }
    }
    for (UILabel *lab in labelArray) {
        if (lab.tag == type) {
            success = true;
            lab.frame = CGRectMake(frame.origin.x + frame.size.height + 3, frame.origin.y, frame.size.width - frame.size.height -3, frame.size.height);
        }
    }
    if (!success) {
        NSLog(@"StretchingPlayerView 设置button失败，不存在改button。");
    }
}
-(void)setButtonTitle:(NSString *)title type:(ChangeButtonType)type{
    BOOL success = false;
    for (UILabel *lab in labelArray) {
        if (lab.tag == type) {
            success = true;
            lab.text = title;
        }
    }
    if (!success) {
        NSLog(@"StretchingPlayerView 设置button失败，不存在改button。");
    }
}
-(void)setButtonFont:(UIFont *)myfont{
    font = myfont;
    for (UILabel  *lab in labelArray) {
        lab.font = myfont;
    }
}
-(void)setButtonNotSelectedTitleColor:(UIColor *)titleColor{

    notSelectedColor = titleColor;
    for (UILabel *lab in labelArray) {
        lab.textColor = notSelectedColor;
    }
}

-(void)setButtonSelectedTitleColor:(UIColor *)titleColor{
    selectedColor = titleColor;
}


-(void)deleteButton:(ChangeButtonType)type{
    BOOL success = false;
    NSMutableArray *arr = [NSMutableArray array];
    NSMutableArray *arrlab = [NSMutableArray array];
    for (int i =0 ; i < buttonArray.count; i++) {
        UIButton * btn = buttonArray[i];
        UILabel *lab = labelArray [i];
        if (btn.tag == type) {
            [btn removeFromSuperview];
            [lab removeFromSuperview];
            success = true;
        }else{
            [arr addObject:btn];
            [arrlab addObject:lab];
        }
    }
    
    
    if (!success) {
        NSLog(@"StretchingPlayerView 删除button失败，不存在改button。");
    }else{
        buttonArray = arr;
        labelArray = arrlab;
    }
}



-(void)setButtonNotSelectedImage:(UIImage *)Image{
    notSelectedImage = Image;
    for (UIButton *btn in buttonArray) {
        [btn setImage:notSelectedImage forState:UIControlStateNormal];
    }
}
-(void)setButtonSelectedImage:(UIImage *)Image{
    selectedImage = Image;
}

-(BOOL)getButtonSelected:(ChangeButtonType)type{
    for (UIButton *midBtn in buttonArray) {
        if (midBtn.tag == type) {
            return midBtn.selected;
        }
    }
    return NO;
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
