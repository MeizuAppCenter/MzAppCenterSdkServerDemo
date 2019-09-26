
import com.google.common.collect.Sets;
import com.meizu.ocean.common.utils.HttpScratcher;
import com.meizu.ocean.common.utils.MD5Utils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(MyTest.class);

	private final HttpScratcher httpScratcher = new HttpScratcher(30, 30, 20, 100);

	private static final Set<String> EXCLUDE_SET = Sets.newHashSet("sign_type", "sign");

	private static final String MEIZU_QUERY_URL = "https://api-lichee.meizu.com/api/order/query?package_name=%s&cp_trade_no=%s&ts=%s&sign=%s";

	private static final String NOTIFY_URL = "https://api-lichee.meizu.com/api/order/notify/sample";

	// APP_KEY 按照实际值设置
	private static final String APP_KEY = "";

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
			LOGGER.debug("final sign string:[{}]", finalSignString);
		}
		return MD5Utils.digest(finalSignString, "utf-8");
	}


	/**
	 * 查询接口单元测试
	 */
	@Test
	public void testQueryOrder() {

		String packageName = "com.meizu.mstore";
		String cpTradeNo = "1569400933257";
		TreeMap<String, String> signParams = new TreeMap<>();
		signParams.put("package_name", packageName);
		signParams.put("cp_trade_no", cpTradeNo);
		signParams.put("ts", String.valueOf(System.currentTimeMillis()));
		signParams.put("sign", sign(signParams));

		String queryUrl = String.format(MEIZU_QUERY_URL, packageName, cpTradeNo, signParams.get("ts"), signParams.get("sign"));
		System.out.println("queryUrl = " + queryUrl);

		try {
			String response = httpScratcher.doHttpGet(queryUrl, null);
			System.out.println("response = " + response);
		} catch (Exception e) {
			LOGGER.error("request error", e);
		}
	}


	/**
	 * 回调接口单元测试
	 * @param
	 */
	@Test
	public void testNotify() {

		TreeMap<String, String> params = new TreeMap<>();
		params.put("cp_trade_no", "1569400933257");
		params.put("trade_no", "c14c269404157558");
		params.put("package_name", "com.meizu.mstore.sdk.demo");
		params.put("product_id", "1569400933257");
		params.put("total_fee", "0.0600");
		params.put("trade_status", "4");
		params.put("pay_time", "1569400950002");
		params.put("create_time", null);
		String sign = sign(params);
		params.put("sign_type", "md5");
		params.put("sign", sign);

		StringBuilder paramString = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (paramString.length() > 0) {
			paramString = paramString.deleteCharAt(paramString.length() - 1);
		}
		String response = null;
		try {
			response = httpScratcher.doHttpPost(NOTIFY_URL, null, paramString.toString());
		} catch (Exception e) {
			LOGGER.error("request failed",e);
		}

		System.out.println("response = " + response);
	}

}
