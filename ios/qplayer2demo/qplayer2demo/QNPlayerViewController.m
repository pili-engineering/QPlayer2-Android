//
//  QNPlayerViewController.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/9/18.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#import "QNPlayerViewController.h"
#import "QNScanViewController.h"
#import "QNPlayerConfigViewController.h"

#import "QNAppDelegate.h"

#import "QNPlayerMaskView.h"
#import "QNInfoHeaderView.h"

#import "QNURLListTableViewCell.h"

#import "QNPlayerModel.h"
#import "QDataHandle.h"
#import "QNToastView.h"


#define PL_PLAYER_VIDEO_ROOT_FOLDER @"PLPlayerFloder"
#define GET_PL_PLAYER_VIDEO_FOLDER(folderName) [PL_PLAYER_VIDEO_ROOT_FOLDER stringByAppendingPathComponent:folderName]
#define PL_PLAYER_VIDEO_REVERSER GET_PL_PLAYER_VIDEO_FOLDER(@"PLPlayerCacheFile")

@interface QNPlayerViewController ()
<
//QPlayerDelegate,  /** PLPlayer 代理 **/
UITableViewDelegate,
UITableViewDataSource,
QNPlayerMaskViewDelegate,
PLScanViewControlerDelegate
>

/** PLPlayer 的核心应用类 **/
//@property (nonatomic, strong) QPlayer *player;
/** 播放器蒙版视图 **/
@property (nonatomic, strong) QNPlayerMaskView *maskView;

/** 界面显示的播放信息数组 **/
@property (nonatomic, strong) NSArray *titleArray;

@property (nonatomic, strong) QNInfoHeaderView *infoHeaderView;
@property (nonatomic, strong) UITableView *urlListTableView;

/** 被选中 URL 在列表中的下标 **/
@property (nonatomic, assign) NSInteger selectedIndex;
/** 是否显示 URL 列表 **/
@property (nonatomic, assign) BOOL isPull;

@property (nonatomic, copy) NSString *playerLogFileName;
@property (nonatomic, strong) NSMutableArray *logRecordArray;
/** 无可显示 URL 的提示 **/
@property (nonatomic, strong) UILabel *hintLabel;

@property (nonatomic, strong) NSTimer *durationTimer;
@property (nonatomic, assign) BOOL isFlip;
@property (nonatomic, assign) CGFloat topSpace;

/** 分栏选择 **/
@property (nonatomic, strong) UISegmentedControl *segment;
@property (nonatomic, assign) BOOL isLiving;
@property (nonatomic, assign) NSInteger modeCount;

@property (nonatomic, strong) NSMutableArray<QNPlayerModel *> *playerModels;

/**toast **/
@property (nonatomic, strong) QNToastView *toastView;
@property (nonatomic, assign) NSString *definition;

@property (nonatomic, strong) QPlayerContext *playerContext;
@end

@implementation QNPlayerViewController

- (void)dealloc {
    NSLog(@"QNPlayerViewController dealloc");
}

- (void)viewWillAppear:(BOOL)animated {
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    if (appDelegate.isFlip) {
        [self.navigationController setNavigationBarHidden:YES animated:NO];
    } else{
        [self.navigationController setNavigationBarHidden:NO animated:NO];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.durationTimer invalidate];
    self.durationTimer = nil;
//    [self.player stop];
    [self.playerContext.controlHandler stop];
}

