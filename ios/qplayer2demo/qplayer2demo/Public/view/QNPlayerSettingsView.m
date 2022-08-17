//
//  PlayerView.m
//  QPlay2-wang
//
//  Created by 王声禄 on 2022/7/7.
//

#import "QNPlayerSettingsView.h"
#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height
@implementation QNPlayerSettingsView{
    UIScrollView *backScrollView;
    UITextField *position;
    NSMutableArray *buttonArray;
    NSString *startPosition;   //起播位置
    QNSpeedPlayerView *speedView;
    QNChangePlayerView *eyeView;
    QNChangePlayerView *actionView;
    QNChangePlayerView *seekView;
    QNChangePlayerView *decoderView;
    QNChangePlayerView *stretchPlayerView;
    void (^changePlayerViewCallback)(ChangeUIButtonType type , NSString * startPosition);
    void (^speedViewCallback)(SpeedUIButtonType type);
}

-(instancetype)initChangePlayerViewCallBack:(void (^)(ChangeUIButtonType type , NSString * startPosition) )back{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(ScreenWidth-390, 0, 390, ScreenHeight);
        self.backgroundColor = [UIColor blackColor];
        self.alpha =0.8;
        startPosition =@"0";
        changePlayerViewCallback = back;
        [self addScrollView:CGRectMake(0, 0, 390, ScreenHeight)];
        
//        self.userInteractionEnabled = YES;
    }
    return self;
}
-(instancetype)initSpeedViewCallBack:(void (^)(SpeedUIButtonType type) )back{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(ScreenWidth-130, 0, 130, ScreenHeight);
        self.contentSize = CGSizeMake(130, ScreenHeight);
        self.backgroundColor = [UIColor blackColor];
        self.alpha =0.8;
        speedViewCallback = back;
        buttonArray = [NSMutableArray array];
        [self addSpeedView];
//        self.userInteractionEnabled = YES;
    }
    return self;
}

-(void)setPostioTittle:(int)value{
    if (position) {
        position.text = [NSString stringWithFormat:@"%d",value];
    }
}


-(void)setSpeedDefault:(SpeedUIButtonType)type{
    switch (type) {
        case UIButtonTypeFor20:
        case UIButtonTypeFor15:
        case UIButtonTypeFor10:
        case UIButtonTypeFor075:
        case UIButtonTypeFor05:
            [speedView setDefault:type];
            break;
            
        default:
            break;
    }
}
-(void)setChangeDefault:(ChangeUIButtonType)type{
    switch (type) {
        case UIButtonTypeStreAutomatic:
        case UIButtonTypeStretching:
        case UIButtonTypeSpreadOver:
        case UIButtonType16and9:
        case UIButtonType4and3:
            [stretchPlayerView setDefault:type];
            break;
        case UIButtonTypeFilterNone:
        case UIButtonTypeFilterGreenAndRed:
        case UIButtonTypeFilterBlueAndYellow:
        case UIButtonTypeFilterRedAndGreen:
            [eyeView setDefault:type];
            break;
        case UIButtonTypeActionPlay:
        case UIButtonTypeActionPause:
            [actionView setDefault:type];
            break;
        case UIButtonTypeSeekAccurate:
        case UIButtonTypeSeekKey:
            [seekView setDefault:type];
            break;
        case UIButtonTypeDectorHard:
        case UIButtonTypeDectorSoft:
        case UIButtonTypeDectorAutomatic:
            [decoderView setDefault:type];
            break;
        default:
            NSLog(@"设置出错");
            break;
    }
}
-(void)addSpeedView{
    speedView = [[QNSpeedPlayerView alloc]initWithFrame:CGRectMake(0, 0, 100, ScreenWidth) backgroudColor:[UIColor clearColor]];
    [speedView addButtonText:@"2.0x" frame:CGRectMake((speedView.frame.size.width-100)/2, 50, 100, 50) type:UIButtonTypeFor20 target:self selector:@selector(SpeedButtonClick:)];
    [speedView addButtonText:@"1.5x" frame:CGRectMake((speedView.frame.size.width-100)/2, (speedView.frame.size.height -100)/6+50, 100, 50) type:UIButtonTypeFor15 target:self selector:@selector(SpeedButtonClick:)];
    [speedView addButtonText:@"1.25x" frame:CGRectMake((speedView.frame.size.width-100)/2, (speedView.frame.size.height -100)*2/6+50, 100, 50) type:UIButtonTypeFor125 target:self selector:@selector(SpeedButtonClick:)];
    [speedView addButtonText:@"1.0x" frame:CGRectMake((speedView.frame.size.width-100)/2, (speedView.frame.size.height -100)*3/6+50, 100, 50) type:UIButtonTypeFor10 target:self selector:@selector(SpeedButtonClick:)];
    [speedView addButtonText:@"0.75x" frame:CGRectMake((speedView.frame.size.width-100)/2, (speedView.frame.size.height -100)*4/6+50, 100, 50) type:UIButtonTypeFor075 target:self selector:@selector(SpeedButtonClick:)];
    [speedView addButtonText:@"0.5x" frame:CGRectMake((speedView.frame.size.width-100)/2, (speedView.frame.size.height -100)*5/6+50, 100, 50) type:UIButtonTypeFor05 target:self selector:@selector(SpeedButtonClick:)];
    [speedView setDefault:UIButtonTypeFor10];
    [self addSubview:speedView];
    
}


