//
//  QNScanViewController.m
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/7/26.
//  Copyright © 2017年 qiniu. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol PLScanViewControlerDelegate <NSObject>

- (void)scanQRResult:(NSString *)qrString isLive:(BOOL)isLive;

@end

@interface QNScanViewController : UIViewController

@property (nonatomic, weak) id<PLScanViewControlerDelegate> delegate;

@end