- (void)onUIApplication:(BOOL)active{
    if (active) {
//        [self.player resume_render];
        [self.playerContext.controlHandler resume_render];
    }else{
//        [self.player pause_render];
        [self.playerContext.controlHandler pause_render];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _playerConfigArray = [QDataHandle shareInstance].playerConfigArray;
    
    NSString *documentsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
    NSString *path = [documentsDir stringByAppendingPathComponent:@"urls.json"];
    
    NSData *data=[[NSData alloc] initWithContentsOfFile:path];
    if (!data) {
        path=[[NSBundle mainBundle] pathForResource:@"urls" ofType:@"json"];
        data=[[NSData alloc] initWithContentsOfFile:path];
    }
    NSArray *urlArray=[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
    
    _playerModels = [NSMutableArray array];
   
    for (NSDictionary *dic in urlArray) {
            QNPlayerModel *modle = [[QNPlayerModel alloc] init];
            [modle setValuesForKeysWithDictionary:dic];
            
        NSMutableArray <QStreamElement*> *streams = [NSMutableArray array];
        for (NSDictionary *elDic in dic[@"streamElements"]) {
            QStreamElement *subModle = [[QStreamElement alloc] init];
            [subModle setValuesForKeysWithDictionary:elDic];
            [streams addObject:subModle];
        }
        
        modle.streamElements = streams;
        [_playerModels addObject:modle];
        
    }

    [self.durationTimer invalidate];
    self.durationTimer = nil;
    self.durationTimer = [NSTimer scheduledTimerWithTimeInterval:0.05f target:self selector:@selector(onTimer:) userInfo:nil repeats:YES];
    // Do any additional setup after loading the view.
    self.navigationController.navigationBar.barTintColor = PL_SEGMENT_BG_COLOR;
    [self.navigationItem setHidesBackButton:YES];
        
    self.modeCount = 0;

    if (PL_HAS_NOTCH) {
        _topSpace = 88;
    } else {
        _topSpace = 64;
    }
    
    // PLPlayer 应用
    [self setUpPlayer:self.playerConfigArray];
    
    [self addPlayerMaskView];

    [self layoutUrlListTableView];
    
    _logRecordArray = [NSMutableArray array];
    
    _toastView = [[QNToastView alloc]initWithFrame:CGRectMake(0, PL_SCREEN_HEIGHT-300, 200, 300)];
    [self.view addSubview:_toastView];
    //清晰度默认为1080p
    self.definition = @"1080p";
//    [self.player addPlayerStaticCallBackName:@"callback1" CallBack:^(QPlayerStatus state) {
//            NSLog(@"2");
//    }];
//    [self.player removePlayerStaticCallBack:@"213"];
    [self playerContextAllCallBack];
}

#pragma mark - 初始化 PLPlayer

//- (void)config

- (void)setUpPlayer:(NSArray<QNClassModel*>*)models {
    NSMutableArray *configs = [NSMutableArray array];
    
    if (models) {
        configs = [models mutableCopy];
    } else {
        NSUserDefaults *userdafault = [NSUserDefaults standardUserDefaults];
        NSArray *dataArray = [userdafault objectForKey:@"PLPlayer_settings"];
        if (dataArray.count != 0 ) {
            for (NSData *data in dataArray) {
                QNClassModel *classModel = [NSKeyedUnarchiver unarchiveObjectWithData:data];
                [configs addObject:classModel];
            }
        }
    }
    
    NSString *documentsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
//    NSString *path = [documentsDir stringByAppendingPathComponent:@"me"];
    
    QAppInformation *info = [[QAppInformation alloc] init];
    info.mAppId = @"com.qbox.QPlayerKitDemo";
    
//    QPlayer* player = [[QPlayer alloc] initPlayerAppInfo:info storageDir:documentsDir logLevel:LOG_VERBOSE];
//    self.player = player;
//    self.player.mDelagete = self;
//    self.player.playerView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
//    [self.view addSubview:_player.playerView];
    QPlayerContext *player =  [[QPlayerContext alloc]initPlayerAppInfo:info storageDir:documentsDir logLevel:LOG_VERBOSE];
    self.playerContext = player;
    self.playerContext.playerView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
    [self.view addSubview:self.playerContext.playerView];
    
    
    for (QNClassModel* model in configs) {
        for (PLConfigureModel* configModel in model.classValue) {
            if ([model.classKey isEqualToString:@"PLPlayerOption"]) {
                [self configurePlayerWithConfigureModel:configModel classModel:model];;
            }
        }
    }
//    NSString *path = [[NSBundle mainBundle] pathForResource:@"1080_60_5390.mp4" ofType:nil];
//    _playerModels.firstObject.streamElements[0].url = path;
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels.firstObject.streamElements;
    model.is_live = _playerModels.firstObject.is_live;
//    [self.player playMediaModel:model startPos:[[QDataHandle shareInstance] getConfiguraPostion]];
    [self.playerContext.controlHandler playMediaModel:model startPos:[[QDataHandle shareInstance] getConfiguraPostion]];
//    });
//    [self.player playMediaModel:@[_playerModels.firstObject] startPos:0];

}

#pragma mark - PLPlayerDelegate
-(void)playerContextAllCallBack{
    __weak QNPlayerViewController *weakSelf = self;
    [self.playerContext.controlHandler addPlayerStateCallBackName:@"stateChanged" CallBack:^(QPlayerContext *_Nullable context, QPlayerStatus state) {
        [weakSelf stateChanged:state];
        
    }];
    [self.playerContext.controlHandler addPlayerBufferingChangeCallBackName:@"BufferingChange" callBack:^(QPlayerContext *_Nullable context, QNotifyModle * _Nonnull notify) {
            [weakSelf renderBufferingChange:notify];
    }];
    [self.playerContext.controlHandler addPlayerQualityVideoDidChangedCallBackName:@"qualityVideoDidChanged" callBack:^(QPlayerContext *_Nullable context, QNotifyModle * _Nonnull notify, NSInteger oldQuality, NSInteger newQuality, NSInteger qualitySerial) {
            [weakSelf qualityVideoDidChanged:notify oldQuality:oldQuality newQuality:newQuality qualitySerial:qualitySerial];
    }];
}

-(void)stateChanged:(QPlayerStatus)state{
    if (state == QPLAYERSTATUS_PREPARE) {
        [self.maskView loadActivityIndicatorView];
        [_toastView addText:@"开始拉视频数据"];
        [_toastView addDecoderType:[self.maskView getDecoderType]];
        [_toastView addText:[NSString stringWithFormat:@"清晰度：%@",self.definition]];
    } else{
//        if (state != PLPlayerStateAutoReconnecting) {
//            [self.maskView removeActivityIndicatorView];
//
//            [self.durationTimer invalidate];
//            self.durationTimer = nil;
//            self.durationTimer = [NSTimer scheduledTimerWithTimeInterval:0.05f target:self selector:@selector(onTimer:) userInfo:nil repeats:YES];
//            [self logPrintWithKey:@"playerVersion" content:playerVersion()];
//            [self logPrintWithKey:@"playerURL" content:[NSString stringWithFormat:@"%@", _player.URL]];
//        }
        if (state == QPLAYERSTATUS_PLAYING) {
//            _maskView.player = _player;
            
            self.maskView.player = self.playerContext;
            [_maskView setPlayButtonState:YES];
            [self showHintViewWithText:@"开始播放器"];
            
            [_toastView addText:@"播放中"];
//            if (_player.metadata) {
//                NSString *content = [NSString stringWithFormat:@"{"];
//                for (NSString *key in _player.metadata) {
//                    content = [content stringByAppendingString:[NSString stringWithFormat:@"%@:%@,", key, _player.metadata[key]]];
//                }
//                content = [content substringToIndex:(content.length - 2)];
//                content = [content stringByAppendingString:@"}"];
//                NSLog(@"_player.metadata ------ %@", content);
//                [self logPrintWithKey:@"playerMetadata" content:content];
//            }
        } else if (state == QPLAYERSTATUS_PAUSED_RENDER || state == QPLAYERSTATUS_STOPPED) {
//            _maskView.playButton.selected = NO;
            
            [_toastView addText:@"暂停渲染或者停止当前播放"];
            [_maskView setPlayButtonState:NO];
        }else if (state == QPLAYERSTATUS_ERROR){
//            [self.durationTimer invalidate];
//            self.durationTimer = nil;
//            _maskView.playButton.selected = NO;
            [_toastView addText:@"播放错误"];
            [_maskView setPlayButtonState:NO];
//            NSString *message;
//            NSString *title;
//            message = @"播放出现错误！";
//            title = @"发生错误";
//
//            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil message:message delegate:nil cancelButtonTitle:title otherButtonTitles:nil];
//            [alertView show];
        }else if (state == QPLAYERSTATUS_COMPLETED){
//            _maskView.playButton.selected = NO;
            
            [_toastView addText:@"播放完成"];
            [_maskView setPlayButtonState:NO];
        }
    }
}
-(void)player:(QPlayer *)player stateChanged:(QPlayerStatus)state{
    
}


/// 播放发生错误的回调
//- (void)player:(QPlayerContext *)player stoppedWithError:(NSError *)error {
//    //    self.maskView.player = _player;
//        self.maskView.player = self.playerContext;
//        [self.maskView removeActivityIndicatorView];
//        [_toastView addText:[NSString stringWithFormat:@"%@",error]];
//    //    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"error info" message:error.description delegate:self cancelButtonTitle:@"知道了" otherButtonTitles:@"重连", nil];
//    //    [alertView show];
//    //    [self.player stop];
//}

/// 点播已缓冲区域的回调
//- (void)player:(nonnull QPlayer *)player loadedTimeRange:(CMTime)timeRange {
//    float durationSeconds = CMTimeGetSeconds(timeRange);
//    NSString *string = [NSString stringWithFormat:@"durationSecods(%0.2f)", durationSeconds];
//}

-(void)renderBufferingChange:(QNotifyModle*)notify{
    if (notify.notify_type == QPLAYER_NOTIFY_RENDER_BUFFERRING_START) {
        [self.maskView loadActivityIndicatorView];
    }
    if (notify.notify_type == QPLAYER_NOTIFY_RENDER_BUFFERRING_END) {
        [self.maskView removeActivityIndicatorView];
    }
}
-(void)player:(QPlayer *)player renderBufferingChange:(QNotifyModle *)notify{

}

-(void)qualityVideoDidChanged:(QNotifyModle *)notify oldQuality:(NSInteger)oldQuality newQuality:(NSInteger)newQuality qualitySerial:(NSInteger)qualitySerial{
    NSString *string = [NSString stringWithFormat:@"清晰度切换成功"];
    [self.toastView addText:string];
}

//清晰度切换成功回调
-(void)player:(QPlayer *)player qualityVideoDidChanged:(QNotifyModle *)notify oldQuality:(NSInteger)oldQuality newQuality:(NSInteger)newQuality qualitySerial:(NSInteger)qualitySerial{

}


///// 即将进入后台播放任务的回调
//- (void)playerWillBeginBackgroundTask:(QPlayer *)player {
//
////    NSLog(@"begin background device orientation - %ld", [UIDevice currentDevice].orientation);
//}

/// 即将结束后台播放状态任务的回调
//- (void)playerWillEndBackgroundTask:(QPlayer *)player {
////    NSLog(@"end background device orientation - %ld", [UIDevice currentDevice].orientation);
//    // 解决部分 iOS 系统退后台后回到前台时，将 orientation 设置为 UIInterfaceOrientationPortrait，与退后台前的方向不符合，导致 playerView 显示不全的问题
//    // 判断实际方向是否与原方向一致，不一致则设置保持一致
//    if (self.isFlip) {
//        [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeRight) forKey:@"orientation"];
//    } else{
//        [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationPortrait) forKey:@"orientation"];
//    }
//}

#pragma mark - 计时器方法

- (void)onTimer:(NSTimer *)timer {
    self.urlListTableView.tableHeaderView = [_infoHeaderView updateInfoWithInfoArray:[self updateInfoArray]];
}

#pragma mark - 更新播放信息数组

- (NSArray *)updateInfoArray {
    NSString *statusStr = [self updatePlayerStatus];
//    NSString *connectTimeStr = [self stringByNSTimerinterval:self.player.connectTime];
    NSString *firstVideoTimeStr = [NSString stringWithFormat:@"%d ms",self.playerContext.firstVideoTime];
//    NSString *widthStr = [NSString stringWithFormat:@"%d", self.player.width];
//    NSString *heightStr = [NSString stringWithFormat:@"%d", self.player.height];
    NSString *renderFPSStr = [NSString stringWithFormat:@"%dfps", self.playerContext.fps];
    NSString *downSpeedStr = [NSString stringWithFormat:@"%.2fkb/s", self.playerContext.downloadSpeed * 1.0/1000];

    NSArray *array = @[statusStr,firstVideoTimeStr,renderFPSStr,downSpeedStr];
//    if (_isLiving) {
//        NSString *videoFPSStr = [NSString stringWithFormat:@"%dfps", self.player.videoFPS];
//        NSString *bitrateStr = [NSString stringWithFormat:@"%.2fkb/s", self.player.videoBitrate];
//        NSMutableArray *mutableArray = [NSMutableArray arrayWithArray:array];
//        [mutableArray addObjectsFromArray:@[videoFPSStr, bitrateStr]];
//        array = [mutableArray copy];
//    } else {
    long bufferPositon = self.playerContext.bufferPostion;
        NSString *fileUnit = @"ms";

        NSString *fileSizeStr = [NSString stringWithFormat:@"%d%@", bufferPositon, fileUnit];
        NSMutableArray *mutableArray = [NSMutableArray arrayWithArray:array];
        [mutableArray addObjectsFromArray:@[fileSizeStr]];
        array = [mutableArray copy];
//    }
    return array;
    return @[];
}

- (NSString *)stringByNSTimerinterval:(NSTimeInterval)interval {
    NSInteger min = interval/60;
    NSInteger sec = (NSInteger)interval%60;
    if (min >= 60) {
        NSInteger hour = min/60;
        NSInteger mins = min%60;
        return [NSString stringWithFormat:@"%02ld:%02ld:%02ld", hour, mins, sec];
    } else if (min > 0 && min < 60) {
        return [NSString stringWithFormat:@"%02ld:%02ld", min, sec];
    } else {
        return [NSString stringWithFormat:@"%lds", sec];
    }
}

- (NSString *)updatePlayerStatus {
    NSDictionary *statusDictionary = @{@(QPLAYERSTATUS_NONE):@"Unknow",
                                       @(QPLAYERSTATUS_INIT):@"init",
                                       @(QPLAYERSTATUS_PREPARE):@"PREPARE",
                                       @(QPLAYERSTATUS_PLAYING):@"Playing",
                                       @(QPLAYERSTATUS_PAUSED):@"Paused",
                                       @(QPLAYERSTATUS_PAUSED_RENDER):@"Paused",
                                       @(QPLAYERSTATUS_STOPPED):@"Stopped",
                                       @(QPLAYERSTATUS_ERROR):@"Error",
                                       @(QPLAYERSTATUS_SEEKING):@"seek",
                                       @(QPLAYERSTATUS_COMPLETED):@"Completed"
                                       };
    return statusDictionary[@(self.playerContext.currentPlayerState)];

}

#pragma mark - 添加点播界面蒙版

- (void)addPlayerMaskView{
    self.maskView = [[QNPlayerMaskView alloc] initWithFrame:CGRectMake(0, 0, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT) player:self.playerContext isLiving:NO];
    self.maskView.center = self.playerContext.playerView.center;
    self.maskView.delegate = self;
    self.maskView.backgroundColor = PL_COLOR_RGB(0, 0, 0, 0.35);
//    [self.maskView.rotateButton addTarget:self action:@selector(rotateButtonAction:) forControlEvents:UIControlEventTouchDown];
    [self.view insertSubview:_maskView aboveSubview:self.playerContext.playerView];
    
    [self.maskView.qualitySegMc addTarget:self action:@selector(qualityAction:) forControlEvents:UIControlEventValueChanged];
}

#pragma mark - QNPlayerMaskView 代理方法

- (void)playerMaskView:(QNPlayerMaskView *)playerMaskView didGetBack:(UIButton *)backButton {
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    if (appDelegate.isFlip) {
        [self forceOrientationLandscape:NO];
        _toastView.frame = CGRectMake(0, PL_SCREEN_HEIGHT-300, 200, 300);
    } else{
        [self.playerContext.controlHandler stop];
 
        [self.durationTimer invalidate];
        self.durationTimer = nil;
        
        self.maskView = nil;
        
//        self.player.mDelagete = nil;
        self.playerContext = nil;
        // 更新日志
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)playerMaskView:(QNPlayerMaskView *)playerMaskView isLandscape:(BOOL)isLandscape {
    [self forceOrientationLandscape:isLandscape];
    if (isLandscape) {
        _toastView.frame = CGRectMake(40, 150, 200, 150);
    }else
    {
        _toastView.frame = CGRectMake(0, PL_SCREEN_HEIGHT-300, 200, 300);
    }
}

-(void)reOpenPlayPlayerMaskView:(QNPlayerMaskView *)playerMaskView{
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels[_selectedIndex].streamElements;
    model.is_live = _playerModels[_selectedIndex].is_live;
    
    [_playerContext.controlHandler playMediaModel:model startPos:[[QDataHandle shareInstance] getConfiguraPostion]];
//    _maskView.playButton.selected = YES;
    [_maskView setPlayButtonState:YES];

}

- (void)forceOrientationLandscape:(BOOL)isLandscape {
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    appDelegate.isFlip = isLandscape;
    [appDelegate application:[UIApplication sharedApplication] supportedInterfaceOrientationsForWindow:self.view.window];
    _isFlip = appDelegate.isFlip;
    if (isLandscape) {
        [self.navigationController setNavigationBarHidden:YES animated:NO];
        [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeRight) forKey:@"orientation"];
        [self.urlListTableView removeFromSuperview];
        self.playerContext.playerView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT);
        self.maskView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT);
    } else {
        [self.navigationController setNavigationBarHidden:NO animated:NO];
        [[UIDevice currentDevice] setValue:@(UIDeviceOrientationPortrait) forKey:@"orientation"];
        [self.view addSubview:_urlListTableView];
        self.playerContext.playerView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
        self.maskView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
    }
    [UIViewController attemptRotationToDeviceOrientation];
}

