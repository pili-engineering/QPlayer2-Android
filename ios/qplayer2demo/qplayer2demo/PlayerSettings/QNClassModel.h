//
//  QNClassModel.h
//  QPlayerKitDemo
//
//  Created by 冯文秀 on 2017/6/29.
//  Copyright © 2017年 0dayZh. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PLConfigureModel : NSObject
@property (nonatomic, copy) NSString *configuraKey;
@property (nonatomic, strong) NSMutableArray *configuraValue;
@property (nonatomic, strong) NSNumber *selectedNum;

+ (PLConfigureModel *)configureModelWithDictionary:(NSDictionary *)dictionary;
@end

@interface QNClassModel : NSObject
@property (nonatomic, copy) NSString *classKey;
@property (nonatomic, strong) NSArray<PLConfigureModel*> *classValue;

+ (NSArray *)classArrayWithArray:(NSArray *)array;
@end

