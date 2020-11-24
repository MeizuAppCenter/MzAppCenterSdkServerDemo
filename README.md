> 魅族应用联运SDK服务端文档



# 版本变更

- [v1.0](https://github.com/MeizuAppCenter/MzAppCenterSdkServerDemo/blob/master/SDK1.0%262.0.md)
  
  - 支付功能
- [v2.0](https://github.com/MeizuAppCenter/MzAppCenterSdkServerDemo/blob/master/SDK1.0%262.0.md)
  
  - 增加支付时可用优惠券抵扣
- [v3.0](https://github.com/MeizuAppCenter/MzAppCenterSdkServerDemo/blob/master/SDK3.0.md)
  
  - 增加自动续费支持
  
  

# 接入

-  [准备工作](http://open-wiki.flyme.cn/doc-wiki/index#id?119)

-  [ SDK接入](http://open-wiki.flyme.cn/doc-wiki/index#id?119)

# 查询

- [魅族开放平台-应用联运](http://developer.meizu.com/console/reports/appBillingDetail)

  ![image-20201124160116293](/Users/meizu/Library/Application Support/typora-user-images/image-20201124160116293.png)



# 结算

- 每月10号之后可查看上个月订单 [对账信息](http://developer.meizu.com/console/apps/finance/reconciliation?t=1606205814758)
- 接入方针对订单确认无误后可生成 [账单信息](http://developer.meizu.com/console/apps/finance/bill?t=1606205830743) 
- 接入方打印账单&发票(如需要)  寄送[魅族财务](http://developer.meizu.com/console/apps/finance/bill?t=1606205830743),进入审核打款流程

# 退款

- 退款请与对接商务沟通，获取退款模板，发送申请退款邮件
- 审核通过后，订单支付款会原路返回，自动返回用户账户；订单退款金额会在后续结算中扣除


