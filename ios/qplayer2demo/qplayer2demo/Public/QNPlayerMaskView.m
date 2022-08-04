//
//  QNPlayerMaskView.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/9/28.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#import "QNPlayerMaskView.h"
#import "QNAppDelegate.h"
#import "QDataHandle.h"
typedef NS_ENUM(NSInteger, PLMoveDirectionType)
{
    PLHorizontailDirection,
    PLVerticalDirection
};

@interface QNPlayerMaskView ()
<
UIGestureRecognizerDelegate
>{
    bool isScreenFull;
    CGRect fullFrame;
}

@property (nonatomic, strong) UIView *topButtonView;
@property (nonatomic, strong) UIView *bottomView;

@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UIButton *fullScreenButton;

@property (nonatomic, strong) UIView *volumeView;
@property (nonatomic, strong) UISlider *prograssSlider;
@property (nonatomic, strong) NSTimer *durationTimer;
@property (nonatomic, strong) UIImageView *minVolumeImg;
@property (nonatomic, strong) UIImageView *maxVolumeImg;

@property (strong, nonatomic) UIActivityIndicatorView *activityIndicatorView;

@property (nonatomic, strong) UIView *fastView;
@property (nonatomic, strong) UIProgressView *fastProgressView;
@property (nonatomic, strong) UILabel *fastTimeLabel;
@property (nonatomic, strong) UIImageView *fastImageView;

/** 是否正在拖拽 **/
@property (nonatomic, assign) BOOL isSeeking;

/** slider上次的值 **/
@property (nonatomic, assign) CGFloat sliderLastValue;

@property (nonatomic, assign) long long totalDuration;
@property (nonatomic, assign) float currentTime;
/** 用来保存快进的总时长 **/
@property (nonatomic, assign) CGFloat sumTime;

/** 水平移动或者垂直移动 **/
@property (nonatomic, assign) PLMoveDirectionType moveDirection;

/** 点播当前已播时长 **/
@property (nonatomic, strong) UILabel *currentTimeLabel;
/** 点播总时长 **/
@property (nonatomic, strong) UILabel *totalDurationLabel;

/** 单击 **/
@property (nonatomic, strong) UITapGestureRecognizer *singleTap;
/** 双击 **/
@property (nonatomic, strong) UITapGestureRecognizer *doubleTap;

/** 设置悬浮窗 **/
@property (nonatomic, strong) UIButton *showSettingViewButton;
@property (nonatomic, strong) QNPlayerSettingsView *settingView;

/** 设置倍速悬浮窗 **/
@property (nonatomic, strong) UIButton *showSpeedViewButton;
@property (nonatomic, strong) QNPlayerSettingsView *settingSpeedView;

@end

@implementation QNPlayerMaskView

- (void)dealloc{
    NSLog(@"QNPlayerMaskView dealloc");
    _delegate = nil;
}

#pragma mark - setter

- (void)setCurrentTime:(float)currentTime
{
//    CMTime newTime = self.player.currentTime;
//    newTime.value = newTime.timescale * currentTime;
    [self.player seek:currentTime*1000];
}

