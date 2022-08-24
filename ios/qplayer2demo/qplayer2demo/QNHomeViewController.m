//
//  QNHomeViewController.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/9/18.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#import "QNHomeViewController.h"
#import "QNPlayerViewController.h"
#import "PLCellPlayerViewController.h"
#import "QNPlayerConfigViewController.h"


#define kLogoSizeWidth (PL_SCREEN_WIDTH - 100)
#define kLogoSizeHeight (PL_SCREEN_WIDTH - 100)*0.38

@interface QNHomeViewController ()

@property (nonatomic, strong) NSArray<QNClassModel*> *playerConfigArray;


@end

@implementation QNHomeViewController
- (void)viewWillAppear:(BOOL)animated {
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // 布局主页面
    [self layoutMainView];

}

- (void)layoutMainView {
    UIImageView *qiniuLogImgView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"LOGO"]];
    qiniuLogImgView.frame = CGRectMake(50, (PL_SCREEN_HEIGHT - kLogoSizeHeight - 116)/4, kLogoSizeWidth, kLogoSizeHeight);
    [self.view addSubview:qiniuLogImgView];
    
    // 单 player
    UIButton *playerButton = [[UIButton alloc] initWithFrame:CGRectMake(70, (PL_SCREEN_HEIGHT - kLogoSizeHeight - 116)/4 + kLogoSizeHeight + 50, PL_SCREEN_WIDTH - 140, 34)];
    playerButton.backgroundColor = PL_BUTTON_BACKGROUNDCOLOR;
    playerButton.layer.cornerRadius = 3;
    playerButton.tag = 10;
    [playerButton addTarget:self action:@selector(enterPlayerAction:) forControlEvents:UIControlEventTouchDown];
    [playerButton setTitle:@"长视频" forState:UIControlStateNormal];
    playerButton.titleLabel.font = PL_FONT_MEDIUM(14);
    [self.view addSubview:playerButton];
    
    // 单 player 多 cell
    UIButton *cellPlayerButton = [[UIButton alloc] initWithFrame:CGRectMake(70, (PL_SCREEN_HEIGHT - kLogoSizeHeight - 116)/4 + kLogoSizeHeight + 50 + 70, PL_SCREEN_WIDTH - 140, 34)];
    cellPlayerButton.backgroundColor = PL_BUTTON_BACKGROUNDCOLOR;
    cellPlayerButton.layer.cornerRadius = 3;
    cellPlayerButton.tag = 20;
    [cellPlayerButton addTarget:self action:@selector(enterCellPlayerAction:) forControlEvents:UIControlEventTouchDown];
    [cellPlayerButton setTitle:@"短视频" forState:UIControlStateNormal];
    cellPlayerButton.titleLabel.font = PL_FONT_MEDIUM(14);
//    [self.view addSubview:cellPlayerButton];
    
    // 多 player 多 item
    UIButton *itemPlayerButton = [[UIButton alloc] initWithFrame:CGRectMake(70, (PL_SCREEN_HEIGHT - kLogoSizeHeight - 116)/4 + kLogoSizeHeight + 50 + 140, PL_SCREEN_WIDTH - 140, 34)];
    itemPlayerButton.backgroundColor = PL_BUTTON_BACKGROUNDCOLOR;
    itemPlayerButton.layer.cornerRadius = 3;
    itemPlayerButton.tag = 20;
    [itemPlayerButton addTarget:self action:@selector(enterItemPlayerAction:) forControlEvents:UIControlEventTouchDown];
    [itemPlayerButton setTitle:@"初始化" forState:UIControlStateNormal];
    itemPlayerButton.titleLabel.font = PL_FONT_MEDIUM(14);
    [self.view addSubview:itemPlayerButton];
    
}

#pragma mark - 进入各个模式

- (void)enterPlayerAction:(UIButton *)button {
    QNPlayerViewController *playerViewController = [[QNPlayerViewController alloc] init];

    [self.navigationController pushViewController:playerViewController animated:YES];
}

- (void)enterCellPlayerAction:(UIButton *)button {
    PLCellPlayerViewController *cellPlayerViewController = [[PLCellPlayerViewController alloc] init];
    [self.navigationController pushViewController:cellPlayerViewController animated:YES];
}

- (void)enterItemPlayerAction:(UIButton *)button {
    QNPlayerConfigViewController *itemPlayerViewController = [[QNPlayerConfigViewController alloc] init];
    [self.navigationController pushViewController:itemPlayerViewController animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
