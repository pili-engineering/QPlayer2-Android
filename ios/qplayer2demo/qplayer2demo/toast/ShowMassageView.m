//
//  toastView.m
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/25.
//

#import "ShowMassageView.h"

@implementation ShowMassageView{
    UITextView *textView;
}
-(instancetype)initWithFrame:(CGRect)frame Massage:(NSString *)massage{
    self = [super initWithFrame:frame];
    if (self) {
        float width = 10+massage.length *12;
        
        if (width >= frame.size.width) {
            width = frame.size.width;
        }
        
        self.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y,  width, self.frame.size.height);
        self.backgroundColor = [UIColor colorWithWhite:0.5 alpha:0.7];
        self.layer.cornerRadius =5.0f;
        [self ShowMassage:massage];
    }
    return self;
}

-(void)ShowMassage:(NSString *)Massage{
    
    textView  = [[UITextView alloc]initWithFrame:CGRectMake(0, 0, self.frame.size.width , self.frame.size.height )];
    textView.text = Massage;
    textView.backgroundColor = [UIColor clearColor];
    textView.textColor = [UIColor whiteColor];
    textView.editable = NO;
    [self addSubview:textView];
//    [NSTimer scheduledTimerWithTimeInterval:5 repeats:NO block:^(NSTimer * _Nonnull timer) {
//            [self removeFromSuperview];
//    }];
}


-(void)setFrame:(CGRect)frame{
    textView.bounds = CGRectMake(0, 0,frame.size.width, frame.size.height);
//    self.frame = frame;
    [super setFrame:frame];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