- (void)setPlayer:(QPlayer *)player {
    _player = player;
    self.playButton.selected = _player.isPlaying;
    if (self.isLiving) {
        self.totalDuration = 0;
    } else {
        self.totalDuration = self.player.duration/1000;
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

#pragma mark - getter

- (float)currentTime
{
    return self.player.currentPosition/1000;
}

- (UIView *)fastView {
    if (!_fastView) {
        _fastView = [[UIView alloc] init];
        _fastView.backgroundColor = PL_COLOR_RGB(0, 0, 0, 0.8);
        _fastView.layer.cornerRadius = 4;
        _fastView.layer.masksToBounds = YES;
    }
    return _fastView;
}

- (UIImageView *)fastImageView {
    if (!_fastImageView) {
        _fastImageView = [[UIImageView alloc] init];
    }
    return _fastImageView;
}

- (UILabel *)fastTimeLabel {
    if (!_fastTimeLabel) {
        _fastTimeLabel               = [[UILabel alloc] init];
        _fastTimeLabel.textColor     = [UIColor whiteColor];
        _fastTimeLabel.textAlignment = NSTextAlignmentCenter;
        _fastTimeLabel.font          = [UIFont systemFontOfSize:14.0];
    }
    return _fastTimeLabel;
}

- (UIProgressView *)fastProgressView {
    if (!_fastProgressView) {
        _fastProgressView                   = [[UIProgressView alloc] init];
        _fastProgressView.progressTintColor = [UIColor whiteColor];
        _fastProgressView.trackTintColor    = [[UIColor lightGrayColor] colorWithAlphaComponent:0.4];
    }
    return _fastProgressView;
}

- (UISlider *)prograssSlider {
    if (!_prograssSlider) {
        CGFloat playerWidth = CGRectGetWidth(self.frame);
        _prograssSlider = [[UISlider alloc] initWithFrame:CGRectMake(76, 3, playerWidth - 170, 20)];
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
    }
    return _prograssSlider;
}


#pragma mark - basic

- (id)initWithFrame:(CGRect)frame player:(QPlayer *)player isLiving:(BOOL)isLiving{
    if (self = [super initWithFrame:frame]) {
        self.player = player;
        self.isLiving = isLiving;
        self.isSeeking = NO;

        CGFloat playerWidth = CGRectGetWidth(frame);
        CGFloat playerHeight = CGRectGetHeight(frame);
        if (self.isLiving) {
            self.totalDuration = 0;
        } else {
            self.totalDuration = self.player.duration;
        }
        float minutes = _totalDuration / 60.0;
        int seconds = (int)_totalDuration % 60;
        
        self.bottomView = [[UIView alloc] initWithFrame:CGRectMake(8, playerHeight - 28, playerWidth - 16, 28)];
        self.bottomView.backgroundColor = [UIColor clearColor];
        [self addSubview:_bottomView];

        self.totalDurationLabel = [[UILabel alloc] initWithFrame:CGRectMake(playerWidth - 92, 3, 40, 20)];
        self.totalDurationLabel.font = PL_FONT_LIGHT(14);
        self.totalDurationLabel.textColor = [UIColor whiteColor];
        self.totalDurationLabel.textAlignment = NSTextAlignmentRight;
        self.totalDurationLabel.text = [NSString stringWithFormat:@"%02d:%02d", (int)minutes, seconds];
        
        [self.bottomView addSubview:_totalDurationLabel];
        
        self.currentTimeLabel = [[UILabel alloc] initWithFrame:CGRectMake(36, 3, 40, 20)];
        self.currentTimeLabel.font = PL_FONT_LIGHT(14);
        self.currentTimeLabel.textColor = [UIColor whiteColor];
        self.currentTimeLabel.textAlignment = NSTextAlignmentLeft;
        self.currentTimeLabel.text = [NSString stringWithFormat:@"%02d:%02d", 0, 0];
        [self.bottomView addSubview:_currentTimeLabel];
        
        self.playButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 22, 22)];
        [self.playButton setImage:[UIImage imageNamed:@"pl_play"] forState:UIControlStateNormal];
        [self.playButton setImage:[UIImage imageNamed:@"pl_stop"] forState:UIControlStateSelected];
        [self.playButton addTarget:self action:@selector(playButtonClick:) forControlEvents:UIControlEventTouchDown];
        [self.bottomView addSubview:_playButton];
        
        
        [self.bottomView addSubview:self.prograssSlider];

        
        self.fullScreenButton = [[UIButton alloc] initWithFrame:CGRectMake(playerWidth - 44, 0, 22, 22)];
        [self.fullScreenButton setImage:[UIImage imageNamed:@"pl_fullScreen"] forState:UIControlStateNormal];
        [self.fullScreenButton setImage:[UIImage imageNamed:@"pl_smallScreen"] forState:UIControlStateSelected];
        [self.fullScreenButton addTarget:self action:@selector(changeScreenSize:) forControlEvents:UIControlEventTouchDown];
        [self.bottomView addSubview:_fullScreenButton];
        
        
        // 初始化定时器
        self.durationTimer = [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(timerAction:) userInfo:nil repeats:YES];
        
        // 音量调整/快进快退
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panAction:)];
        [self addGestureRecognizer:pan];
        
        if (!isLiving) {
            [self layoutFastView];
            self.fastView.hidden = YES;
        }
        
        self.volumeSlider = [[UISlider alloc] initWithFrame:CGRectMake(20, 0, playerHeight - 120, 18)];
        self.volumeSlider.minimumValue = 0;
        self.volumeSlider.maximumValue = 100;
        self.volumeSlider.minimumTrackTintColor = [UIColor whiteColor];
        self.volumeSlider.maximumTrackTintColor = [UIColor grayColor];
        self.volumeSlider.value = 100;
        [self.volumeSlider setThumbImage:[UIImage imageNamed:@"pl_round.png"] forState:UIControlStateNormal];
        [self.volumeSlider addTarget:self action:@selector(changeVolume:) forControlEvents:UIControlEventValueChanged];
        
        CGFloat ratio = [self receiveComparison];
        // 声音控件
        self.volumeView = [[UIView alloc] initWithFrame:CGRectMake(- 32 * ratio, playerHeight - 101 * ratio, playerHeight - 70, 18)];
        [self.volumeView addSubview:_volumeSlider];
        
        self.minVolumeImg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 18, 18)];
        self.minVolumeImg.image = [UIImage imageNamed:@"pl_minVolume"];
        [self.volumeView addSubview:_minVolumeImg];
        
        self.maxVolumeImg = [[UIImageView alloc] initWithFrame:CGRectMake(playerHeight - 98, 0, 18, 18)];
        self.maxVolumeImg.image = [UIImage imageNamed:@"pl_maxVolume"];
        [self.volumeView addSubview:_maxVolumeImg];
        self.volumeView.transform = CGAffineTransformMakeRotation(- M_PI_2);
        [self addSubview:_volumeView];
        
        [self updateVoulmeSliderImage];
    
        
        self.backButton = [[UIButton alloc] initWithFrame:CGRectMake(5, 6, 44, 44)];
        [self.backButton setImage:[UIImage imageNamed:@"pl_back"] forState:UIControlStateNormal];
        [self.backButton addTarget:self action:@selector(getBackAction:) forControlEvents:UIControlEventTouchDown];
        [self addSubview:_backButton];
        
        self.topButtonView = [[UIView alloc] initWithFrame:CGRectMake(playerWidth - 190, 7, 190, 28)];
        self.topButtonView.backgroundColor = [UIColor clearColor];
        [self addSubview:_topButtonView];
        
        self.settingButton = [[UIButton alloc] initWithFrame:CGRectMake(148, 0, 28, 28)];
        [self.settingButton setImage:[UIImage imageNamed:@"pl_setting"] forState:UIControlStateNormal];
