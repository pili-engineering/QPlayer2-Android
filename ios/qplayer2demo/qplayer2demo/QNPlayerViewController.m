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
UITableViewDelegate,
UITableViewDataSource,
QNPlayerMaskViewDelegate,
PLScanViewControlerDelegate,
QIPlayerStateChangeListener,
QIPlayerBufferingListener,
QIPlayerQualityListener
>

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
@property (nonatomic, assign) BOOL scanClick;
@property (nonatomic, strong) RenderView *myRenderView;
@property (nonatomic, assign) BOOL isPlaying;
@property (nonatomic, assign) BOOL beInterruptedByOtherAudio;
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
    if (!self.scanClick) {
        
        [self.playerContext.controlHandler stop];
    }
    [self.playerContext.controlHandler playerRelease];
    self.playerContext = nil;
}

- (void)onUIApplication:(BOOL)active{
    if (active) {
        [self.playerContext.controlHandler resume];
    }else{
        [self.playerContext.controlHandler pause];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.scanClick = NO;
    self.isPlaying = NO;
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
    
    
    _toastView = [[QNToastView alloc]initWithFrame:CGRectMake(0, PL_SCREEN_HEIGHT-300, 200, 300)];
    [self.view addSubview:_toastView];
    //清晰度默认为1080p
    self.definition = @"1080p";
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

    
    QPlayerContext *player =  [[QPlayerContext alloc]initPlayerVersion:nil storageDir:documentsDir logLevel:LOG_VERBOSE];
    self.playerContext = player;
//    self.playerContext.controlHandler.playerView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
    _myRenderView = [[RenderView alloc]initWithFrame:CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT)];
    [_myRenderView attachRenderHandler:self.playerContext.renderHandler];
//    [self.view addSubview:self.playerContext.controlHandler.playerView];
    [self.view addSubview:_myRenderView];
    
    
    for (QNClassModel* model in configs) {
        for (PLConfigureModel* configModel in model.classValue) {
            if ([model.classKey isEqualToString:@"PLPlayerOption"]) {
                [self configurePlayerWithConfigureModel:configModel classModel:model];;
            }
        }
    }
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels.firstObject.streamElements;
    model.is_live = _playerModels.firstObject.is_live;
    [self.playerContext.controlHandler playMediaModel:model startPos:[[QDataHandle shareInstance] getConfiguraPostion]];


}

#pragma mark - PLPlayerDelegate
-(void)playerContextAllCallBack{

    [self.playerContext.controlHandler addPlayerStateListener:self];
    [self.playerContext.controlHandler addPlayerBufferingListener:self];
    [self.playerContext.controlHandler addPlayerQualityListener:self];
    
}
-(void)onStateChange:(QPlayerContext *)context state:(QPlayerState)state{
    if (state == QPLAYERSTATUS_PREPARE) {
        [self.maskView loadActivityIndicatorView];
        [_toastView addText:@"开始拉视频数据"];
        [_toastView addDecoderType:[self.maskView getDecoderType]];
        [_toastView addText:[NSString stringWithFormat:@"清晰度：%@",self.definition]];
    } else if (state == QPLAYERSTATUS_PLAYING) {
        //            _maskView.player = _player;
        self.isPlaying = YES;
        self.maskView.player = self.playerContext;
        [_maskView setPlayButtonState:YES];
        [self showHintViewWithText:@"开始播放器"];
        
        [_toastView addText:@"播放中"];
        
    } else if (state == QPLAYERSTATUS_PAUSED || state == QPLAYERSTATUS_STOPPED) {
        [_toastView addText:@"暂停渲染或者停止当前播放"];
        [_maskView setPlayButtonState:NO];
    }else if (state == QPLAYERSTATUS_ERROR){
        [_toastView addText:@"播放错误"];
        [_maskView setPlayButtonState:NO];
    }else if (state == QPLAYERSTATUS_COMPLETED){
        
        [_toastView addText:@"播放完成"];
        [_maskView setPlayButtonState:NO];
    }
    
}


-(void)onBufferingEnd:(QPlayerContext *)context{
    [self.maskView removeActivityIndicatorView];
}
-(void)onBufferingStart:(QPlayerContext *)context{
    [self.maskView loadActivityIndicatorView];
}
-(void)onQualitySwitchComplete:(QPlayerContext *)context oldQuality:(NSInteger)oldQuality newQuality:(NSInteger)newQuality qualitySerial:(NSInteger)qualitySerial{
    NSString *string = [NSString stringWithFormat:@"清晰度切换成功"];
    [self.toastView addText:string];
}


#pragma mark - 计时器方法

- (void)onTimer:(NSTimer *)timer {
    self.urlListTableView.tableHeaderView = [_infoHeaderView updateInfoWithInfoArray:[self updateInfoArray]];
}

#pragma mark - 更新播放信息数组

