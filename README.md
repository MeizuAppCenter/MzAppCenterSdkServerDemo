# MzAppCenterSdkServerDemo

魅族联运服务端接入 Demo。演示了如何查询订单与接收发货通知。

## 1.订单查询接口 
### 请求方式
get 请求
### 接口url

https://api-lichee.meizu.com/api/order/query

### 请求参数
| 参数名 | 类型 | 是否必填 | 说明 |
| ------ | ------ | ------ | ------ |
| package_name | String | 是 | 应用包名 |
| cp_trade_no | String | 是 | cp订单号 |
| ts | Long | 是 | 时间戳(毫秒) |
| sign | String | 是 | 参与签名的字段：package_name、cp_trade_no、ts |

### 接口签名规则
    1.对所有待签名参数按照字段名的ASCII码从小到大排序(字典序)后,使用URL键值对的格式(即key1=value1&key2=value2…)拼接成字符串string。

    2.拼接加密的key:string1=string+":"+appKey（appKey表示提供给开发者签名key）

    3.接下来对string1作md5(32位小写字母+数字)加密，即 sign=md5(string1)。（UTF-8编码）

### 响应参数

| 参数名 | 数据类型 | 是否必填 | 说明 |
| ------ | ------ | ------ | ------ |
| packageName	| String | 是 | 应用包名
| cp_trade_no	| String | 是 | CP订单ID
| trade_no	| String | 是 | 魅族sdk订单ID
| total_fee	| Double | 是 | 订单金额(CP需校验是否一致)
| product_id | String | 是 | 产品ID(下单时CP上传)
| pay_time	| Long | 是 | 支付时间
| trade_status	| int | 是 | 订单状态：[1:新建/2:预支付/3:预支付创建失败/4:支付成功]

### 接口返回示例
正常访问结果如下,如果出现value为空的情况,则是未查询到订单信息 请检查订单号
```
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

## 2.CP 接收发货通知接口：（后台填写的回调接口）

__注意：
 1. 回调地址在开发者后台填写，暂时只支持应用上架前修改，上架后请在沟通群联系魅族同学修改
 2. 回调地址必须为post请求，且能正常访问时才能在开发者后台保存
 3. 回调接口请求方式为 `POST`，为防止伪造数据请求，请在收到通知时多重校验，推荐如下： sign验证+调用订单查询接口验证状态
 4. 回调接口处理逻辑可参考提供的java demo

### 请求参数
| 参数名 | 数据类型 | 是否必填 | 说明 |
| ------ | ------ | ------ | ------ |
| cp_trade_no | String | Y | cp订单号
| trade_no | String	| Y	| SDK订单号
| package_name | String | Y | 应用包名
| product_id | String |  Y | 应用订单产品ID
| total_fee  | Double | Y | 订单金额
| trade_status | Integer | Y | 订单状态订单状态：[1:新建/2:预支付/3:预支付创建失败/4:支付成功]
| pay_time | Long | Y | 支付时间，CP需要保存，月底对账按照此维度时间来统计
| create_time | Long | Y | 订单创建时间(毫秒)
| sign | String | Y	|  参与签名的字段: 除sign以及sign_type外其他字段均参与校验

### sign生成详细方法

1.对请求参数按照**字典集排序**,拼接成key1=value1&key2=value2的形式 拼接成signParam示例如下
cp_trade_no=xxx&create_time=xxx&package_name=xxx&pay_time=xxx&product_id=xxx&total_fee=xxx&tra de_no=xxx&trade_status=xxx

2.拼接appkey，形式为signParam+":"+appkey,结果如下所示signData
cp_trade_no=xxx&create_time=xxx&package_name=xxx&pay_time=xxx&product_id=xxx&total_fee=xxx&tra de_no=xxx&trade_status=xxx:appkey

3.对signData进行md5加密 sign = MD5Utils.digest( signData, "UTF-8" );

**备注：**

为防止伪造回调请求，请cp在收到回调后**调用订单查询接口核查订单状态进行双重验证**

对于参数值为null的情况，请将null转化成字符串null拼接在signParam中，以create_time为例
cp_trade_no=xxx&**create_time=null**&package_name=xxx&pay_time=xxx&product_id=xxx&total_fee=xxx&tra de_no=xxx&trade_status=xxx


### 响应参数：
| 参数名 | 数据类型 | 是否必填 | 说明 |
| ------ | ------ | ------ | ------ |
| code | Integer | Y | code由cp返回，表明是否通知成功；200为通知成功，其它都为失败 |

### 常见错误码

| 错误码 | 错误信息 |
| ------ | ------ | 
| 211012	| 应用未签署有效合同 |
| 211014	| apk签名/开发者签名key为空 |
| 211013	| 应用签名不合法 |