//        [self.topButtonView addSubview:_settingButton];
        
        self.shootButton = [[UIButton alloc] initWithFrame:CGRectMake(111, 0, 28, 28)];
        [self.shootButton setImage:[UIImage imageNamed:@"pl_shoot"] forState:UIControlStateNormal];
//        [self.topButtonView addSubview:_shootButton];
        
        self.clipButton = [[UIButton alloc] initWithFrame:CGRectMake(74, 0, 28, 28)];
        [self.clipButton setImage:[UIImage imageNamed:@"pl_clip"] forState:UIControlStateNormal];
        [self.clipButton setImage:[UIImage imageNamed:@"pl_origin"] forState:UIControlStateSelected];
//        [self.topButtonView addSubview:_clipButton];
        
        self.rotateButton = [[UIButton alloc] initWithFrame:CGRectMake(148, 0, 28, 28)];
        [self.rotateButton setImage:[UIImage imageNamed:@"pl_rotate"] forState:UIControlStateNormal];
//        [self.topButtonView addSubview:_rotateButton];
        
        self.mirrorButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
        [self.mirrorButton setImage:[UIImage imageNamed:@"pl_mirror"] forState:UIControlStateNormal];
//        [self.topButtonView addSubview:_mirrorButton];
        
        NSArray *segmentedArray = [[NSArray alloc]initWithObjects:@"1080p",@"720p",@"640p",@"270p",nil];

        self.qualitySegMc = [[UISegmentedControl alloc]initWithItems:segmentedArray];

        self.qualitySegMc.frame = CGRectMake(playerWidth - 250, 7, 250, 28);

        self.qualitySegMc.selectedSegmentIndex = 0;//设置默认选择项索引

        self.qualitySegMc.tintColor = [UIColor grayColor];
        [self addSubview:_qualitySegMc];
        

        
        [self createGesture];
        
        [self hideInterfaceView];
        
        [self addNotifications];

        // 展示转码的动画
        self.activityIndicatorView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(playerWidth/2 - 20, playerHeight/2 - 20, 40, 40)];
        [self.activityIndicatorView setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleWhiteLarge];
        
        //设置悬浮框
        _showSettingViewButton = [[UIButton alloc] initWithFrame:CGRectMake(playerWidth, 7, 30, 30)];
        _showSettingViewButton.backgroundColor = [UIColor clearColor];
        [_showSettingViewButton setImage:[UIImage imageNamed:@"icon-more"] forState:UIControlStateNormal];
        [_showSettingViewButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_showSettingViewButton addTarget:self action:@selector(ShowSettingViewButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        _showSettingViewButton.hidden = YES;
        [self addSubview:_showSettingViewButton];
        _settingView = [[QNPlayerSettingsView alloc]initChangePlayerViewCallBack:^(ChangeUIButtonType type, NSString * _Nonnull startPosition) {
            if (type < 5) {
                [self.player setRenderRatio:(QPlayerRenderRatio)(type + 1)];
                
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Render ratio" selIndex:(int)type];
            }else if(type < 104){
                [self.player setBlindType:(QPlayerBlindType)(type - 100)];
                [[QDataHandle shareInstance] setSelConfiguraKey:@"色盲模式" selIndex:(int)(type - 100)];
            }else if(type < 204){
                [self.player setDecoderType:(QPlayerDecoderType)(type - 200)];;
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Decoder" selIndex:(int)(type - 200)];
            }else if(type < 302 ){
                [self.player  setSeekMode:(int)(type-300)];
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Seek" selIndex:(int)(type-300)];
            }else if(type < 402){
                [self.player setStartAction:(int)(type-400)];;;
                
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Start Action" selIndex:(int)(type-400)];
            }
            
            if (startPosition && ![startPosition isEqualToString:@""]) {
                int satartPod = [startPosition intValue];
                
                [[QDataHandle shareInstance] setValueConfiguraKey:@"播放起始" selValue:satartPod];
            }
            
        }];
        _settingView.hidden = YES;
        [self addSubview:_settingView];
        
        _showSpeedViewButton = [[UIButton alloc] initWithFrame:CGRectMake(playerWidth, 7, 30, 30)];
        _showSpeedViewButton.backgroundColor = [UIColor clearColor];
        [_showSpeedViewButton setTitle:@"倍速" forState:UIControlStateNormal];
//        [_showSpeedViewButton setFont:[UIFont systemFontOfSize:12.0f]];
        _showSpeedViewButton.titleLabel.font = [UIFont systemFontOfSize:15.0f];
        [_showSpeedViewButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_showSpeedViewButton addTarget:self action:@selector(ShowSpeedViewButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        _showSpeedViewButton.hidden = YES;
        [self addSubview:_showSpeedViewButton];
        _settingSpeedView = [[QNPlayerSettingsView alloc]initSpeedViewCallBack:^(SpeedUIButtonType type) {
            float speed = 1.0;
            switch (type) {
                case UIButtonTypeFor20:
                    speed = 2.0;
                    break;
                case UIButtonTypeFor15:
                    speed = 1.5;
                    break;
                case UIButtonTypeFor125:
                    speed = 1.25;
                    break;
                case UIButtonTypeFor10:
                    speed = 1.0;
                    break;
                case UIButtonTypeFor075:
                    speed = 0.75;
                    break;
                case UIButtonTypeFor05:
                    speed = 0.5;
                    break;
                default:
                    break;
            }
            
            [self.player setSpeed:speed];
            [[QDataHandle shareInstance] setSelConfiguraKey:@"播放速度" selIndex:(int)(type)];
            
        }];
        _settingSpeedView.hidden = YES;
        [self addSubview:_settingSpeedView];
        
        
        // default UI
        for (QNClassModel* model in [QDataHandle shareInstance].playerConfigArray) {
            for (PLConfigureModel* configModel in model.classValue) {
                if ([model.classKey isEqualToString:@"PLPlayerOption"]) {
                    [self configurePlayerWithConfigureModel:configModel classModel:model];;
                }
            }
        }

    }
    return self;
}


- (void)configurePlayerWithConfigureModel:(PLConfigureModel *)configureModel classModel:(QNClassModel *)classModel {
    NSInteger index = [configureModel.selectedNum integerValue];
    
    if ([classModel.classKey isEqualToString:@"PLPlayerOption"]) {
        if ([configureModel.configuraKey containsString:@"播放速度"]) {
            [_settingSpeedView setSpeedDefault:(SpeedUIButtonType)index];
        }

        if ([configureModel.configuraKey containsString:@"播放起始"]){
//            self.startPos = [configureModel.configuraValue[0] intValue];
            [_settingView setPostioTittle:[configureModel.configuraValue[0] intValue]];

        } else if ([configureModel.configuraKey containsString:@"Decoder"]) {
            [_settingView setChangeDefault:(ChangeUIButtonType)(index + 200)];
            
        } else if ([configureModel.configuraKey containsString:@"Seek"]) {
            [_settingView setChangeDefault:(ChangeUIButtonType)(index + 300)];

        } else if ([configureModel.configuraKey containsString:@"Start Action"]) {
            [_settingView setChangeDefault:(ChangeUIButtonType)(index + 400)];
            
        } else if ([configureModel.configuraKey containsString:@"Render ratio"]) {
            [_settingView setChangeDefault:(ChangeUIButtonType)(index)];
            
        } else if ([configureModel.configuraKey containsString:@"色盲模式"]) {
            [_settingView setChangeDefault:(ChangeUIButtonType)(index + 100)];
        }
    }
}



/**
 *  创建手势
 */
- (void)createGesture {
    // 单击 - 操作视图出现/消失
    self.singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showAction)];
    self.singleTap.delegate                = self;
    self.singleTap.numberOfTouchesRequired = 1; //手指数
    self.singleTap.numberOfTapsRequired    = 1;
    [self addGestureRecognizer:self.singleTap];
    
    // 双击(播放/暂停)
    self.doubleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(doubleTapAction:)];
    self.doubleTap.delegate                = self;
    self.doubleTap.numberOfTouchesRequired = 1; //手指数
    self.doubleTap.numberOfTapsRequired    = 2;
    [self addGestureRecognizer:self.doubleTap];
    
    // 解决点击当前view时候响应其他控件事件
    [self.singleTap setDelaysTouchesBegan:YES];
    [self.doubleTap setDelaysTouchesBegan:YES];
    // 双击失败响应单击事件
    [self.singleTap requireGestureRecognizerToFail:self.doubleTap];
}

