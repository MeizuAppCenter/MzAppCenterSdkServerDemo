# 



> 魅族SDK3.0服务端接口文档



# 时序图

​     [支付续费流程时序图](https://www.processon.com/view/link/5f9785a77d9c0806f2912bd2)



# 常见问题解答

- ##### 自动续费的流程是怎样的

  - 用户在应用内支付并签约，生成一笔签约信息，用于后续扣费;
  - 签约成功后，魅族方会将签约信息回调给接入方，请接入方保存相关信息;
  - 接入方服务端按照签约信息的规则，定时调用魅族服务端接口进行自动扣款;

- ##### 自动续费接口什么时候调用

  - 首次调用: 签约时传递的首次扣款时间
  - 后续调用：首次扣款时间按照扣款周期顺延

- ##### 如果服务宕机 错过了此次扣款时间怎么处理

  - 未避免此种情况出现，自动扣款允许在预定扣款时间前3天(包括当天)调用自动扣款接口，  

    给与业务方 足够的异常处理时间。

  - 示例：预定扣款时间为1月15日，扣款周期为1个月，则可以在1月13~14日调用自动扣款接口。  

    提前调用自动扣费接口不影响下次扣款时间，如上述 下次扣款时间为2月15日

  

# 加密算法

    1.对所有待签名参数按照字段名的ASCII码从小到大排序(字典序)后,使用URL键值对的格式(即key1=value1&key2=value2…)拼接成字符串string。
    
    2.拼接加密的key:string1=string+":"+appKey（appKey表示提供给开发者签名key）
    
    3.接下来对string1作md5(32位小写字母+数字)加密，即 sign=md5(string1)。（UTF-8编码）
    
    4.回调时sign sign_type不参与签名



# 接口文档

- ## 订单查询接口

  - 接口地址：https://api-lichee.meizu.com/api/order/query

  - 请求方式：GET

  - 请求参数

    |    参数名    |     描述     |  类型  | 是否必填 | 是否签名 |
    | :----------: | :----------: | :----: | :------: | :------: |
    | package_name |     包名     | string |    是    |    是    |
    | cp_trade_no  | 接入方订单号 | string |    是    |    是    |
    |      ts      |  时间戳(ms)  |  Long  |    是    |    是    |
    |     sign     |     签名     | string |    是    |    否    |

    

  - 响应结果

    |    参数名    |                          描述                           |  类型  | 是否必填 |
    | :----------: | :-----------------------------------------------------: | :----: | :------: |
    | packageName  |                          包名                           | string |    是    |
    | cp_trade_no  |                      接入方订单号                       | string |    是    |
    |   trade_no   |                      魅族sdk订单号                      | string |    是    |
    |  total_fee   |                        支付金额                         | double |    是    |
    |  product_id  |                         商品id                          | string |    是    |
    |   pay_time   |                        支付时间                         |  long  |    是    |
    | trade_status | 订单状态：[1:新建/2:预支付/3:预支付创建失败/4:支付成功] |  int   |    是    |

    ```
    // 正常访问结果如下,如果出现value为空的情况,则是未查询到订单信息 请检查订单号
    {
    	"code": 200,
    	"message": "",
    	"redirect": "",
    	"value": {
    		"cp_trade_no": "1534994759572", // CP订单ID
    		"packageName": "com.meizu.mstore.sdk.demo", // 包名
    		"pay_time": 0, // 订单完成时间 
    		"product_id": "153499", // 商品id 
    		"total_fee": 0.2,  // 订单支付总金额  
    		"trade_no": "1534994759572", // 魅族sdk订单号
    		"trade_status": 2 // 订单状态 [1:新建/2:预支付/3:预支付创建失败/4:支付成功]
    	}
    }
    ```

    

- ## 订单回调接口

  - 接口地址：由接入方实现，用于接收订单支付结果回调

  - 请求方式：POST

  - 备注：

    - 回调地址必须不带任何参数
    - 回调地址签名必须针对回调的所有参数进行签名，便于后续扩展
    - 回调处理请参考 [回调示例](https://github.com/MeizuAppCenter/MzAppCenterSdkServerDemo/blob/master/Java/%E5%9B%9E%E8%B0%83-example.java)

  - 请求参数

    |    参数名    |                         描述                          |  类型  | 是否必填 | 是否签名 |
    | :----------: | :---------------------------------------------------: | :----: | :------: | :------: |
    | cp_trade_no  |                     接入方订单号                      | string |    是    |    是    |
    |   trade_no   |                     魅族sdk订单号                     | string |    是    |    是    |
    | package_name |                         包名                          | string |    是    |    是    |
    |  product_id  |                        商品id                         | string |    是    |    是    |
    |  total_fee   |                       订单金额                        | double |    是    |    是    |
    | trade_status | 订单状态[1:新建/2:预支付/3:预支付创建失败/4:支付成功] |  int   |    是    |    是    |
    |   pay_time   |              订单支付时间(用于后续对账)               |  long  |    是    |    是    |
    | create_time  |                     订单创建时间                      |  long  |    是    |    是    |
    |     sign     |                         签名                          | string |    是    |    否    |

    

  - 响应结果

    - 返回结果格式为json

    ```
    {
      "code": 200, //数字类型，200表示正常 非200表示异常，针对异常的订单后续会有一定规则的重试
      "message":"" //string类型，非200时，请填充该字段，便于排查问题
    }
    ```

    

- ## 签约查询接口

  - 接口地址：https://api-lichee.meizu.com/api/public/cyclePay/sign/query

  - 请求方式：GET

  - 请求参数

    |    参数名    |     描述     |  类型  | 是否必填 | 是否签名 |
    | :----------: | :----------: | :----: | :------: | :------: |
    | package_name |     包名     | string |    是    |    是    |
    |  cp_sign_no  | 接入方订单号 | string |    是    |    是    |
    |     sign     |     签名     | string |    是    |    否    |

  -   响应结果

    |   参数名称    |             描述             |  类型  | 是否必填 |
    | :-----------: | :--------------------------: | :----: | :------: |
    | package_name  |             包名             | string |    是    |
    |  cp_sign_no   |         接入方签约号         | string |    是    |
    |    sign_no    |        魅族sdk签约号         | string |    是    |
    | execute_time  |         首次扣款时间         | string |    是    |
    |  period_type  |   扣款周期类型(MONTH/DAY)    | string |    是    |
    |    period     |         扣款周期间隔         | string |    是    |
    | single_amount |           扣款金额           | string |    是    |
    |    status     | 签约状态(0新建 1正常 2解约 ) | string |    是    |
    | invalid_time  |         合约失效时间         | string |    是    |
    |  valid_time   |         合约生效时间         | string |    是    |
    |   sign_time   |           签约时间           | string |    是    |
    |  notify_time  |           回调时间           | string |    是    |

    ```
    // 正常
    {
    	"code": 200,
    	"message": "",
    	"redirect": "",
    	"value": {
    		"cp_sign_no": "20201116171457",
    		"execute_time": "2020-11-18 00:00:00",
    		"invalid_time": "2115-02-01 00:00:00",
    		"notify_time": "2020-11-17 18:07:17",
    		"package_name": "com.meizu.mstore.sdk.demo",
    		"period": "1",
    		"period_type": "MONTH",
    		"sign_no": "157942375112245268",
    		"sign_time": "2020-11-16 17:15:36",
    		"single_amount": "1.00",
    		"status": "1",
    		"valid_time": "2020-11-16 17:15:36"
    	}
    }

    // 异常
    {
    	"code": 123, // code为非200 表示异常
    	"message": "" // 异常提示信息
    }
    
    ```
    
    

- ## 签约回调接口

  - 接口地址：由接入方实现，用于接收签约回调

  - 请求方式:  POST

  - 备注：

    - 回调地址必须不带任何参数

    - 回调地址签名必须针对回调的所有参数进行签名，便于后续扩展

  - 请求参数

    |   参数名称    |             描述             |  类型  | 是否必填 | 是否签名 |
    | :-----------: | :--------------------------: | :----: | :------: | :------: |
    | package_name  |             包名             | string |    是    |    是    |
    |  cp_sign_no   |         接入方签约号         | string |    是    |    是    |
    |    sign_no    |          魅族签约号          | string |    是    |    是    |
    |  period_type  |         扣款周期类型         | string |    是    |    是    |
    |    period     |           扣款周期           | string |    是    |    是    |
    | execute_time  |         首次扣款时间         | string |    是    |    是    |
    | single_amount |           扣款金额           | string |    是    |    是    |
    |    status     | 签约状态(0新建 1正常 2解约 ) | string |    是    |    是    |
    |  valid_time   |           生效时间           | string |    是    |    是    |
    | invalid_time  |           失效时间           | string |    是    |    是    |
    |   sign_time   |           签约时间           | string |    是    |    是    |
    |  notify_time  |         回调通知时间         | string |    是    |    是    |
    |     sign      |             签名             | string |    是    |    否    |
    |   sign_type   |           签名方式           | string |    是    |    否    |

  - 请求响应

    - 请求响应格式为json

      ```
      {
        "code": 200, //数字类型，200表示正常 非200表示异常，针对异常的订单后续会有一定规则的重试
        "message":"" //string类型，非200时，请填充该字段，便于排查问题
      }
      
      ```
    

-   续费接口

  - 接口地址: https://api-lichee.meizu.com/api/public/cyclePay/pay

  - 请求方式: post

  - 请求参数: 

    |    参数名称    |                         描述                          |  类型  | 是否必填 | 是否签名 |
    | :------------: | :---------------------------------------------------: | :----: | :------: | :------: |
    |  package_name  |                         包名                          | string |    是    |    是    |
    |  cp_trade_no   |              接入方订单号(必须保证唯一)               | string |    是    |    是    |
    |   cp_sign_no   |              接入方签约号(必须保证唯一)               | string |    是    |    是    |
    | cp_create_time |               接入方订单创建时间戳(ms)                |  long  |    是    |    是    |
    |   cp_attach    |                    接入方拓展字段                     | string |    是    |    是    |
    |   product_id   |                        商品id                         | string |    是    |    是    |
    |  product_name  |                       商品名称                        | string |    是    |    是    |
    |  product_body  |                       商品描述                        | string |    是    |    是    |
    |  product_unit  |                       商品单位                        | string |    是    |    是    |
    |   total_fee    | 扣款金额(必须小于等于签约续费金额 且签名时格式为x.xx) | string |    是    |    是    |
    |   notify_url   |                   支付成功回调地址                    | string |    是    |    是    |
    |      sign      |                         签名                          | String |    是    |    是    |

    

  - 请求响应

    ```
    {
    	"code": 200, // 200表示请求成功 非200表示异常
    	"message": "" // 当code为非200时，此处填充异常提示信息
    }
    ```

    
