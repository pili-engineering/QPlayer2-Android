//
//  PLCellPlayerViewController.m
//  PLPlayerKitDemo
//
//  Created by 冯文秀 on 2018/5/10.
//  Copyright © 2018年 Aaron. All rights reserved.
//

#import "PLCellPlayerViewController.h"
#import "PLCellPlayerTableViewCell.h"
#import "QNPlayerModel.h"
#import "QNPlayerShortVideoMaskView.h"
#import "QNToastView.h"
static NSString *status[] = {
    @"Unknow",
    @"Preparing",
    @"Ready",
    @"Open",
    @"Caching",
    @"Playing",
    @"Paused",
    @"Stopped",
    @"Error",
    @"Reconnecting",
    @"Completed"
};

@interface PLCellPlayerViewController ()
<
UITableViewDelegate,
UITableViewDataSource
>

@property (nonatomic, strong) QPlayerContext *player;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) PLCellPlayerTableViewCell *currentCell;
@property (nonatomic, strong) UIActivityIndicatorView *activityIndicatorView;

@property (nonatomic, strong) NSMutableArray <QMediaItemContext *>*cacheArray;
@property (nonatomic, strong) NSMutableArray<QNPlayerModel *> *playerModels;

@property (nonatomic, assign) CGFloat topSpace;

@property (nonatomic, strong) QNToastView *toastView;

@end

@implementation PLCellPlayerViewController

- (void)dealloc {
    
    [self.player.controlHandler stop];
    for (int i = 0; i < _cacheArray.count; i ++) {
        QMediaItemContext *item = _cacheArray[i];
       BOOL aa =  [item.controlHandler stop];
        NSLog(@"----%d",aa);
    }
    [_cacheArray removeAllObjects];
    NSLog(@"PLCellPlayerViewController - dealloc");
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.player.controlHandler stop];
}


