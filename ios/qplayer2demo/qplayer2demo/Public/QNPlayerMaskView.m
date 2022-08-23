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
#import "QNHeadsetNotification.h"
#import "QNButtonView.h"
typedef NS_ENUM(NSInteger, PLMoveDirectionType)
{
    PLHorizontailDirection,
    PLVerticalDirection
};

@interface QNPlayerMaskView ()
<
UIGestureRecognizerDelegate,
QIPlayerQualityListener,
QIPlayerAuthenticationListener
>{
    bool isScreenFull;
    CGRect fullFrame;
}

@property (nonatomic, strong) QNButtonView *buttonView;

@property (nonatomic, strong) UIButton *backButton;


@property (strong, nonatomic) UIActivityIndicatorView *activityIndicatorView;

@property (nonatomic, strong) UIView *fastView;
@property (nonatomic, strong) UIProgressView *fastProgressView;
@property (nonatomic, strong) UILabel *fastTimeLabel;
@property (nonatomic, strong) UIImageView *fastImageView;


/** slider上次的值 **/
@property (nonatomic, assign) CGFloat sliderLastValue;

@property (nonatomic, assign) float currentTime;
/** 用来保存快进的总时长 **/
@property (nonatomic, assign) CGFloat sumTime;

/** 水平移动或者垂直移动 **/
@property (nonatomic, assign) PLMoveDirectionType moveDirection;

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

@property (nonatomic, assign) QPlayerDecoder decoderType;
@property (nonatomic, assign) BOOL seeking;
@property (nonatomic, strong) RenderView *myRenderView;
@end

@implementation QNPlayerMaskView



#pragma mark - basic

