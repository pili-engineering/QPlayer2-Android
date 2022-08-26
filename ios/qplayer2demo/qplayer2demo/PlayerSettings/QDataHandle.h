//
//  QDataHandle.h
//  QPlayerKitDemo
//
//  Created by 孙慕 on 2022/7/11.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QNClassModel.h"
#import "QNChangePlayerView.h"

NS_ASSUME_NONNULL_BEGIN

@interface QDataHandle : NSObject

+ (QDataHandle *)shareInstance;

@property (nonatomic, strong) NSArray<QNClassModel*> *playerConfigArray;

-(void)setSelConfiguraKey:(NSString *)tittle selIndex:(int)selIndex;

-(void)setValueConfiguraKey:(NSString *)tittle selValue:(int)value;

-(int)getConfiguraPostion;
-(void)saveConfigurations;
-(BOOL)getAuthenticationState;

@end

NS_ASSUME_NONNULL_END
