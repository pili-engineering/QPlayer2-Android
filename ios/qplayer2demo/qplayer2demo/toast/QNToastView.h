//
//  QNToastView.h
//  QPlayerKitDemo
//
//  Created by 王声禄 on 2022/7/27.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface QNToastView : UIView
//初始化
-(instancetype)initWithFrame:(CGRect)frame;

//添加toast内容
-(void)addText:(NSString *)str;

//添加解码方式的toast
-(void)addDecoderType:(QPlayerDecoder)type;
@end

NS_ASSUME_NONNULL_END