- (id)initWithFrame:(CGRect)frame player:(QPlayerContext *)player isLiving:(BOOL)isLiving renderView:(RenderView *)view{
    if (self = [super initWithFrame:frame]) {
        self.player = player;
        
        [self.player.controlHandler addPlayerQualityListener:self];
        [self.player.controlHandler addPlayerAuthenticationListener:self];
        self.isLiving = isLiving;
        self.myRenderView = view;
        CGFloat playerWidth = CGRectGetWidth(frame);
        CGFloat playerHeight = CGRectGetHeight(frame);
        
        self.buttonView = [[QNButtonView alloc]initWithFrame:CGRectMake(8, playerHeight - 28, playerWidth - 16, 28) player:player playerFrame:frame isLiving:isLiving];
        
        [self addSubview:_buttonView];
        
        [self.buttonView playButtonClickCallBack:^(BOOL selectedState) {
            if(self.player.controlHandler.currentPlayerState == QPLAYER_STATE_COMPLETED){
                if (self.delegate != nil && [self.delegate respondsToSelector:@selector(reOpenPlayPlayerMaskView:)]) {
                    [self.delegate reOpenPlayPlayerMaskView:self];
                }
            }
        }];
        
        [self.buttonView changeScreenSizeButtonClickCallBack:^(BOOL selectedState) {
            if (self.delegate != nil && [self.delegate respondsToSelector:@selector(playerMaskView:isLandscape:)]) {
                [self.delegate playerMaskView:self isLandscape:selectedState];
            }
            [self changeFrame:self.frame isFull:selectedState];
        }];
        [self.buttonView sliderStartCallBack:^(BOOL seeking) {
                    self.seeking = seeking;
        }];
        [self.buttonView sliderEndCallBack:^(BOOL seeking) {
                    self.seeking = seeking;
        }];
        // 音量调整/快进快退
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panAction:)];
        [self addGestureRecognizer:pan];
        
        if (!isLiving) {
            [self layoutFastView];
            self.fastView.hidden = YES;
        }
        
        CGFloat ratio = [self receiveComparison];
        // 声音控件

        
        self.backButton = [[UIButton alloc] initWithFrame:CGRectMake(5, 6, 44, 44)];
        [self.backButton setImage:[UIImage imageNamed:@"pl_back"] forState:UIControlStateNormal];
        [self.backButton addTarget:self action:@selector(getBackAction:) forControlEvents:UIControlEventTouchDown];
        [self addSubview:_backButton];
        
        
        NSArray *segmentedArray = [[NSArray alloc]initWithObjects:@"1080p",@"720p",@"480p",@"270p",nil];

        self.qualitySegMc = [[UISegmentedControl alloc]initWithItems:segmentedArray];

        self.qualitySegMc.frame = CGRectMake(playerWidth - 250, 7, 250, 28);

        self.qualitySegMc.selectedSegmentIndex = 0;//设置默认选择项索引
        self.qualitySegMc.tintColor = [UIColor grayColor];
        [self addSubview:_qualitySegMc];
        

        
        
        [self createGesture];
        
        [self hideInterfaceView];
        
        [[QNHeadsetNotification alloc]addNotificationsPlayer:player];

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
        _settingView = [[QNPlayerSettingsView alloc]initChangePlayerViewCallBack:^(ChangeButtonType type, NSString * _Nonnull startPosition,BOOL selected) {
            if (type < 5) {
                [self.player.renderHandler setRenderRatio:(QPlayerRenderRatio)(type + 1)];
                
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Render ratio" selIndex:(int)type];
            }else if(type < 104){
                [self.player.renderHandler setBlindType:(QPlayerBlind)(type - 100)];
                [[QDataHandle shareInstance] setSelConfiguraKey:@"色盲模式" selIndex:(int)(type - 100)];
            }else if(type < 204){
                
                self.decoderType = (QPlayerDecoder)(type - 200);
                [self.player.controlHandler setDecoderType:(QPlayerDecoder)(type - 200)];;
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Decoder" selIndex:(int)(type - 200)];
            }else if(type < 302 ){
                [self.player.controlHandler  setSeekMode:(QPlayerSeek)(type-300)];
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Seek" selIndex:(int)(type-300)];
            }else if(type < 402){
                [self.player.controlHandler setStartAction:(QPlayerStart)(type-400)];;;
                
                [[QDataHandle shareInstance] setSelConfiguraKey:@"Start Action" selIndex:(int)(type-400)];
            }
            else if(type == 500){
                
                [self.player.controlHandler setSEIEnable: selected];
                if (selected) {
                    
                    [[QDataHandle shareInstance] setSelConfiguraKey:@"SEI" selIndex:0];
                }else{
                    
                    [[QDataHandle shareInstance] setSelConfiguraKey:@"SEI" selIndex:1];
                }
            }
            else if(type == 600){
                if (selected) {
                    [self.player.controlHandler forceAuthenticationFromNetwork];
                    [[QDataHandle shareInstance] setSelConfiguraKey:@"鉴权" selIndex:0];
                }else{
                    
                    [[QDataHandle shareInstance] setSelConfiguraKey:@"鉴权" selIndex:1];
                }
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
            
            [self.player.controlHandler setSpeed:speed];
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
            self.decoderType = (QPlayerDecoder)index;
            [_settingView setChangeDefault:(ChangeButtonType)(index + 200)];
            
        } else if ([configureModel.configuraKey containsString:@"Seek"]) {
            [_settingView setChangeDefault:(ChangeButtonType)(index + 300)];

        } else if ([configureModel.configuraKey containsString:@"Start Action"]) {
            [_settingView setChangeDefault:(ChangeButtonType)(index + 400)];
            
        } else if ([configureModel.configuraKey containsString:@"Render ratio"]) {
            [_settingView setChangeDefault:(ChangeButtonType)(index)];
            
        } else if ([configureModel.configuraKey containsString:@"色盲模式"]) {
            [_settingView setChangeDefault:(ChangeButtonType)(index + 100)];
        }//默认开启
        else if ([configureModel.configuraKey containsString:@"SEI"]) {
            if (index == 0) {
                
                [_settingView setChangeDefault:UIButtonTypeSEIData];
            }
            else{
                
            }
        }//默认开启
        else if ([configureModel.configuraKey containsString:@"鉴权"]) {
            if (index == 0) {
                
                [_settingView setChangeDefault:UIButtonTypeAuthentication];
            }
            else{
                
            }
       }
        
    }
}



///**
// *  创建手势
// */
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

- (void)dealloc{
    NSLog(@"QNPlayerMaskView dealloc");
    _delegate = nil;
}

#pragma mark - setter

- (void)setCurrentTime:(float)currentTime
{
//    CMTime newTime = self.player.currentTime;
//    newTime.value = newTime.timescale * currentTime;
    [self.player.controlHandler seek:currentTime*1000];
}


- (void)setPlayer:(QPlayerContext *)player {
    self.buttonView.player = player;
    _player = player;
}
#pragma mark - playerListenerDelegate

-(void)onQualitySwitchRetryLater:(QPlayerContext *)context usertype:(NSString *)usertype urlType:(QPlayerURLType)urlType{

    NSInteger nums = self.qualitySegMc.numberOfSegments;
    for (int i = 0; i < nums; i++) {
        if ([[self.qualitySegMc titleForSegmentAtIndex:i] isEqual:[NSString stringWithFormat:@"%ld%@",(long)[self.player.controlHandler getSwitchingQuality:usertype urlType:urlType],@"p"]]) {
            self.qualitySegMc.selectedSegmentIndex = i;
            break;
        }
    }
}

