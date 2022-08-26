//
//  QDataHandle.m
//  QPlayerKitDemo
//
//  Created by 孙慕 on 2022/7/11.
//  Copyright © 2022 Aaron. All rights reserved.
//

#import "QDataHandle.h"

@implementation QDataHandle

+ (QDataHandle *)shareInstance {
    
    static QDataHandle * single = nil;
    static dispatch_once_t onceToken ;
    
    dispatch_once(&onceToken, ^{
        single =[[QDataHandle alloc]init];
    }) ;
    return single;
    
}

-(instancetype)init{
    if (self = [super init]) {
        [self showPlayerConfiguration];
    }
    return self;
}


- (void)showPlayerConfiguration {
    NSUserDefaults *userdafault = [NSUserDefaults standardUserDefaults];
    NSArray *dataArray = [userdafault objectForKey:@"PLPlayer_settings"];
    
    if (dataArray.count != 0 ) {
        NSMutableArray *array = [NSMutableArray array];
        for (NSData *data in dataArray) {
            QNClassModel *classModel = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            [array addObject:classModel];
        }
        _playerConfigArray = [array copy];
        
    } else {

        NSDictionary *startDict = @{@"播放起始 (ms)":@[@"0.0",@"0.0"], @"default":@0};
        NSDictionary *videoToolboxDict = @{@"Decoder":@[@"自动",@"硬解",@"软解"], @"default":@0};
        
        NSDictionary *seekDict = @{@"Seek":@[@"关键帧seek",@"精准seek"], @"default":@0};
        
        NSDictionary *actionDict = @{@"Start Action":@[@"启播播放",@"启播暂停"], @"default":@0};

        NSDictionary *renderDict = @{@"Render ratio":@[@"自动",@"拉伸",@"铺满",@"16:9",@"4:3"], @"default":@0};
        
        NSDictionary *speepDict = @{@"播放速度":@[@"0.5",@"0.75",@"1.0",@"1.25",@"1.5",@"2.0"], @"default":@2};
        
        NSDictionary *colorBDict = @{@"色盲模式":@[@"无",@"红色盲",@"绿色盲",@"蓝色盲"], @"default":@0};
        
        NSDictionary *authonDict = @{@"鉴权":@[@"开启",@"关闭"], @"default":@0};
        
        NSDictionary *SEIDict = @{@"SEI":@[@"开启",@"关闭"], @"default":@0};
        
        NSDictionary *backgroundPlayDict = @{@"后台播放":@[@"开启",@"关闭"], @"default":@0};
        
        
        NSArray *piliOptionArray = @[startDict,videoToolboxDict, seekDict,actionDict,renderDict,speepDict,colorBDict,authonDict,SEIDict,backgroundPlayDict];
        
//        NSDictionary *PLPlayerDict = @{@"PLPlayer":piliPlayerArray};
        NSDictionary *PLPlayerOptionDict = @{@"PLPlayerOption":piliOptionArray};
    
        NSArray *configureArray = @[PLPlayerOptionDict];

        // 装入属性配置数组
        _playerConfigArray = [QNClassModel classArrayWithArray:configureArray];
    }
}

-(void)setSelConfiguraKey:(NSString *)tittle selIndex:(int)selIndex{
    for (QNClassModel *classModel in _playerConfigArray){
        for (PLConfigureModel *cMode in classModel.classValue) {
            if ([cMode.configuraKey containsString:tittle]) {
                cMode.selectedNum = @(selIndex);
            }
        }
    }
}


- (void)saveConfigurations {
    NSMutableArray *dataArr = [NSMutableArray array];
    for (QNClassModel * classModel in _playerConfigArray) {
        NSData *data = [NSKeyedArchiver archivedDataWithRootObject:classModel];
        [dataArr addObject:data];
    }
    NSUserDefaults *userdafault = [NSUserDefaults standardUserDefaults];
    [userdafault setObject:[NSArray arrayWithArray:dataArr] forKey:@"PLPlayer_settings"];
    [userdafault synchronize];
}
-(void)setValueConfiguraKey:(NSString *)tittle selValue:(int)value{
    for (QNClassModel *classModel in _playerConfigArray){
        for (PLConfigureModel *cMode in classModel.classValue) {
            if ([cMode.configuraKey containsString:tittle]) {
                if (cMode.configuraValue.count > 1) {
                    cMode.configuraValue[0] = @(value);
                    
                }
            }
        }
    }
    
}

-(int)getConfiguraPostion{
    for (QNClassModel *classModel in _playerConfigArray){
        for (PLConfigureModel *cMode in classModel.classValue) {
            if ([cMode.configuraKey containsString:@"播放起始"]) {
                
                NSLog(@"起播位置-----%d",[cMode.configuraValue[0] intValue]);
                
                return  [cMode.configuraValue[0] intValue];
            }
        }
    }
    
    return 0;
}
-(BOOL)getAuthenticationState{
    for (QNClassModel *classModel in _playerConfigArray){
        for (PLConfigureModel *cMode in classModel.classValue) {
            if ([cMode.configuraKey containsString:@"鉴权"]) {
                if ([cMode.selectedNum intValue] == 0) {
                    return YES;
                }
                else{
                    return NO;
                }
            }
        }
    }
    return NO;
}

@end
