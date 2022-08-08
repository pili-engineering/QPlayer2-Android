//
//  QNBottomView.m
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/15.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import "QNButtomView.h"
@interface QNButtomView()

@property (nonatomic, strong) UILabel *totalDurationLabel;
@property (nonatomic, strong) UILabel *currentTimeLabel;
@property (nonatomic, strong) NSTimer *durationTimer;
@property (nonatomic, assign) long long totalDuration;
@property (nonatomic, assign) BOOL isLiving;
@property (nonatomic, strong) UIButton *fullScreenButton;
@property (nonatomic, strong) UISlider *prograssSlider;
@property (nonatomic, assign) BOOL isSeeking;

@property (nonatomic, strong) UIButton *playButton;
@property (nonatomic, assign) BOOL shortVideoBool;
@end
@implementation QNButtomView{
    CGFloat playerWidth;
    CGFloat playerHeight;
    CGRect myPlayerFrame;
    float minutes;
    int seconds;
    void (^myCallback) (BOOL selectedState);
    void (^changeScreenSizeCallback) (BOOL selectedState);
}
-(instancetype)initWithFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame isLiving:(BOOL)isLiving{
    self = [super initWithFrame:frame];
    if (self) {
        _shortVideoBool = false;
        self.isSeeking = NO;
        self.isLiving = isLiving;
        myPlayerFrame = playerFrame;
        self.backgroundColor = [UIColor clearColor];
        playerWidth = CGRectGetWidth(playerFrame);
        playerHeight = CGRectGetHeight(playerFrame);
        self.player = player;
        if (self.isLiving) {
            self.totalDuration = 0;
        } else {
            self.totalDuration = self.player.controlHandler.duration;
        }
        minutes = _totalDuration / 60.0;
        seconds = (int)_totalDuration % 60;
        [self addPlayButton];
        [self addTotalDurationLabel];
        [self addCurrentTimeLabel];
        [self addFullScreenButton];
        self.durationTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
    }
    return self;
}
-(instancetype)initWithShortVideoFrame:(CGRect)frame player:(QPlayerContext *)player playerFrame:(CGRect)playerFrame isLiving:(BOOL)isLiving{
    self = [super initWithFrame:frame];
    if (self) {
        self.shortVideoBool = YES;
        self.isSeeking = NO;
        self.isLiving = isLiving;
        myPlayerFrame = playerFrame;
        self.backgroundColor = [UIColor clearColor];
        playerWidth = CGRectGetWidth(playerFrame);
        playerHeight = CGRectGetHeight(playerFrame);
        
        [self addTotalDurationLabel];
        self.player = player;
        if (self.isLiving) {
            self.totalDuration = 0;
        } else {
            self.totalDuration = self.player.controlHandler.duration;
        }
        minutes = _totalDuration / 60.0;
        seconds = (int)_totalDuration % 60;
        [self addPlayButton];
        [self addCurrentTimeLabel];
        self.durationTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
    }
    return self;
}
#pragma mark 添加控件
-(void)addTotalDurationLabel{
    if (_shortVideoBool) {
        
        self.totalDurationLabel = [[UILabel alloc] initWithFrame:CGRectMake(playerWidth - 55, 3, 40, 20)];
    }
    else{
        
        self.totalDurationLabel = [[UILabel alloc] initWithFrame:CGRectMake(playerWidth - 92, 3, 40, 20)];
    }
    self.totalDurationLabel.font = PL_FONT_LIGHT(14);
    self.totalDurationLabel.textColor = [UIColor whiteColor];
    self.totalDurationLabel.textAlignment = NSTextAlignmentRight;
    self.totalDurationLabel.text = [NSString stringWithFormat:@"%02d:%02d", (int)minutes, seconds];
    
    [self addSubview:_totalDurationLabel];
}