-(void)onAuthenticationFailed:(QPlayerContext *)context error:(QPlayerAuthenticationErrorType)error{
    if (error == QPLAYER_AUTHENTICATION_ERROR_TYPE_AET_NO_BLIND_AUTH) {
        [_settingView setChangeDefault:UIButtonTypeFilterNone];
    }
}
#pragma mark - getter

- (float)currentTime
{
    return self.player.controlHandler.currentPosition/1000;
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


#pragma mark - public methods

// 加载视频转码的动画
- (void)loadActivityIndicatorView {

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
-(void)setPlayButtonState:(BOOL)state{
    [self.buttonView setPlayButtonState:state];
}

-(QPlayerDecoder)getDecoderType{
    return self.decoderType;
}
#pragma mark - private methods

-(void)setIsLiving:(BOOL)isLiving{
    self.buttonView.isLiving = isLiving;
}
// 触摸消失
- (void)showAction
{
    self.buttonView.hidden = !self.buttonView.hidden;
    if(isScreenFull){
        
        self.showSettingViewButton.hidden = self.buttonView.hidden;
        self.showSpeedViewButton.hidden = self.buttonView.hidden;
    }else{
        
        self.showSettingViewButton.hidden = YES;
        self.showSpeedViewButton.hidden = YES;
    }
    if (self.qualitySegMc.numberOfSegments >1) {
        self.qualitySegMc.hidden  = !self.qualitySegMc.hidden;
    }




    if (!self.buttonView.hidden) {
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

    [self.buttonView setPlayState];

}

// 出现后隔5秒消失
- (void)hideInterfaceView
{
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(dismiss) object:nil];
    [self performSelector:@selector(dismiss) withObject:nil afterDelay:5];
    
}

- (void)dismiss
{
    if (self.seeking) {
        [self hideInterfaceView];
    }
    else{
        if (!_isLiving) {
            self.fastView.hidden = YES;
        }
        self.buttonView.hidden = YES;
        self.qualitySegMc.hidden = YES;
        self.showSettingViewButton.hidden = YES;
        self.showSpeedViewButton.hidden = YES;
        self.backgroundColor = [UIColor clearColor];
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
    
    [self.player.renderHandler setPanoramaViewRotate:rotateX rotateY:rotateY];

}


- (void)changeFrame:(CGRect)frame isFull:(BOOL)isFull {
    CGFloat playerWidth = CGRectGetWidth(frame);
    CGFloat playerHeight = CGRectGetHeight(frame);

   
    self.showSettingViewButton.frame = CGRectMake(playerWidth - 100, 8, 25, 30);
    self.settingView.frame = CGRectMake(playerWidth - 390, 0, 390, playerHeight);
    self.showSpeedViewButton.frame = CGRectMake(playerWidth - 170, 8, 40, 30);
    self.settingSpeedView.frame = CGRectMake(playerWidth - 130, 0, 130, playerHeight);
    isScreenFull = isFull;
    if (isFull) {
        self.buttonView.frame = CGRectMake(8, playerHeight - 55, playerWidth - 16, 28);
        
        [self.buttonView changeFrame:frame isFull:isFull];
        _showSettingViewButton.hidden = NO;
        _showSpeedViewButton.hidden = NO;
        fullFrame = frame;
        self.settingSpeedView.contentSize = CGSizeMake(130, frame.size.height);
        [self.settingSpeedView reloadInputViews];
    } else{
        self.buttonView.frame = CGRectMake(8, playerHeight - 28, playerWidth - 16, 28);
        
        [self.buttonView changeFrame:frame isFull:isFull];
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
        _showSettingViewButton.hidden = YES;
        _showSpeedViewButton.hidden = YES;
    }
//    self.activityIndicatorView.center = self.player.controlHandler.playerView.center;
    self.activityIndicatorView.center = self.myRenderView.center;
}





#pragma mark - 返回

- (void)getBackAction:(UIButton *)backButton {

    
    if (self.delegate != nil && [self.delegate respondsToSelector:@selector(playerMaskView:didGetBack:)]) {
        [self.delegate playerMaskView:self didGetBack:backButton];
    }
    
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    if (!appDelegate.isFlip) {
        [self.buttonView setFullButtonState:NO];
        [self changeFrame:self.frame isFull:NO];
    }
}


- (BOOL)gestureRecognizer:(UIGestureRecognizer*)gestureRecognizer shouldReceiveTouch:(UITouch*)touch {

    if([touch.view isKindOfClass:[UISlider class]] || [touch.view isKindOfClass:[QNPlayerSettingsView class]] || [touch.view isKindOfClass:[QNChangePlayerView class]] || [touch.view isKindOfClass:[QNSpeedPlayerView class]] || [touch.view isKindOfClass:[UILabel class]]){
        return NO;
    } else {
        return YES;
    }
    
}

@end