- (void)layoutFastView {
    [self addSubview:self.fastView];
    [self.fastView addSubview:self.fastImageView];
    [self.fastView addSubview:self.fastTimeLabel];
    [self.fastView addSubview:self.fastProgressView];
    
    [self.fastView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(125);
        make.height.mas_equalTo(80);
        make.center.equalTo(self);
    }];
    
    [self.fastImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_offset(32);
        make.height.mas_offset(32);
        make.top.mas_equalTo(5);
        make.centerX.mas_equalTo(self.fastView.mas_centerX);
    }];
    
    [self.fastTimeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.with.trailing.mas_equalTo(0);
        make.top.mas_equalTo(self.fastImageView.mas_bottom).offset(2);
    }];
    
    [self.fastProgressView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.mas_equalTo(12);
        make.trailing.mas_equalTo(-12);
        make.top.mas_equalTo(self.fastTimeLabel.mas_bottom).offset(10);
    }];
}

- (CGFloat)receiveComparison {
    if (PL_SCREEN_WIDTH == 375) {
        return 1.18;
    }
    else if (PL_SCREEN_WIDTH == 414) {
        return 1.30;
    }
    return 1.0;
}

#pragma mark - 通知

- (void)addNotifications {
    // 监听耳机插入和拔掉通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioRouteChangeListenerCallback:) name:AVAudioSessionRouteChangeNotification object:nil];
}

