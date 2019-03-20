package com.meizu.lichee.api.action;

@Controller
@RequestMapping( "/api" )
public class OrderAction {

	private static final Logger logger = LoggerFactory.getLogger( OrderAction.class );

	private static final MEI_ZU_QUERY_URI = "https://api-lichee.meizu.com/api/order/query?package_name=%s&cp_trade_no=%s&ts=%s&sign=%s";

	@ResponseBody
	@RequestMapping(value = {"/order/notify"}, method = RequestMethod.POST)
	public Object handleNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{

		// 读取流数据
		InputStream is = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String params = "";
		while ((params = reader.readLine()) != null) {
			sb.append(params);
		}

		logger.info("notify info: {}", params);

		// 验证sign
		String appKey = "appKey"; // appKey 按照实际值设置

		Map<String, Object> paramMap = new TreeMap<>();
		String sign = "";
		for (String item : sb.toString().split("&")) {

			String[] keyValue = item.split("=");
			if (!keyValue[0].equals("sign") && !keyValue[0].equals("sign_type")) {
				paramMap.put(keyValue[0], keyValue[1]);
			} else {
				if (keyValue[0].equals("sign")) {
					sign = keyValue[1];
				}
			}
		}

		if (sign.equals(SignUtils.signUrl(paramMap,appKey))) {

            try {
                // 调用query接口
                String orderInfo = query(paramMap,appKey);
                // todo 验证订单 && 根据业务需要做后续处理
            } catch (Exception e) {
                logger.error("invoke meizu order query api failed",e);
            }

		} else {

              // todo 异常处理
		}

		// todo 按照文档返回信息，反馈是否收到回调
		return null;
	}

	// 调用query接口获取查询数据
    private String query(Map<String,Object> paramMap,String appKey) throws Exception{

        signParams.put( "package_name", paramMap.get("packageName"));
        signParams.put( "cp_trade_no", paramMap.get("cp_trade_no"));
        signParams.put( "ts", System.currentTimeMillis());
        signParams.put("sign",SignUtils.signUrl(signParams,appKey));

        queryUrl = String.format(MEI_ZU_QUERY_URI, signParams.get("package_name"), signParams.get("cp_trade_no"), signParams.get("ts"), signParams.get("sign"));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(queryUrl);
        CloseableHttpResponse response = httpclient.execute(httpget);
        return EntityUtils.toString(response.getEntity());
    }






}
