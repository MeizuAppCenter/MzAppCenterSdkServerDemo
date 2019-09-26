package com.meizu.lichee.api.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.meizu.ocean.common.utils.HttpScratcher;
import com.meizu.ocean.common.utils.MD5Utils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 此代码为示例代码
 * 用于处理魅族支付回调
 */
@Controller
@RequestMapping("/api")
public class NotifyAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderAction.class);

	private final HttpScratcher httpScratcher = new HttpScratcher(30, 30, 20, 100);

	private static final Set<String> EXCLUDE_SET = Sets.newHashSet("sign_type", "sign");

	// APP_KEY 按照实际值设置
	private static final String APP_KEY = "a6663fd91e814ecba4b93fdf764e4f94";

	private static final String MEIZU_QUERY_URL = "https://api-lichee.meizu.com/api/order/query?package_name=%s&cp_trade_no=%s&ts=%s&sign=%s";

	@ResponseBody
	@RequestMapping(value = {"/order/notify/sample"}, method = RequestMethod.POST)
	public Object handleNotify(HttpServletRequest request) throws Exception {

		// 读取流数据
		TreeMap<String, String> paramMap = parseParams(request);

		// 验证签名
		String sign = sign(paramMap);
		if (!StringUtils.equalsIgnoreCase(sign, String.valueOf(paramMap.get("sign")))) {
			LOGGER.error("notify failed for sign not equal.params:[{}]", JSON.toJSONString(paramMap));
			return new ResponseEntity(ResponseErrorCode.APP_SIGN_ERROR,"sign not correct");
		}

		// 调用魅族订单查询接口确认订单状态
		if (!checkFromMeizu(paramMap.get("package_name"), paramMap.get("cp_trade_no"), NumberUtils.toInt(paramMap.get("trade_status")))) {
			LOGGER.error("notify failed for check order from meizu error.params:[{}]", JSON.toJSONString(paramMap));
			return new ResponseEntity(ResponseErrorCode.STATUS_NOT_CORRECT,"query status not equals notify status");
		}

		// 后置处理
		if (!postProcess(paramMap)) {
			LOGGER.error("notify failed for post process failed.params:[{}]", JSON.toJSONString(paramMap));
			return ResponseEntity.DEFAULT_FAILED_RESPONSE_ENTITY;
		}
		return new ResponseEntity();
	}


	/**
	 * 此方法用于处理cp自有业务逻辑 请自行根据需求实现
	 * @param paramMap
	 * @return
	 */
	private boolean postProcess(Map<String, String> paramMap) {
		return true;
	}

	/**
	 * 解析inputStream中的请求参数
	 *
	 * @param request
	 * @return
	 */
	private TreeMap<String, String> parseParams(HttpServletRequest request) {

		TreeMap<String, String> paramMap = new TreeMap<>();
		try {
			InputStream is = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String params;
			while ((params = reader.readLine()) != null) {
				sb.append(params);
			}

			for (String item : sb.toString().split("&")) {
				String[] keyValue = item.split("=");
				paramMap.put(keyValue[0], keyValue[1]);
			}

		} catch (Exception e) {
			LOGGER.error("notify failed for parse input stream error", e);
		}

		return paramMap;
	}

	/**
	 * 生成sign
	 *
	 * @param paramMap
	 * @return
	 */
	private String sign(TreeMap<String, String> paramMap) {
		if (MapUtils.isEmpty(paramMap)) {
			return StringUtils.EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			if (!EXCLUDE_SET.contains(entry.getKey())) {
				sb.append(entry.getKey())
						.append("=")
						.append(entry.getValue())
						.append("&");
			}
		}
		String finalSignString = sb.deleteCharAt(sb.length() - 1).append(":").append(APP_KEY).toString();
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("final sign string:[{}]",finalSignString);
		}
		return MD5Utils.digest(finalSignString, "utf-8");
	}

	/**
	 * 调用魅族query接口查询订单详情
	 * @param packageName
	 * @param cpTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 */
	private boolean checkFromMeizu(String packageName, String cpTradeNo, int tradeStatus) throws Exception {

		TreeMap<String, String> signParams = new TreeMap<>();
		signParams.put("package_name", packageName);
		signParams.put("cp_trade_no", cpTradeNo);
		signParams.put("ts", String.valueOf(System.currentTimeMillis()));
		signParams.put("sign", sign(signParams));
		String queryUrl = String.format(MEIZU_QUERY_URL, packageName, cpTradeNo, signParams.get("ts"), signParams.get("sign"));
		JSONObject response = JSON.parseObject(httpScratcher.doHttpGet(queryUrl,null));
		if (response == null || response.getIntValue("code") != 200) {
			LOGGER.error("notify failed for response is not correct", response);
			return false;
		}

		JSONObject value = response.getJSONObject("value");
		if (value.getIntValue("trade_status") != tradeStatus) {
			LOGGER.error("notify failed for trade_status not correct.cpTradeNo:[{}],notify trade status:[{}],query trade status:[{}]", cpTradeNo, tradeStatus, value.getInteger("trade_status"));
			return false;
		}

		return true;
	}



}
