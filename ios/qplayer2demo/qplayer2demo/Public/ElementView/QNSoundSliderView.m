//
//  SoundSliderView.m
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/14.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import "QNSoundSliderView.h"
@interface QNSoundSliderView()

//@property (nonatomic, weak) QPlayer *player;
@property (nonatomic, weak) QPlayerContext *player;
@property (nonatomic, strong) UISlider *volumeSlider;
@property (nonatomic, strong) UIImageView *minVolumeImg;
@property (nonatomic, strong) UIImageView *maxVolumeImg;

@end

@implementation QNSoundSliderView{
    CGFloat playerWidth;
    CGFloat playerHeight;
}
-(instancetype)initWithFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame{
    self = [super initWithFrame:frame];
    if (self) {
        
        self.transform = CGAffineTransformMakeRotation(- M_PI_2);
        playerWidth = CGRectGetWidth(playerFrame);
        playerHeight = CGRectGetHeight(playerFrame);
        self.player = player;
        
        [self addVolumeSlider];
        [self addVolumeSliderImage];
        [self updateVoulmeSliderImage];
    }
    return self;
}
#pragma mark 添加控件
-(void)addVolumeSlider{
    self.volumeSlider = [[UISlider alloc] initWithFrame:CGRectMake(20, 0, playerHeight - 120, 18)];
    self.volumeSlider.minimumValue = 0;
    self.volumeSlider.maximumValue = 100;
    self.volumeSlider.minimumTrackTintColor = [UIColor whiteColor];
    self.volumeSlider.maximumTrackTintColor = [UIColor grayColor];
    self.volumeSlider.value = 100;
    [self.volumeSlider setThumbImage:[UIImage imageNamed:@"pl_round.png"] forState:UIControlStateNormal];
    [self.volumeSlider addTarget:self action:@selector(changeVolume:) forControlEvents:UIControlEventValueChanged];
    [self addSubview:_volumeSlider];
}
-(void)addVolumeSliderImage{
    self.minVolumeImg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 18, 18)];
    self.minVolumeImg.image = [UIImage imageNamed:@"pl_minVolume"];
    [self addSubview:_minVolumeImg];
    
    self.maxVolumeImg = [[UIImageView alloc] initWithFrame:CGRectMake(playerHeight - 98, 0, 18, 18)];
    self.maxVolumeImg.image = [UIImage imageNamed:@"pl_maxVolume"];
    [self addSubview:_maxVolumeImg];
}
#pragma mark 按钮点击事件
//声音Slider
- (void)changeVolume:(UISlider *)slider {
    [self.player.controlHandler setVolume:(int)slider.value];
    [self updateVoulmeSliderImage];
    
    NSLog(@"volumeSlider.value======%d",(int)slider.value);
}
- (void)updateVoulmeSliderImage {
    if (self.volumeSlider.value == 0) {
        self.minVolumeImg.image = [UIImage imageNamed:@"pl_min_blue_volume"];
        self.maxVolumeImg.image = [UIImage imageNamed:@"pl_maxVolume"];
    }
    else if (self.volumeSlider.value == 3) {
        self.minVolumeImg.image = [UIImage imageNamed:@"pl_minVolume"];
        self.maxVolumeImg.image = [UIImage imageNamed:@"pl_max_blue_volume"];
    }
    else {
        self.minVolumeImg.image = [UIImage imageNamed:@"pl_minVolume"];
        self.maxVolumeImg.image = [UIImage imageNamed:@"pl_maxVolume"];
    }
}

//手势垂直滑动控制声音
//此处暂时因为冲突舍弃     注：是直接复制的源代码，直接用可能用不了，未经调试
- (void)verticalMovedWithValue:(float)value
{
    self.volumeSlider.value = self.volumeSlider.value - value/10000;
    [self.player.controlHandler setVolume:(int)self.volumeSlider.value];
    [self updateVoulmeSliderImage];
}

- (void)panAction:(UIPanGestureRecognizer *)pan {
    // 根据上次和本次移动的位置，算出一个速率的point
    CGPoint veloctyPoint = [pan velocityInView:self];
//    static int panX = 0;
//    static int panY = 0;
    
    CGPoint locationPoint = [pan locationInView:self];
    CGPoint transPoint = [pan translationInView:self];
    [pan setTranslation:CGPointZero inView:self];
    
    static int rotateY = 0;
    static int rotateX = 0;
    
    rotateY -= transPoint.x;
    rotateX += transPoint.y;
    
    rotateY = rotateY % 360;
    rotateX = rotateX % 360;
    
    NSLog(@"pointx==%d===%d",rotateY,rotateX);
    [self.player.controlHandler setPanoramaViewRotate:rotateX rotateY:rotateY];
    
//    switch (pan.state) {
//        case UIGestureRecognizerStateBegan:{
//            CGFloat x = fabs(veloctyPoint.x);
//            CGFloat y = fabs(veloctyPoint.y);
//            // 水平移动
//            if (x > y) {
//                if (!_isLiving) {
//                    // 取消隐藏
//                    self.moveDirection = PLHorizontailDirection;
//                    // 给sumTime初值
//                    self.sumTime = self.player.currentPosition/1000;
//                }
//            }
//            // 垂直移动
//            else if (x < y) {
//                self.moveDirection = PLVerticalDirection;
//            }
//            break;
//        }
//        case UIGestureRecognizerStateChanged:{
//
//
//            switch (self.moveDirection) {
//                case PLHorizontailDirection:{
////                    if (!_isLiving) {
////                        // 水平移动，x 方向值
////                        [self horizontalMoved:veloctyPoint.x];
////                    }
//
//                   int panY = 0;
//
//                    panY = rotateY + veloctyPoint.x/100;
//                    panY = panY % 360;
//
//                    NSLog(@"horizontalMoved == %d---(%f)",panY,veloctyPoint.x);
//                    if (panY != rotateY) {
//                        rotateY = panY;
//                        [self.player setPanoramaViewRotate:rotateX rotateY:rotateY];
//                    }
//
//                    break;
//                }
//                case PLVerticalDirection:{
//                    // 垂直移动，y 方向值
////                    [self verticalMovedWithValue:veloctyPoint.y];
//
//                    int panX = 0;
//
//
//                    panX = rotateX + veloctyPoint.x/100;
//                    panX = panX % 360;
//
//                    NSLog(@"horizontalMoved == %d---(%f)",panX,veloctyPoint.x);
//                     if (panX != rotateX) {
//                         rotateX = panX;
//                         [self.player setPanoramaViewRotate:rotateX rotateY:rotateY];
//                     }
//                    break;
//                }
//                default:
//                    break;
//            }
//            break;
//        }
//        case UIGestureRecognizerStateEnded:{ // 移动停止
////            self.isSeeking = NO;
////            // 移动结束时，根据判断，到指定位置，避免屏幕跳动
////            switch (self.moveDirection) {
////                case PLHorizontailDirection:{
////                    if (!_isLiving) {
////                        [self seekToTime:self.sumTime];
////                        self.sumTime = 0;
////                    }
////                    break;
////                }
////                case PLVerticalDirection:{
////                    [self performSelector:@selector(dismiss) withObject:self afterDelay:4];
////                    break;
////                }
////                default:
////                    break;
////            }
//            break;
//        }
//        default:
//            break;
//    }
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
