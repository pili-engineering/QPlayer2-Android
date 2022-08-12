//
//  PLCellPlayerTableViewCell.m
//  PLPlayerKitCellDemo
//
//  Created by 冯文秀 on 2018/3/12.
//  Copyright © 2018年 Hera. All rights reserved.
//

#import "PLCellPlayerTableViewCell.h"
#import "QNPlayerShortVideoMaskView.h"

@interface PLCellPlayerTableViewCell()<QNPlayerShortVideoMaskViewDelegate>
@property (nonatomic, assign) CGFloat width;
@property (nonatomic, assign) CGFloat height;
@property (nonatomic, strong) QNPlayerShortVideoMaskView* maskView;

@end

@implementation PLCellPlayerTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.contentView.backgroundColor = [UIColor blackColor];
        self.width = PLAYER_PORTRAIT_WIDTH;
        self.height = PLAYER_PORTRAIT_HEIGHT;
        self.stateLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, _height- 20, _width - 10, 20)];
        self.stateLabel.backgroundColor = [UIColor clearColor];
        self.stateLabel.font = PL_FONT_MEDIUM(12);
        self.stateLabel.textColor = [UIColor whiteColor];
        self.stateLabel.textAlignment = NSTextAlignmentLeft;
        self.stateLabel.numberOfLines = 0;
        [self.contentView addSubview:_stateLabel];
        
        self.URLLabel = [[UILabel alloc] initWithFrame:CGRectMake(5, 2, _width - 10, 100)];
        self.URLLabel.backgroundColor = [UIColor clearColor];
        self.URLLabel.font = PL_FONT_MEDIUM(12);
        self.URLLabel.textColor = [UIColor whiteColor];
        self.URLLabel.textAlignment = NSTextAlignmentLeft;
        self.URLLabel.text = self.url;
        self.URLLabel.numberOfLines = 0;
        [self.contentView addSubview:_URLLabel];

        UIView *lineView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, _width, 0.5)];
        lineView.backgroundColor = [UIColor whiteColor];
        [self.contentView addSubview:lineView];
        
        
    }
    return self;
}

- (void)setPlayerView:(RenderView *)playerView {
    _playerView = playerView;
    playerView.frame = self.contentView.bounds;
    if (_playerView) {
        [self.contentView insertSubview:_playerView atIndex:0];
    }
    
}

- (void)setUrl:(NSString *)url {
    _url = url;
    if (url) {
        CGRect bounds = [self.url boundingRectWithSize:CGSizeMake(_width - 10, 1000) options:NSStringDrawingUsesLineFragmentOrigin attributes:[NSDictionary dictionaryWithObject:PL_FONT_MEDIUM(12) forKey:NSFontAttributeName] context:nil];
        self.URLLabel.frame = CGRectMake(5, 2, _width - 10, bounds.size.height);
        self.URLLabel.text = self.url;
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    if (!self.maskView) {
        [self addPlayerMaskView:_player];
    }
    // Configure the view for the selected state
}
-(void)setState:(BOOL)state{
    if (self.maskView) {
        [self.maskView setPlayButtonState:state];
    }
    
}
-(void)setPlayer:(QPlayerContext *)player{
    if (!self.maskView) {
        [self addPlayerMaskView:player];
    }else{
        self.maskView.player = player;
    }
}
#pragma mark - 添加点播界面蒙版

- (void)addPlayerMaskView:(QPlayerContext *)player{
    self.maskView = [[QNPlayerShortVideoMaskView alloc] initWithShortVideoFrame:CGRectMake(0, PL_SCREEN_HEIGHT-90, PL_SCREEN_WIDTH, 50) player:player isLiving:NO];
    self.maskView.delegate = self;
    self.maskView.backgroundColor = [UIColor clearColor];
    [self.contentView addSubview:_maskView];
    
}
#pragma mark - QNPlayerShortVideoMaskViewDelegate

-(void)reOpenPlayPlayerMaskView:(QNPlayerShortVideoMaskView *)playerMaskView{
    [_maskView setPlayButtonState:YES];

}
@end