- (void)viewWillAppear:(BOOL)animated {
    [self.navigationController setNavigationBarHidden:NO animated:NO];

}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.cacheArray = [NSMutableArray array];
    
    // Do any additional setup after loading the view.
    self.title = @"短视频";
    self.navigationController.navigationBar.barTintColor = PL_SEGMENT_BG_COLOR;
    
    UIButton *backButton = [[UIButton alloc] initWithFrame:CGRectMake(5, 6, 34, 34)];
    UIImage *image = [UIImage imageNamed:@"pl_back"];
    // iOS 11 之后， UIBarButtonItem 在 initWithCustomView 是图片按钮的情况下变形
    if ([UIDevice currentDevice].systemVersion.floatValue >= 11.0f) {
        image = [self originImage:image scaleToSize:CGSizeMake(34, 34)];
    }
    [backButton setImage:image forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(getBack) forControlEvents:UIControlEventTouchDown];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:backButton];
    

    NSString *documentsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
    NSString *path = [documentsDir stringByAppendingPathComponent:@"lite_urls.json"];
    
    NSData *data=[[NSData alloc] initWithContentsOfFile:path];
    if (!data) {
        path=[[NSBundle mainBundle] pathForResource:@"lite_urls" ofType:@"json"];
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
    
    self.tableView = [[UITableView alloc]initWithFrame:CGRectMake(0, 0, PL_SCREEN_WIDTH, PL_SCREEN_HEIGHT) style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.rowHeight = PLAYER_PORTRAIT_HEIGHT + 1;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.pagingEnabled = YES;
    self.tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    [self.view addSubview:_tableView];
    

    [self setUpPlayer];
    
    _toastView = [[QNToastView alloc]initWithFrame:CGRectMake(0, PL_SCREEN_HEIGHT - 350, 200, 300)];
    [self.view addSubview:_toastView];
    
    [self playerContextAllCallBack];
}

- (void)setUpPlayer{
    NSMutableArray *configs = [NSMutableArray array];
    if (PL_HAS_NOTCH) {
        _topSpace = 88;
    } else {
        _topSpace = 64;
    }
    
    NSString *documentsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
//    NSString *path = [documentsDir stringByAppendingPathComponent:@"me"];
    
    QAppInformation *info = [[QAppInformation alloc] init];
    info.mAppId = @"com.qbox.QPlayerKitDemo";

    QPlayerContext *player = [[QPlayerContext alloc]initPlayerAppInfo:info storageDir:documentsDir logLevel:LOG_VERBOSE];
    //设置为软解
    [player.controlHandler setDecoderType:QPLAYER_DECODER_SETTING_SOFT_PRIORITY];
    self.player = player;
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = _playerModels.firstObject.streamElements;
    model.is_live = _playerModels.firstObject.is_live;
    [self.player.controlHandler playMediaModel:model startPos:0];


}

#pragma mark - PLPlayerDelegate
-(void)playerContextAllCallBack{
    __weak PLCellPlayerViewController *weakSelf = self;
    [self.player.controlHandler addPlayerStateCallBackName:@"stateChanged" CallBack:^(QPlayerContext *_Nullable context, QPlayerStatus state) {
        [weakSelf statePlayerChanged:state];
        
    }];
    [self.player.renderHandler addPlayerFirstFrameCallBackName:@"firstFrame" callBack:^(QPlayerContext *_Nullable context, QNotifyModle * _Nonnull notify, NSInteger elapsedTime) {
            [weakSelf screenRenderFirstFrame:notify elapsedTime:elapsedTime];
    }];
    [self.player.controlHandler addPlayerStreamOpenAndDurationCallBackName:@"streamOpen" callBack:^(QPlayerContext *_Nullable context, QNotifyModle * _Nonnull notify, int64_t duration) {
            [weakSelf streamOpen:notify duration:duration];
    }];
    [self.player.controlHandler addPlayerStreamOpenAndErrorCallBackName:@"error" callBack:^(QPlayerContext *_Nullable context, QNotifyModle * _Nonnull notify, NSInteger error) {
        [weakSelf streamOpenError:notify error:error];
    }];
}

-(void)statePlayerChanged:(QPlayerStatus)state{
    if(state == QPLAYERSTATUS_NONE){
        [_toastView addText:@"初始状态"];
    }
    else if (state == QPLAYERSTATUS_INIT){
        
        [_toastView addText:@"创建完成"];
    }
    else if(state == QPLAYERSTATUS_PREPARE ||state == QPLAYERSTATUS_MEDIA_ITEM_PREPARE){
        [_toastView addText:@"正在加载"];
    }
    else if (state == QPLAYERSTATUS_PLAYING) {
        if (_currentCell == nil) {
            [self tableView:self.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForItem:0 inSection:0]];
        }else{
            _currentCell.player = self.player;
        }
        _currentCell.state = YES;
        [_toastView addText:@"正在播放"];
    }
    else if(state == QPLAYERSTATUS_PAUSED ||  state == QPLAYERSTATUS_PAUSED_RENDER){
        _currentCell.state = NO;
        [_toastView addText:@"播放暂停"];
    }
    else if(state == QPLAYERSTATUS_STOPPED || state == QPLAYERSTATUS_COMPLETED){
        _currentCell.state = NO;
        [_toastView addText:@"播放完成"];
    }
    else if(state == QPLAYERSTATUS_ERROR){
        _currentCell.state = NO;
        
        [_toastView addText:@"播放出错"];
    }
    else if (state == QPLAYERSTATUS_SEEKING){
        [_toastView addText:@"正在seek"];
    }
    else{
        [_toastView addText:@"其他状态"];
    }


    
}


-(void)screenRenderFirstFrame:(QNotifyModle *)notify elapsedTime:(NSInteger)elapsedTime{
    NSLog(@"预加载首帧时间----%d",elapsedTime);
    
    
    if (!_currentCell) {
        _currentCell = (PLCellPlayerTableViewCell *)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _currentCell.playerView = _player.controlHandler.playerView;
        
    });
    
    [self updateCache:_currentCell.model];
}

-(void)streamOpen:(QNotifyModle *)notify duration:(int64_t)duration{
    
    NSLog(@"-------------streamOpen ----%@",notify.user_type);
}

-(void)streamOpenError:(QNotifyModle *)notify error:(NSInteger)error{
    
    NSLog(@"-------------streamOpenError -----%@",notify.user_type);
}

#pragma mark - mediaItemDelegate