-(void)SpeedButtonClick:(UIButton*)btn{

    if(speedViewCallback){
        speedViewCallback(btn.tag);
    }
    
}
-(void)addScrollView:(CGRect)frame{
//    self = [[UIScrollView alloc]initWithFrame:frame];
    self.contentSize = CGSizeMake(frame.size.width, 650);
//    self.backgroundColor = [UIColor clearColor];
    self.userInteractionEnabled = YES;
    self.scrollEnabled = YES;
//    self.delegate = self;
//    [self addSubview:backScrollView];
    
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(5, 5, frame.size.width-10, 30)];
    titleLabel.text = @"切换下一集生效设置";
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.font = [UIFont systemFontOfSize:15.0f];
    [self addSubview:titleLabel];
    
    [self addLine:CGRectMake(5, titleLabel.frame.origin.y +titleLabel.frame.size.height+3, self.frame.size.width-10, 3)];
    
    [self addDecoder:CGRectMake(0, 45, self.frame.size.width, 90)];
    
    [self addLine:CGRectMake(5, 138, self.frame.size.width-10, 2)];
    
    [self addSeek:CGRectMake(0, 145, self.frame.size.width, 90)];
    
    [self addLine:CGRectMake(5, 238, self.frame.size.width-10, 2)];
    
    [self addAction:CGRectMake(0, 245, self.frame.size.width, 90)];
    
    [self addLine:CGRectMake(5, 338, self.frame.size.width, 2)];
    
    [self addPositionTexfield: CGRectMake(0, 345, self.frame.size.width - 150, 70)];
    
    UILabel *nowLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 430, self.frame.size.width, 30)];
    nowLabel.text = @"立即生效设置";
    nowLabel.textColor = [UIColor whiteColor];
    nowLabel.backgroundColor = [UIColor clearColor];
    [self addSubview:nowLabel];
    
    [self addLine:CGRectMake(5, 463, self.frame.size.width, 2)];
    
    [self addRender:CGRectMake(0, 465, 350, 90)];
    
    [self addLine:CGRectMake(5, 558, self.frame.size.width, 2)];
    
    [self addFilter:CGRectMake(0, 565, 350, 90)];
}


-(void)addPositionTexfield:(CGRect)frame{
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(frame.origin.x+5, frame.origin.y+5, frame.size.width, 30)];
    titleLabel.text = @"起播位置(毫秒)";
    titleLabel.textColor = [UIColor whiteColor];
    [self addSubview:titleLabel];
    
    
    position = [[UITextField alloc]initWithFrame:CGRectMake(frame.origin.x+5, titleLabel.frame.origin.y + titleLabel.frame.size.height +5, frame.size.width, 30)];
//    position.placeholder = @"0";
    position.text = @"0";
    position.textColor = [UIColor whiteColor];
    position.delegate  = self;
    position.keyboardType = UIKeyboardTypeNumberPad;
    position.backgroundColor = [UIColor clearColor];
    position.clearButtonMode = UITextFieldViewModeWhileEditing;
    [self addSubview:position];
    
    [self addLine:CGRectMake(position.frame.origin.x, position.frame.origin.y+position.frame.size.height +1, position.frame.size.width, 1)];
    
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (changePlayerViewCallback) {
        changePlayerViewCallback(nil,position.text);
    }
    [textField resignFirstResponder]; //回收键盘
return YES;
}

-(void)textFieldDidEndEditing:(UITextField *)textField
{
    if (changePlayerViewCallback) {
        changePlayerViewCallback(nil,position.text);
    }

}