#pragma mark - 创建  urlListTableView

- (void)layoutUrlListTableView
{
    self.isPull = YES;
    
    self.urlListTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, _topSpace + PLAYER_PORTRAIT_HEIGHT, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT - _topSpace - PLAYER_PORTRAIT_HEIGHT) style:UITableViewStylePlain];
    self.urlListTableView.delegate = self;
    self.urlListTableView.dataSource = self;
    self.urlListTableView.sectionHeaderHeight = 36;
    [self.urlListTableView registerClass:[QNURLListTableViewCell class] forCellReuseIdentifier:@"listCell"];
    self.urlListTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    self.titleArray = @[@"status - PLPlayer 的播放状态 :",@"firstVideoTime - 首开时间 :",@"renderFPS - 播放渲染帧率 :",@"downSpeed - 下载速率(kb/s) :"];
    
        NSMutableArray *mutableArray = [NSMutableArray arrayWithArray:_titleArray];
        [mutableArray addObjectsFromArray:@[@"bufferPostion - 缓存大小 :"]];
        _titleArray = [mutableArray copy];
    self.infoHeaderView = [[QNInfoHeaderView alloc] initWithTopMargin:0 titleArray:_titleArray infoArray:[self updateInfoArray]];
    
    self.urlListTableView.tableHeaderView = _infoHeaderView;
    [self.view addSubview:_urlListTableView];
}


