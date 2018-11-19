package com.meizu.lichee.api.action;

@Controller
@RequestMapping( "/api" )
public class OrderAction {

	private static final Logger logger = LoggerFactory.getLogger( OrderAction.class );


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

			// todo 调用查询接口 验证订单状态; 根据业务需要做后续处理
		} else {

      // todo 异常处理
		}

		// todo 按照文档返回信息，反馈是否收到回调
		return null;
	}



}