/**
 *  耳机插入、拔出事件
 */
- (void)audioRouteChangeListenerCallback:(NSNotification*)notification {
    NSDictionary *interuptionDict = notification.userInfo;
    NSInteger routeChangeReason = [[interuptionDict valueForKey:AVAudioSessionRouteChangeReasonKey] integerValue];
    switch (routeChangeReason) {
        case AVAudioSessionRouteChangeReasonNewDeviceAvailable:
            // 耳机插入
            if (self.player.isPlaying) {
                [self.player resume_render];
            }
            break;
            
        case AVAudioSessionRouteChangeReasonOldDeviceUnavailable:
        {
            // 耳机拔掉
            // 拔掉耳机继续播放
            if (self.player.isPlaying) {
                [self.player pause_render];
            }
        }
            break;
            
        case AVAudioSessionRouteChangeReasonCategoryChange:
            // called at start - also when other audio wants to play
            NSLog(@"AVAudioSessionRouteChangeReasonCategoryChange");
            break;
    }
}

#pragma mark - public methods

/**
 更新音量视图标识显示
 */
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

// 加载视频转码的动画
- (void)loadActivityIndicatorView {
//    if ([self.activityIndicatorView isAnimating]) {
//        [self.activityIndicatorView stopAnimating];
//        [self.activityIndicatorView removeFromSuperview];
//    }
    if(!self.activityIndicatorView.isAnimating){
        [self addSubview:self.activityIndicatorView];
        [self.activityIndicatorView startAnimating];
    }
}