#pragma mark - PLPlayerSettingsVcDelegate

- (void)didCompleteConfiguration:(NSArray<QNClassModel *> *)configs {
    [self setUpPlayer:configs];
}

- (void)configurePlayerWithConfigureModel:(PLConfigureModel *)configureModel classModel:(QNClassModel *)classModel {
    NSInteger index = [configureModel.selectedNum integerValue];
    
    if ([classModel.classKey isEqualToString:@"PLPlayerOption"]) {
        if ([configureModel.configuraKey containsString:@"播放速度"]) {
            [self.playerContext.controlHandler setSpeed:[configureModel.configuraValue[index] floatValue]];
        }

        if ([configureModel.configuraKey containsString:@"播放起始"]){


        } else if ([configureModel.configuraKey containsString:@"Decoder"]) {
            [self.playerContext.controlHandler setDecoderType:(QPlayerDecoderType)index];
            
            
        } else if ([configureModel.configuraKey containsString:@"Seek"]) {
            [self.playerContext.controlHandler  setSeekMode:index];

        } else if ([configureModel.configuraKey containsString:@"Start Action"]) {
            [self.playerContext.controlHandler setStartAction:(int)index];
            
        } else if ([configureModel.configuraKey containsString:@"Render ratio"]) {
            [self.playerContext.controlHandler setRenderRatio:(QPlayerRenderRatio)(index + 1)];
            
        } else if ([configureModel.configuraKey containsString:@"色盲模式"]) {
            [self.playerContext.controlHandler setBlindType:(QPlayerBlindType)index];
        }
    }
}

