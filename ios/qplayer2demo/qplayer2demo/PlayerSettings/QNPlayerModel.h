//
//  QNPlayerModel.h
//  QPlayerKitDemo
//
//  Created by 孙慕 on 2022/6/9.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface QNPlayerModel : NSObject

@property(nonatomic,strong)NSString *name;

@property (assign, nonatomic) BOOL isLive;

@property (strong, nonatomic) NSArray <QStreamElement*> *streamElements;
@end

NS_ASSUME_NONNULL_END
