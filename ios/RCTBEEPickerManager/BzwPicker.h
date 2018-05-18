//
//  BzwPicker.h
//  PickerView
//
//  Created by Bao on 15/12/14.
//  Copyright © 2015年 Microlink. All rights reserved.
//

#import <UIKit/UIKit.h>

#define SCREEN_WIDTH ([UIScreen mainScreen].bounds.size.width)
#define SCREEN_HEIGHT ([UIScreen mainScreen].bounds.size.height)


typedef void(^backBolock)(NSDictionary * );

@interface BzwPicker : UIView<UIPickerViewDataSource,UIPickerViewDelegate>

@property (strong,nonatomic)UIPickerView *pick;

@property(nonatomic,copy)backBolock bolock;

@property (strong, nonatomic) NSDictionary *pickerDic;//一开始进来的字典

@property(strong,nonatomic)NSArray *dataDry;//一进来的就没有数组和字典的区别肯定是一个字典

@property (strong, nonatomic) NSMutableArray *provinceArray;//省、市
@property (strong, nonatomic) NSMutableArray *cityArray;//市，县
@property (strong, nonatomic) NSArray *townArray;//县，区


@property(strong,nonatomic)NSArray *selectthreeAry;

@property (strong,nonatomic)NSArray *selectArry;//2级联动时候用的

@property (strong,nonatomic)UIButton *leftBtn;//取消
@property (strong,nonatomic)UIButton *rightBtn;

@property(strong,nonatomic)NSString *leftStr;
@property(strong,nonatomic)NSString *centStr;
@property(strong,nonatomic)NSString *rightStr;
@property(strong,nonatomic)NSString *pickerToolBarFontSize;
@property(strong,nonatomic)NSString *pickerFontSize;
@property(strong,nonatomic)NSString *pickerFontFamily;
@property(strong,nonatomic)NSArray *pickerFontColor;
@property(strong,nonatomic)NSString *pickerRowHeight;



@property(assign,nonatomic)BOOL Correlation;//判断有没有没有关联

@property(nonatomic,strong)NSString *numberCorrela;//关联是2行 还是3行

@property(nonatomic,strong)NSArray *noCorreArry;

//创建一个数组来传递返回的值
@property(nonatomic,strong)NSMutableArray *backArry;

@property(assign,nonatomic)BOOL noArryElementBool;

@property(strong,nonatomic)NSMutableArray *infoArry;

//创建一个数组 接收进来的选择Value

@property(strong,nonatomic)NSArray *selectValueArry;

@property(strong,nonatomic)NSArray *weightArry;

@property(assign,nonatomic)CGFloat lineWith;
//创建一个下角标记录是第几行 来一进来判断第一行被选中 当进来的是关联两行的逻辑的时候 或者三行关联的时候取第二行做记录

@property(assign,nonatomic)NSInteger num;

//创建一个下角标 第三行做记录

@property(assign,nonatomic)NSInteger threenum;

@property(assign,nonatomic)NSInteger seleNum;//用来做索引下标用



-(instancetype)initWithFrame:(CGRect)frame dic:(NSDictionary *)dic leftStr:(NSString *)leftStr centerStr:(NSString *)centerStr rightStr:(NSString *)rightStr topbgColor:(NSArray *)topbgColor bottombgColor:(NSArray *)bottombgColor leftbtnbgColor:(NSArray *)leftbtnbgColor rightbtnbgColor:(NSArray *)rightbtnbgColor centerbtnColor:(NSArray *)centerbtnColor selectValueArry:(NSArray *)selectValueArry  weightArry:(NSArray *)weightArry
       pickerToolBarFontSize:(NSString *)pickerToolBarFontSize  pickerFontSize:(NSString *)pickerFontSize  pickerFontColor:(NSArray *)pickerFontColor  pickerRowHeight:(NSString *)pickerRowHeight  pickerFontFamily:(NSString *)pickerFontFamily;

-(void)selectRow;
@end
