> 魅族SDK 1.0&2.0服务端接口文档



# 时序图

​     [支付流程时序图](https://www.processon.com/view/link/5b9b59ace4b0d4d65c062d37)


# 加密算法

    1.对所有待签名参数按照字段名的ASCII码从小到大排序(字典序)后,使用URL键值对的格式(即key1=value1&key2=value2…)拼接成字符串string。
    
    2.拼接加密的key:string1=string+":"+appKey（appKey表示提供给开发者签名key）
    
    3.接下来对string1作md5(32位小写字母+数字)加密，即 sign=md5(string1)。（UTF-8编码）
    
    4.回调时 sign sign_type字段不参与签名

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
  
      