#pragma mark - PLScanViewControlerDelegate 代理方法

- (void)scanQRResult:(NSString *)qrString isLive:(BOOL)isLive{
    if (_playerContext.isPlaying) {
        [_playerContext.controlHandler pause_render];
    }
    NSURL *url;
    if (qrString) {
        url = [NSURL URLWithString:qrString];
    }
    if (url) {
        QNPlayerModel *modle = [[QNPlayerModel alloc] init];
        QStreamElement *el = [[QStreamElement alloc] init];
        el.url = qrString;
        el.is_select = YES;
        modle.streamElements = @[el];
        modle.is_live = isLive;
        [_playerModels addObject:modle];
        _selectedIndex = _playerModels.count - 1;
        [self tableView:self.urlListTableView didSelectRowAtIndexPath:[NSIndexPath indexPathForItem:_selectedIndex inSection:0]];

        [self.urlListTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:_selectedIndex inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
//        [self.urlListTableView reloadData];

    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"scan url error" message:qrString delegate:nil cancelButtonTitle:@"知道了" otherButtonTitles:nil];
        [alertView show];
    }
}

#pragma mark - 扫码二维码

- (void)scanCodeAction:(UIButton *)scanButton {
    QNScanViewController *scanViewController = [[QNScanViewController alloc] init];
    scanViewController.delegate = self;
    [self.navigationController pushViewController:scanViewController animated:YES];
}