-(void)addPlayButton{
    self.playButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 22, 22)];
    [self.playButton setImage:[UIImage imageNamed:@"pl_play"] forState:UIControlStateNormal];
    [self.playButton setImage:[UIImage imageNamed:@"pl_stop"] forState:UIControlStateSelected];
    [self.playButton addTarget:self action:@selector(playButtonClick:) forControlEvents:UIControlEventTouchDown];
    [self addSubview:_playButton];
    
    
    [self addSubview:self.prograssSlider];
}
-(void)addCurrentTimeLabel{
    self.currentTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(36, 3, 40, 20)];
    self.currentTimeLabel.font = PL_FONT_LIGHT(14);
    self.currentTimeLabel.textColor = [UIColor whiteColor];
    self.currentTimeLabel.textAlignment = NSTextAlignmentLeft;
    self.currentTimeLabel.text = [NSString stringWithFormat:@"%02d:%02d", 0, 0];
    [self addSubview:_currentTimeLabel];
}

-(void)addFullScreenButton{
    self.fullScreenButton = [[UIButton alloc] initWithFrame:CGRectMake(playerWidth - 44, 0, 22, 22)];
    [self.fullScreenButton setImage:[UIImage imageNamed:@"pl_fullScreen"] forState:UIControlStateNormal];
    [self.fullScreenButton setImage:[UIImage imageNamed:@"pl_smallScreen"] forState:UIControlStateSelected];
    [self.fullScreenButton addTarget:self action:@selector(changeScreenSize:) forControlEvents:UIControlEventTouchDown];
    [self addSubview:_fullScreenButton];
    
}
- (void)setPlayer:(QPlayerContext *)player {
    _player = player;
    self.playButton.selected = (_player.controlHandler.currentPlayerState == QPLAYERSTATUS_PLAYING);
    if (self.isLiving) {
        self.totalDuration = 0;
    } else {
        self.totalDuration = self.player.controlHandler.duration/1000;
    }
    float minutes = _totalDuration / 60.0;
    int seconds = (int)_totalDuration % 60;
    if (minutes < 60) {
        self.totalDurationLabel.text = [NSString stringWithFormat:@"%02d:%02d", (int)minutes, seconds];
    } else{
        float hours = minutes / 60.0;
        int min = (int)minutes % 60;
        self.totalDurationLabel.text = [NSString stringWithFormat:@"%02d:%02d:%02d", (int)hours, (int)min, seconds];
    }
    self.prograssSlider.maximumValue = _totalDuration;
}

- (UISlider *)prograssSlider {
    if (!_prograssSlider) {
        CGFloat playerWidth = CGRectGetWidth(self.frame);
        if (_shortVideoBool) {
            
            _prograssSlider = [[UISlider alloc] initWithFrame:CGRectMake(76, 3, playerWidth - 126, 20)];
        }
        else{
            
            _prograssSlider = [[UISlider alloc] initWithFrame:CGRectMake(76, 3, playerWidth - 170, 20)];
        }
        _prograssSlider.enabled = !_isLiving;
        [_prograssSlider setThumbImage:[UIImage imageNamed:@"pl_round.png"]forState:UIControlStateNormal];
        _prograssSlider.minimumValue = 0;
        _prograssSlider.maximumValue = 1;
        _prograssSlider.minimumTrackTintColor = [UIColor whiteColor];
        _prograssSlider.maximumTrackTintColor = [UIColor grayColor];
        _prograssSlider.value = 0;
        
        // slider滑动中事件
        [_prograssSlider addTarget:self action:@selector(progressSliderValueChanged:) forControlEvents:UIControlEventValueChanged];
        [_prograssSlider addTarget:self action:@selector(sliderTouchUpDown:) forControlEvents:UIControlEventTouchDown];
        [_prograssSlider addTarget:self action:@selector(sliderTouchUpInside:) forControlEvents:UIControlEventTouchUpInside];
        [_prograssSlider addTarget:self action:@selector(sliderTouchUpCancel:) forControlEvents:UIControlEventTouchCancel];
        
        [_prograssSlider addTarget:self action:@selector(sliderTouchUpOutside:) forControlEvents:UIControlEventTouchUpOutside];
        [_prograssSlider addTarget:self action:@selector(sliderTouchDragExit:) forControlEvents:UIControlEventTouchDragExit];
    }
    return _prograssSlider;
}