-(void)addAllCallBack:(QMediaItemContext *)mediaItem{
    
    __weak PLCellPlayerViewController *weakSelf = self;
    
    [mediaItem.controlHandler addPlayerOnStateChangedCallBackName:@"StateChanged" callBack:^(QMediaItemContext * _Nonnull context, QMediaItemState state) {
        NSLog(@"-------------预加载--onStateChanged -- %d---%@",state,context.controlHandler.media_model.streamElements[0].url);
        
    }];
    [mediaItem.controlHandler addPlayerOpenNotifyAndDurationCallBackName:@"openNotify" callBack:^(QMediaItemContext * _Nonnull context, QNotifyModle * _Nonnull notify, int64_t duration) {
            NSLog(@"-------------预加载--openNotify -- %d",duration);
    }];
    [mediaItem.controlHandler addPlayerOpenErrorCallBackName:@"openError" callBack:^(QMediaItemContext * _Nonnull context, QNotifyModle * _Nonnull notify) {
        NSLog(@"-------------预加载--openError -- ");
    }];
    [mediaItem.controlHandler addPlayerIOErrorCallBackName:@"IOError" callBack:^(QMediaItemContext * _Nonnull context, QNotifyModle * _Nonnull notify) {
            NSLog(@"-------------预加载--ioError");
    }];
    [mediaItem.controlHandler addPlayerCommandNotAllowCallBackName:@"NotAllow" callBack:^(QMediaItemContext * _Nonnull context, QNotifyModle * _Nonnull notify, NSString * _Nonnull commandName, QMediaItemState state) {
        NSLog(@"-------------预加载--notAllow---%@",commandName);
    }];
}

#pragma mark - tableView delegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _playerModels.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *CellIdentifier = [NSString stringWithFormat:@"Cell%ld%ld", (long)[indexPath section], (long)[indexPath row]];
    // 出列可重用的 cell，从缓存池取标识为 "Cell" 的 cell
    PLCellPlayerTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[PLCellPlayerTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    QNPlayerModel *model = _playerModels[indexPath.row];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.model = model;
       
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    PLCellPlayerTableViewCell *cell = (PLCellPlayerTableViewCell *)[tableView cellForRowAtIndexPath:indexPath];
    [self updatePlayCell:cell];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return PL_SCREEN_HEIGHT;
}


// 松手时已经静止,只会调用scrollViewDidEndDragging
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if (decelerate == NO) { // scrollView已经完全静止
        [self handleScroll];
    }
}

// 松手时还在运动, 先调用scrollViewDidEndDragging,在调用scrollViewDidEndDecelerating
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    // scrollView已经完全静止
    [self handleScroll];
}

-(void)handleScroll{
    // 找到下一个要播放的cell(最在屏幕中心的)
    PLCellPlayerTableViewCell *finnalCell = nil;
    NSArray *visiableCells = [self.tableView visibleCells];
    CGFloat gap = MAXFLOAT;
    for (PLCellPlayerTableViewCell *cell in visiableCells) {

        if (cell.model) { // 如果这个cell有视频
            CGPoint coorCentre = [cell.superview convertPoint:cell.center toView:nil];
            CGFloat delta = fabs(coorCentre.y-[UIScreen mainScreen].bounds.size.height*0.5);
            if (delta < gap) {
                gap = delta;
                finnalCell = cell;
            }
        }
    }

    // 注意, 如果正在播放的cell和finnalCell是同一个cell, 不应该在播放
    if (finnalCell != nil && _currentCell != finnalCell)  {
        [self updatePlayCell:finnalCell];
        return;
    }

}


-(void)updatePlayCell:(PLCellPlayerTableViewCell *)cell{
    cell.player = self.player;
    BOOL isPlaying = (_player.controlHandler.currentPlayerState == QPLAYERSTATUS_PLAYING);
  
    if (_currentCell == cell && _currentCell) {
        if(isPlaying) {
                [_player.controlHandler pauseRender];
        } else{
                [_player.controlHandler resumeRender];
        }
    } else{

        QMediaItemContext *item = [self findCrashMediaItemsOf:cell.model];
        if (item) {
            [self.player.controlHandler playMediaItem:item];
            NSLog(@"预加载--播放缓存---%@",item.controlHandler.media_model.streamElements[0].url);
        }else{
            QMediaModel *model = [[QMediaModel alloc] init];
            model.streamElements = cell.model.streamElements;
            model.is_live = _playerModels.firstObject.is_live;
            [self.player.controlHandler playMediaModel:model startPos:0];
            
            NSLog(@"预加载--new---%@",item.controlHandler.media_model.streamElements[0].url);
        }
    
        _currentCell = cell;
    }
}