- (void)dismissView {
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - TableView 代理方法

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _playerModels.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    QNURLListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"listCell" forIndexPath:indexPath];
    [cell configureListURLString:_playerModels[indexPath.row].streamElements[0].url index:indexPath.row];
    cell.deleteButton.tag = 100 + indexPath.row;
    [cell.deleteButton addTarget:self action:@selector(deleteUrlString:) forControlEvents:UIControlEventTouchDown];
    if (indexPath.row == _selectedIndex) {
        cell.urlLabel.textColor = PL_SELECTED_BLUE;
        cell.urlLabel.font = PL_FONT_MEDIUM(14);
    } else {
        cell.urlLabel.textColor = [UIColor blackColor];
        cell.urlLabel.font = PL_FONT_LIGHT(13);
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [QNURLListTableViewCell configureListCellHeightWithURLString:_playerModels[indexPath.row].streamElements[0].url index:indexPath.row];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSURL *selectedURL = [NSURL URLWithString:_playerModels[indexPath.row].streamElements[0].url];
//    if ([_player.URL isEqual:selectedURL]) {
//        return;
//    }
    if (_playerContext.playing) {
        [_playerContext.controlHandler pause_render];
    }
    
    _selectedIndex = indexPath.row;
    [_urlListTableView reloadData];
    
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels[indexPath.row].streamElements;
    model.is_live = _playerModels[indexPath.row].is_live;
    
    if(model.streamElements.count > 1){
        [self.maskView.qualitySegMc removeAllSegments];
        int index = 0;
        int indexSel = 0;
        for (QStreamElement* modle0 in model.streamElements) {
            if(modle0.is_select == YES){
                break;
            }
            indexSel ++;
        }
    
        
        for (QStreamElement* modle0 in model.streamElements) {
            [self.maskView.qualitySegMc insertSegmentWithTitle:[NSString stringWithFormat:@"%dp",modle0.quality] atIndex:index animated:NO];
             index ++;
        }
        self.maskView.qualitySegMc.selectedSegmentIndex = indexSel;
    }else{
        [self.maskView.qualitySegMc removeAllSegments];
        self.maskView.qualitySegMc.hidden = YES;
    }
    
    
    [_playerContext.controlHandler playMediaModel:model startPos:[[QDataHandle shareInstance] getConfiguraPostion]];
    [_maskView setPlayButtonState:NO];
    [self judgeWeatherIsLiveWithURL:selectedURL];
    
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, PL_SCREEN_WIDTH, 36)];
    headerView.backgroundColor = PL_COLOR_RGB(212, 220, 240, 1);
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 5, 179, 26)];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.font = PL_FONT_MEDIUM(14);

    titleLabel.text = @"PLPlayer 点播 URL 列表";

    [headerView addSubview:titleLabel];
    
    UIButton *pullButton = [[UIButton alloc] initWithFrame:CGRectMake(190, 7, 22, 22)];
    pullButton.selected = _isPull;
    [pullButton setImage:[UIImage imageNamed:@"pl_down"] forState:UIControlStateNormal];
    [pullButton setImage:[UIImage imageNamed:@"pl_up"] forState:UIControlStateSelected];
    [pullButton addTarget:self action:@selector(pullClickList:) forControlEvents:UIControlEventTouchDown];