// 移除视频转码的动画
- (void)removeActivityIndicatorView {
    if ([self.activityIndicatorView isAnimating]) {
        [self.activityIndicatorView removeFromSuperview];
        [self.activityIndicatorView stopAnimating];
    }
}

#pragma mark - private methods

// 触摸消失
- (void)showAction
{
    self.bottomView.hidden = !self.bottomView.hidden;
    self.topButtonView.hidden = !self.topButtonView.hidden;
    if(isScreenFull){
        
        self.showSettingViewButton.hidden = self.bottomView.hidden;
        self.showSpeedViewButton.hidden = self.bottomView.hidden;
    }else{
        
        self.showSettingViewButton.hidden = YES;
        self.showSpeedViewButton.hidden = YES;
    }
    if (self.qualitySegMc.numberOfSegments >1) {
        self.qualitySegMc.hidden  = !self.qualitySegMc.hidden;
    }


    if (!_fullScreenButton.selected) {
        self.volumeView.hidden = self.bottomView.hidden;
    }

    if (!self.bottomView.hidden) {
        self.backgroundColor = PL_COLOR_RGB(0, 0, 0, 0.3);
        [self hideInterfaceView];
    } else{
        self.backgroundColor = [UIColor clearColor];
    }
    if (self.settingView.hidden == NO) {
        self.settingView.hidden = YES;
    }
    if (self.settingSpeedView.hidden == NO) {
        self.settingSpeedView.hidden = YES;
    }
    
    [self endEditing:YES];
}

/**
 *  双击播放/暂停
 *
 *  @param gesture UITapGestureRecognizer
 */
- (void)doubleTapAction:(UIGestureRecognizer *)gesture {
    self.playButton.selected = !self.playButton.selected;
    if (self.playButton.selected) {
        [self.player resume_render];
    } else {
        [self.player pause_render];
    }

}

// 出现后隔5秒消失
- (void)hideInterfaceView
{
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(dismiss) object:nil];
    [self performSelector:@selector(dismiss) withObject:nil afterDelay:5];
}

- (void)dismiss
{
    if (!_isLiving) {
        self.fastView.hidden = YES;
    }
    self.bottomView.hidden = YES;
    self.topButtonView.hidden = YES;
    self.volumeView.hidden = YES;
    self.qualitySegMc.hidden = YES;
    self.showSettingViewButton.hidden = YES;
    self.showSpeedViewButton.hidden = YES;
    self.backgroundColor = [UIColor clearColor];
}

// 点击播放按钮切换图片 暂停、播放
- (void)playButtonClick:(UIButton *)button {
    button.selected = !button.selected;
    if(self.player.currentPlayerState == QPLAYERSTATUS_COMPLETED){
        if (self.delegate != nil && [self.delegate respondsToSelector:@selector(reOpenPlayPlayerMaskView:)]) {
            [self.delegate reOpenPlayPlayerMaskView:self];
        }
    }
    
    if (button.selected) {
        [self.player resume_render];
//        [self.player play];

    } else {
        [self.player pause_render];
//        [self.player stop];
    }
}

//显示设置界面
-(void)ShowSettingViewButtonClick:(UIButton *)btn{
    if (_settingView) {
        [self showAction];
        btn.hidden = YES;
        _settingView.hidden = NO;
    }
}
-(void)ShowSpeedViewButtonClick:(UIButton *)btn{
    if (self.settingSpeedView) {
        [self showAction];
        btn.hidden = YES;
        self.settingSpeedView.hidden = NO;
    }
}