-(void)AddToCash:(QNPlayerModel *)playerModel{
    NSString *documentsDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
//    NSString *path = [documentsDir stringByAppendingPathComponent:@"me"];
    
    QAppInformation *info = [[QAppInformation alloc] init];
    info.mAppId = @"com.qbox.QPlayerKitDemo";
    
    QMediaModel *model = [[QMediaModel alloc] init];
    model.streamElements = playerModel.streamElements;
    model.is_live = playerModel.is_live;
    
    // 预加载
    QMediaItemContext *item = [[QMediaItemContext alloc] initItemComtextStorageDir:documentsDir logLevel:LOG_VERBOSE];
    [self addAllCallBack:item];
    [item.controlHandler start:model startPos:0];
    [self.cacheArray addObject:item];
    
    NSLog(@"预加载--addcrash -- %@",playerModel.streamElements.firstObject.url);
    
}

-(int)indexPlayerModelsOf:(QMediaItemContext *)item{
    for (int i = 0; i < _playerModels.count; i ++) {
        QNPlayerModel *modle = _playerModels[i];
        if (modle.is_live == item.controlHandler.media_model.is_live && modle.streamElements == item.controlHandler.media_model.streamElements) {
            return i;
        }
    }
    return -1;
}

-(QMediaItemContext *)findCrashMediaItemsOf:(QNPlayerModel *)modle{
    for (int i = 0; i < _cacheArray.count; i ++) {
        QMediaItemContext *item = _cacheArray[i];
        if (modle.is_live == item.controlHandler.media_model.is_live && modle.streamElements == item.controlHandler.media_model.streamElements) {
            return item;
        }
    }
    return NULL;
}


-(void)updateCache:(QNPlayerModel *)model{

    NSArray *realCacheArray = [self getRealCacheIndexArray:model];
    
    NSMutableArray *delArray = [NSMutableArray array];
    NSMutableArray *addArray = [NSMutableArray array];
    
    for (int i = 0; i < _cacheArray.count; i ++) {//
        QMediaItemContext *model0 = _cacheArray[i];
        int index = (int)[self indexPlayerModelsOf:model0];
        if (![realCacheArray containsObject:@(index)]) {// _cacheArray 独有
            [delArray addObject:model0];
             
        }
    }

    for (int i = 0; i < realCacheArray.count; i ++) {//
        int index = [realCacheArray[i] intValue];
        QNPlayerModel *model0 = _playerModels[index];
        if (![self findCrashMediaItemsOf:model0]) {// realCacheArray 独有
            [self AddToCash:_playerModels[index]];
        }
    }
    
    [_cacheArray removeObjectsInArray:delArray];
}

// 前 1 后 3
-(NSArray *)getRealCacheIndexArray:(QNPlayerModel *)model{
    NSArray *realCacheArray = nil;
    if (_playerModels.count <= 5) {
        NSMutableArray *array = [NSMutableArray array];
        for (int i = 0; i < _playerModels.count; i ++) {
            [array addObject:@(i)];
        }
        realCacheArray = [array copy];
        return realCacheArray;
    }

    int index = (int)[_playerModels indexOfObject:model];
    if (index == 0) {
        realCacheArray = @[@1,@2,@3];
    }
    if (index == 1) {
        realCacheArray = @[@0,@1,@2,@3];
    }
    
    if (index == _playerModels.count-1) {
        realCacheArray = @[@(index-1)];
    }
    if (index == _playerModels.count-2) {
        realCacheArray = @[@(index-1),@(index),@(index + 1)];
    }
    if (index == _playerModels.count-3) {
        realCacheArray = @[@(index-1),@(index),@(index + 1),@(index + 2)];
    }
    
    if (realCacheArray.count == 0) {//之前没有命中
        realCacheArray = @[@(index -1),@(index),@(index + 1),@(index + 2),@(index + 3)];
    }
    
    return realCacheArray;
}


- (void)getBack {
    [self.player.controlHandler stop];
    [self.navigationController popViewControllerAnimated:YES];
}


- (void)addActivityIndicatorView {
    if (self.activityIndicatorView) {
        return;
    }
    UIActivityIndicatorView *activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    activityIndicatorView.center = CGPointMake(CGRectGetMidX(self.player.controlHandler.playerView.bounds), CGRectGetMidY(self.player.controlHandler.playerView.bounds));
    [self.player.controlHandler.playerView addSubview:activityIndicatorView];
    [activityIndicatorView stopAnimating];
    self.activityIndicatorView = activityIndicatorView;
}

- (UIImage *)originImage:(UIImage *)image scaleToSize:(CGSize)size{
    UIGraphicsBeginImageContext(size);
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return scaledImage;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end