//    [headerView addSubview:pullButton];
    
    UIButton *scanButton = [[UIButton alloc] initWithFrame:CGRectMake(245, 7, 22, 22)];
    scanButton.backgroundColor = PL_COLOR_RGB(81, 81, 81, 1);
    scanButton.layer.cornerRadius = 1;
    [scanButton setImage:[UIImage imageNamed:@"pl_scan"] forState:UIControlStateNormal];
    [scanButton addTarget:self action:@selector(scanCodeAction:) forControlEvents:UIControlEventTouchDown];
    [headerView addSubview:scanButton];
    return headerView;
}

- (void)deleteUrlString:(UIButton *)button {
    NSInteger index = button.tag - 100;
    UIAlertController *alertVc = [UIAlertController alertControllerWithTitle:@"删除播放地址" message:[NSString stringWithFormat:@"亲，是否确定要删除播放地址：%@ ？", _playerModels[index]] preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"否" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action){
    }];
    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"是" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self.playerModels removeObjectAtIndex:index];
        if(index == _selectedIndex){
            _selectedIndex = 0;
            [self tableView:self.urlListTableView didSelectRowAtIndexPath:[NSIndexPath indexPathForItem:_selectedIndex inSection:0]];
            [self.urlListTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:_selectedIndex inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];

        }
        if (self.playerModels.count != 0) {
            [self.urlListTableView reloadData];
        } else{
            [self.urlListTableView removeFromSuperview];
            [self showHintViewWithText:@"暂无直播记录，快去扫描二维码观看吧 ~"];
        }
    }];
    [alertVc addAction:cancelAction];
    [alertVc addAction:sureAction];
    [self presentViewController:alertVc animated:YES completion:nil];
}