- (void)timerAction:(NSTimer *)timer {
    long long currentSeconds = self.player.currentPosition/1000;
    long long totalSeconds = self.player.duration/1000;

    if (self.totalDuration != 0 && currentSeconds == totalSeconds) {
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

/**
 *  pan手势事件
 *
 *  @param pan UIPanGestureRecognizer
 */
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
    [self.player setPanoramaViewRotate:rotateX rotateY:rotateY];
    
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

/**
 *  pan垂直移动的方法
 *
 *  @param value 垂直移动值
 */
- (void)verticalMovedWithValue:(float)value
{
    self.volumeSlider.value = self.volumeSlider.value - value/10000;
    [self.player setVolume:(int)self.volumeSlider.value];
    [self updateVoulmeSliderImage];
}

/**
 *  pan 水平移动的方法
 *
 *  @param value 水平移动值
 */
- (void)horizontalMoved:(CGFloat)value {
    // 每次滑动叠加时间
    self.sumTime += value / 200;
    
    // 限定sumTime的范围
    CGFloat totalMovieDuration = (CGFloat)self.player.duration;
    if (self.sumTime > totalMovieDuration) {
        self.sumTime = totalMovieDuration;
    }
    if (self.sumTime < 0) {
        self.sumTime = 0;
    }
    
    BOOL style = NO;
    if (value > 0) {
        style = YES;
    }
    if (value < 0) {
        style = NO;
    }
    if (value == 0) {
        return;
    }
    
    self.isSeeking = YES;
    [self draggedTime:self.sumTime totalTime:totalMovieDuration isForward:style hasPreview:NO];
}

- (void)progressSliderValueChanged:(UISlider *)slider {
    _isSeeking = YES;
}

- (void)sliderTouchUpDown:(UISlider*)slider {
    _isSeeking = YES;

}

- (void)sliderTouchUpInside:(UISlider*)slider {
    _isSeeking = NO;
    
    [self.player seek:(int)slider.value * 1000];
    NSLog(@"seek --- %d", (int)slider.value * 1000);
}

- (void)sliderTouchUpCancel:(UISlider*)slider {
    _isSeeking = NO;
}

/**
 *  从 xx 秒开始播放视频跳转
 *
 *  @param dragedSeconds 视频跳转的秒数
 */
- (void)seekToTime:(NSInteger)dragedSeconds {
    CMTime dragedCMTime = CMTimeMake(dragedSeconds, 1); //kCMTimeZero
    [self.player seek:dragedSeconds * 1000];
    self.fastView.hidden = YES;
}

/**
 更新快进视图数据显示

 @param draggedTime 拖拽变化的时长
 @param totalTime 总时长
 @param forawrd 是否是快进
 @param preview 是否可见
 */
- (void)draggedTime:(NSInteger)draggedTime totalTime:(NSInteger)totalTime isForward:(BOOL)forawrd hasPreview:(BOOL)preview {
    // 快进快退时候停止菊花
    [self removeActivityIndicatorView];
    // 拖拽的时长
    NSInteger proMin = draggedTime / 60;
    NSInteger proSec = draggedTime % 60;
    
    // duration 总时长
    NSInteger durMin = totalTime / 60;
    NSInteger durSec = totalTime % 60;
    
    NSString *currentTimeStr = [NSString stringWithFormat:@"%02zd:%02zd", proMin, proSec];
    NSString *totalTimeStr = [NSString stringWithFormat:@"%02zd:%02zd", durMin, durSec];
    CGFloat  draggedValue = (CGFloat)draggedTime/(CGFloat)totalTime;
    NSString *timeStr = [NSString stringWithFormat:@"%@ / %@", currentTimeStr, totalTimeStr];
    
   
    // 更新slider的值
    self.prograssSlider.value = draggedTime;
    // 更新当前时间
    self.currentTimeLabel.text = currentTimeStr;
    
    if (forawrd) {
        self.fastImageView.image = [UIImage imageNamed:@"pl_forward"];
    } else {
        self.fastImageView.image = [UIImage imageNamed:@"pl_backward"];
    }
    self.fastView.hidden = preview;
    self.fastTimeLabel.text = timeStr;
    self.fastProgressView.progress = draggedValue;
}

- (void)changeVolume:(UISlider *)slider {
    [self.player setVolume:(int)slider.value];
    [self updateVoulmeSliderImage];
    
    NSLog(@"volumeSlider.value======%d",(int)slider.value);
}

// 返回00：00--00:00时间格式的方法
- (NSString *)timeStr:(float)time
{
    int m = time / 60;
    int s = (int)time % 60;
    int M = (int)self.player.currentPosition/1000/ 60;
    int S = (self.player.currentPosition /1000) % 60;
    return [NSString stringWithFormat:@"%02d:%02d / %02d:%02d",m,s,M,S];
}

- (void)changeFrame:(CGRect)frame isFull:(BOOL)isFull {
    CGFloat playerWidth = CGRectGetWidth(frame);
    CGFloat playerHeight = CGRectGetHeight(frame);
    self.bottomView.frame = CGRectMake(8, playerHeight - 28, playerWidth - 16, 28);
    self.topButtonView.frame = CGRectMake(playerWidth - 190, 7, 190, 28);
   
    
    self.totalDurationLabel.frame = CGRectMake(playerWidth - 92, 3, 40, 20);
    self.prograssSlider.frame = CGRectMake(76, 3, playerWidth - 170, 20);
    self.fullScreenButton.frame = CGRectMake(playerWidth - 44, 0, 22, 22);
   
    self.showSettingViewButton.frame = CGRectMake(playerWidth - 100, 8, 25, 30);
    self.settingView.frame = CGRectMake(playerWidth - 390, 0, 390, playerHeight);
    self.showSpeedViewButton.frame = CGRectMake(playerWidth - 170, 8, 40, 30);
    self.settingSpeedView.frame = CGRectMake(playerWidth - 130, 0, 130, playerHeight);
    isScreenFull = isFull;
    if (isFull) {
        self.volumeView.hidden = YES;
        _showSettingViewButton.hidden = NO;
        _showSpeedViewButton.hidden = NO;
        fullFrame = frame;
        self.settingSpeedView.contentSize = CGSizeMake(130, frame.size.height);
        [self.settingSpeedView reloadInputViews];
//        [self.settingView setBackScrollContentSize:CGSizeMake(390, 650)];
    } else{
        if (self.settingView.hidden == NO) {
            self.settingView.hidden =YES;
        }
        if (self.settingSpeedView.hidden == NO) {
            self.settingSpeedView.hidden =YES;
        }
        if (self.showSettingViewButton.hidden == NO) {
            self.showSettingViewButton.hidden =YES;
        }
        if (self.showSpeedViewButton.hidden == NO) {
            self.showSpeedViewButton.hidden =YES;
        }
        self.volumeView.hidden = self.bottomView.hidden;
        _showSettingViewButton.hidden = YES;
        _showSpeedViewButton.hidden = YES;
    }
    self.activityIndicatorView.center = self.player.playerView.center;
}

- (void)willMoveToSuperview:(UIView *)newSuperview {
    if (!newSuperview) {
        [self.durationTimer invalidate];
        self.durationTimer = nil;
    }
}

#pragma mark - 放大／缩小

- (void)changeScreenSize:(UIButton *)button {
    button.selected = !button.selected;
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(playerMaskView:isLandscape:)]) {
        [self.delegate playerMaskView:self isLandscape:button.selected];
    }
    [self changeFrame:self.frame isFull:button.selected];
}

#pragma mark - 返回

- (void)getBackAction:(UIButton *)backButton {
    if (self.player.isPlaying && !self.fullScreenButton.selected) {
        [self.durationTimer invalidate];
        self.durationTimer = nil;
        [self.player stop];
    }
    
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(playerMaskView:didGetBack:)]) {
        [self.delegate playerMaskView:self didGetBack:backButton];
    }
    
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    if (!appDelegate.isFlip) {
        self.fullScreenButton.selected = NO;
        [self changeFrame:self.frame isFull:NO];
    }
}


- (BOOL)gestureRecognizer:(UIGestureRecognizer*)gestureRecognizer shouldReceiveTouch:(UITouch*)touch {

    if([touch.view isKindOfClass:[UISlider class]] || [touch.view isKindOfClass:[QNPlayerSettingsView class]] || [touch.view isKindOfClass:[QNChangePlayerView class]] || [touch.view isKindOfClass:[QNSpeedPlayerView class]]){
        return NO;
    } else {
        return YES;
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