-(void)addFilter:(CGRect)frame{
    eyeView = [[QNChangePlayerView alloc]initWithFrame:frame backgroudColor:[UIColor clearColor]];
    [eyeView setTitleLabelText:@"色觉变化" frame:CGRectMake(10, 10, 120, 30) textColor:[UIColor whiteColor]];
    [eyeView addButtonText:@"无滤镜" frame:CGRectMake(10, 50, 90, 20) type:UIButtonTypeFilterNone target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [eyeView addButtonText:@"红/绿滤镜" frame:CGRectMake(105, 50, 90, 20) type:UIButtonTypeFilterRedAndGreen target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [eyeView addButtonText:@"绿/红滤镜" frame:CGRectMake(200, 50, 90, 20) type:UIButtonTypeFilterGreenAndRed target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [eyeView addButtonText:@"蓝/黄滤镜" frame:CGRectMake(295, 50, 90, 20) type:UIButtonTypeFilterBlueAndYellow target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [eyeView setDefault:UIButtonTypeFilterNone];
    [self addSubview:eyeView];
}
-(void)addAction:(CGRect)frame{
    actionView = [[QNChangePlayerView alloc]initWithFrame:frame backgroudColor:[UIColor clearColor]];
    [actionView setTitleLabelText:@"Start Seek" frame:CGRectMake(10, 10, 120, 30) textColor:[UIColor whiteColor]];
    [actionView addButtonText:@"起播播放" frame:CGRectMake(10, 50, 100, 20) type:UIButtonTypeActionPlay target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [actionView addButtonText:@"起播暂停" frame:CGRectMake(225, 50, 100, 20) type:UIButtonTypeActionPause target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [actionView setDefault:UIButtonTypeActionPlay];
    [self addSubview:actionView];
}

-(void)addSeek:(CGRect)frame{
    seekView = [[QNChangePlayerView alloc]initWithFrame:frame backgroudColor:[UIColor clearColor]];
    [seekView setTitleLabelText:@"Seek" frame:CGRectMake(10, 10, 120, 30) textColor:[UIColor whiteColor]];
    [seekView addButtonText:@"关键帧Seek" frame:CGRectMake(10, 50, 100, 20) type:UIButtonTypeSeekKey target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [seekView addButtonText:@"精准Seek" frame:CGRectMake(225, 50, 100, 20) type:UIButtonTypeSeekAccurate target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [seekView setDefault:UIButtonTypeSeekKey];
    [self addSubview:seekView];
}
-(void)addLine:(CGRect)frame {
    UIView *line = [[UIView alloc]initWithFrame:frame];
    line.backgroundColor = [UIColor whiteColor];
    [self addSubview:line];
}
-(void)addDecoder:(CGRect)frame{
    decoderView = [[QNChangePlayerView alloc]initWithFrame:frame backgroudColor:[UIColor clearColor]];
    [decoderView setTitleLabelText:@"Decoder" frame:CGRectMake(10, 10, 120, 30) textColor:[UIColor whiteColor]];
    [decoderView addButtonText:@"自动" frame:CGRectMake(10, 50, 60, 20) type:UIButtonTypeDectorAutomatic target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [decoderView addButtonText:@"软解" frame:CGRectMake(125, 50, 60, 20) type:UIButtonTypeDectorSoft target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [decoderView addButtonText:@"硬解" frame:CGRectMake(240, 50, 60, 20) type:UIButtonTypeDectorHard target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [decoderView setDefault:UIButtonTypeDectorAutomatic];
    [self addSubview:decoderView];
}
-(void)addRender:(CGRect)frame{
    
    stretchPlayerView = [[QNChangePlayerView alloc]initWithFrame:frame backgroudColor:[UIColor clearColor]];
    [stretchPlayerView setTitleLabelText:@"Render ratio" frame:CGRectMake(10, 10, 120, 30) textColor:[UIColor whiteColor]];
    [stretchPlayerView addButtonText:@"自动" frame:CGRectMake(10, 50, 60, 20) type:UIButtonTypeStreAutomatic target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [stretchPlayerView addButtonText:@"拉伸" frame:CGRectMake(80, 50, 60, 20) type:UIButtonTypeStretching target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [stretchPlayerView addButtonText:@"铺满" frame:CGRectMake(150, 50, 60, 20) type:UIButtonTypeSpreadOver target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [stretchPlayerView addButtonText:@"16:9" frame:CGRectMake(220, 50, 60, 20) type:UIButtonType16and9 target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [stretchPlayerView addButtonText:@"4:3" frame:CGRectMake(290, 50, 60, 20) type:UIButtonType4and3 target:self selector:@selector(changePlayerViewClick:) selectorTag:@selector(changePlayerViewClickTag:)];
    [stretchPlayerView setDefault:UIButtonTypeStreAutomatic];
    [self addSubview:stretchPlayerView];
}
-(void)changePlayerViewClick:(UIButton *)btn{
    if (changePlayerViewCallback) {
        changePlayerViewCallback(btn.tag,position.text);
    }
}
-(void)changePlayerViewClickTag:(UIGestureRecognizer *)btn{
    if (changePlayerViewCallback) {
        changePlayerViewCallback(btn.view.tag,position.text);
    }
}
@end