#pragma mark - 显示提示信息

- (void)showHintViewWithText:(NSString *)hintStr
{
    self.hintLabel.text = hintStr;
    if ([self.view.subviews containsObject:_hintLabel]) {
        [self.hintLabel removeFromSuperview];
    }
    [self.view addSubview:_hintLabel];
}

- (NSString *)convertDataToHexStr:(NSData *)data {
    if (!data || [data length] == 0) {
        return @"";
    }
    NSMutableString *string = [[NSMutableString alloc] initWithCapacity:[data length]];
    
    [data enumerateByteRangesUsingBlock:^(const void *bytes, NSRange byteRange, BOOL *stop) {
        unsigned char *dataBytes = (unsigned char*)bytes;
        for (NSInteger i = 0; i < byteRange.length; i++) {
            NSString *hexStr = [NSString stringWithFormat:@"%x", (dataBytes[i]) & 0xff];
            if ([hexStr length] == 2) {
                [string appendString:hexStr];
            } else {
                [string appendFormat:@"0%@", hexStr];
            }
        }
    }];
    
    return string;
}

- (void)judgeWeatherIsLiveWithURL:(NSURL *)URL {
    NSString *scheme = URL.scheme;
    NSString *pathExtension = URL.pathExtension;
    BOOL isLive;
    if (([scheme isEqualToString:@"rtmp"] && ![pathExtension isEqualToString:@"pili"]) ||
        ([scheme isEqualToString:@"http"] && [pathExtension isEqualToString:@"flv"])) {
        isLive = YES;
    } else {
        isLive = NO;
    }
    [self updateSegmentAndInfomationWithLive:isLive];
}

- (void)updateSegmentAndInfomationWithLive:(BOOL)isLive {
    if (isLive) {
        self.segment.selectedSegmentIndex = 1;
    } else{
        self.segment.selectedSegmentIndex = 0;
    }
    [_urlListTableView removeFromSuperview];
    [self layoutUrlListTableView];
}

#pragma mark - 返回 

- (void)leftBarButtonAction {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)qualityAction:(UISegmentedControl *)segm{
    NSInteger index = segm.selectedSegmentIndex;
    QNPlayerModel *model = self.playerModels[_selectedIndex];
    
    int tempIndex = 0;
    for (QStreamElement* modle0 in model.streamElements) {
        modle0.is_select = NO;
        
        if (index == tempIndex) {
            modle0.is_select = YES;
        }
        tempIndex ++;
    }
    NSArray<NSString*> *segmentedArray = [[NSArray alloc]initWithObjects:@"1080p",@"720p",@"640p",@"270p",nil];
    [_toastView addText:[NSString stringWithFormat:@"即将切换为：%@",segmentedArray[index]]];
    self.definition = segmentedArray[index];
    
    [self.playerContext.controlHandler switchQuality:model.streamElements[index]];
    
}



/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

@end