#pragma mark 对外接口

- (void)changeFrame:(CGRect)frame isFull:(BOOL)isFull{
    playerWidth = CGRectGetWidth(frame);
    playerHeight = CGRectGetHeight(frame);
    self.totalDurationLabel.frame = CGRectMake(playerWidth - 92, 3, 40, 20);
    self.fullScreenButton.frame = CGRectMake(playerWidth - 44, 0, 22, 22);
    self.prograssSlider.frame = CGRectMake(76, 3, playerWidth - 170, 20);
}

-(void)setFullButtonState:(BOOL)state{
    self.fullScreenButton.selected = state;
}
-(void)setPlayButtonState:(BOOL)state{
    self.playButton.selected = state;
}


-(BOOL)getFullButtonState{
    return self.fullScreenButton.isSelected;
}

-(void)changeScreenSizeButtonClickCallBack:(void (^) (BOOL selectedState))callback{
    changeScreenSizeCallback = callback;
}

-(void)playButtonClickCallBack:(void (^) (BOOL selectedState))callback{
    myCallback = callback;
}


-(void)setPlayState{
    self.playButton.selected = !self.playButton.selected;
    if (self.playButton.selected) {
        [self.player.controlHandler resumeRender];
    } else {
        [self.player.controlHandler pauseRender];
    }
}

-(void)timeDealloc{
    
    [self.durationTimer invalidate];
    self.durationTimer = nil;
}




#pragma mark 按钮点击事件
- (void)changeScreenSize:(UIButton *)button {
    button.selected = !button.selected;
    changeScreenSizeCallback(button.isSelected);
}
- (void)playButtonClick:(UIButton *)button {
    button.selected = !button.selected;

    myCallback(button.isSelected);
    
    if (button.selected) {
        [self.player.controlHandler resumeRender];
//        [self.player play];

    } else {
        [self.player.controlHandler pauseRender];
//        [self.player stop];
    }
}
- (void)progressSliderValueChanged:(UISlider *)slider {
    _isSeeking = YES;
}
- (void)sliderTouchUpDown:(UISlider*)slider {
    _isSeeking = YES;

}
- (void)sliderTouchUpInside:(UISlider*)slider {
    _isSeeking = NO;
    
    [self.player.controlHandler seek:(int)slider.value * 1000];
    NSLog(@"seek --- %d", (int)slider.value * 1000);
}
- (void)sliderTouchUpCancel:(UISlider*)slider {
    _isSeeking = NO;
}

- (void)sliderTouchUpOutside:(UISlider*)slider {
    _isSeeking = NO;
    
    [self.player.controlHandler seek:(int)slider.value * 1000];
    NSLog(@"seek --- %d", (int)slider.value * 1000);
}
- (void)sliderTouchDragExit:(UISlider*)slider {
    _isSeeking = YES;

}
- (void)timerAction:(NSTimer *)timer {
    long long currentSeconds = self.player.controlHandler.currentPosition/1000;
    float currentSecondsDouble = self.player.controlHandler.currentPosition/1000.0;
    long long totalSeconds = self.player.controlHandler.duration/1000;

    if (self.totalDuration != 0 && (currentSeconds == totalSeconds || fabsf(currentSecondsDouble - totalSeconds) <=0.5)) {
        if (!_isLiving) {
            self.prograssSlider.value = self.totalDuration;
            float minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            self.currentTimeLabel.text = [NSString stringWithFormat:@"%02d:%02d", (int)minutes, seconds];
            
        }
    } else{
        if (_isSeeking) {
            return;
        }
        
        float minutes = currentSeconds / 60;
        int seconds = currentSeconds % 60;
        self.currentTimeLabel.text = [NSString stringWithFormat:@"%02d:%02d", (int)minutes, seconds];
        self.prograssSlider.value = currentSeconds;
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