- (NSArray *)updateInfoArray {
    NSString *statusStr = [self updatePlayerStatus];
    NSString *firstVideoTimeStr = [NSString stringWithFormat:@"%d ms",self.playerContext.renderHandler.firstVideoTime];
    NSString *renderFPSStr = [NSString stringWithFormat:@"%dfps", self.playerContext.controlHandler.fps];
    NSString *downSpeedStr = [NSString stringWithFormat:@"%.2fkb/s", self.playerContext.controlHandler.downloadSpeed * 1.0/1000];

    NSArray *array = @[statusStr,firstVideoTimeStr,renderFPSStr,downSpeedStr];

    long bufferPositon = self.playerContext.controlHandler.bufferPostion;
    NSString *fileUnit = @"ms";
    
    NSString *fileSizeStr = [NSString stringWithFormat:@"%d%@", bufferPositon, fileUnit];
    NSMutableArray *mutableArray = [NSMutableArray arrayWithArray:array];
    [mutableArray addObjectsFromArray:@[fileSizeStr]];
    array = [mutableArray copy];
    return array;
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
                                       @(QPLAYERSTATUS_STOPPED):@"Stopped",
                                       @(QPLAYERSTATUS_ERROR):@"Error",
                                       @(QPLAYERSTATUS_SEEKING):@"seek",
                                       @(QPLAYERSTATUS_COMPLETED):@"Completed"
                                       };
    return statusDictionary[@(self.playerContext.controlHandler.currentPlayerState)];

}

#pragma mark - 添加点播界面蒙版


- (void)addPlayerMaskView{
    self.maskView = [[QNPlayerMaskView alloc] initWithFrame:CGRectMake(0, 0, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT) player:self.playerContext isLiving:NO renderView:self.myRenderView];
//    self.maskView.center = self.playerContext.controlHandler.playerView.center;
    self.maskView.center = self.myRenderView.center;
    self.maskView.delegate = self;
    self.maskView.backgroundColor = PL_COLOR_RGB(0, 0, 0, 0.35);
//    [self.view insertSubview:_maskView aboveSubview:self.playerContext.controlHandler.playerView];
    [self.view insertSubview:_maskView aboveSubview:self.myRenderView];
        
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
        
//        self.playerContext = nil;
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
    [_maskView setPlayButtonState:YES];

}
- (BOOL)shouldAutorotate

{

return NO;

}

-(BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return UIInterfaceOrientationIsPortrait(interfaceOrientation);
}
- (void)forceOrientationLandscape:(BOOL)isLandscape {
    QNAppDelegate *appDelegate = (QNAppDelegate *)[UIApplication sharedApplication].delegate;
    appDelegate.isFlip = isLandscape;
    [appDelegate application:[UIApplication sharedApplication] supportedInterfaceOrientationsForWindow:self.view.window];
    [UIViewController attemptRotationToDeviceOrientation];
    _isFlip = appDelegate.isFlip;
    if (isLandscape) {
        [self.navigationController setNavigationBarHidden:YES animated:NO];
        [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeRight) forKey:@"orientation"];
        [self.urlListTableView removeFromSuperview];
        self.myRenderView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT);
        self.maskView.frame = CGRectMake(0, 0, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT);
    } else {
        [self.navigationController setNavigationBarHidden:NO animated:NO];
        [[UIDevice currentDevice] setValue:@(UIDeviceOrientationPortrait) forKey:@"orientation"];
        [self.view addSubview:_urlListTableView];
        self.myRenderView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
        self.maskView.frame = CGRectMake(0, _topSpace, PLAYER_PORTRAIT_WIDTH, PLAYER_PORTRAIT_HEIGHT);
    }
    
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

    if (!isLive) {
        [_playerContext.controlHandler resume];
    }
    NSURL *url;
    if (qrString) {
        url = [NSURL URLWithString:qrString];
    }
    else{
        return;
        
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
    
    if (_playerContext.controlHandler.currentPlayerState == QPLAYERSTATUS_PLAYING) {
        [_playerContext.controlHandler pause];
    }
    self.scanClick = YES;
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
    if (_playerContext.controlHandler.currentPlayerState == QPLAYERSTATUS_PLAYING) {
        [_playerContext.controlHandler pause];
    }
    
    _selectedIndex = indexPath.row;
    [_urlListTableView reloadData];
    
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels[indexPath.row].streamElements;
    model.is_live = _playerModels[indexPath.row].is_live;
    self.maskView.isLiving = model.is_live;
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
            if ([modle0.url isEqual:@"http://demo-videos.qnsdk.com/only-video-1080p-60fps.m4s"]) {
                
                self.maskView.qualitySegMc.hidden = YES;
            }else{
                
                [self.maskView.qualitySegMc insertSegmentWithTitle:[NSString stringWithFormat:@"%dp",modle0.quality] atIndex:index animated:NO];
                index++;
            }
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


@end
