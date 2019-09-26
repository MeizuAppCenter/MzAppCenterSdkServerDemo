package com.meizu.lichee.api.action;

/**
 * 异常code 可自行定义
 */
public interface ResponseErrorCode {

	int START = 211000;

	int END = 211999;

	/** 不合法*/
	int APP_SIGN_ERROR = START + 1;

	/**
	 * 订单状态不正确
	 */
	int STATUS_NOT_CORRECT = START + 2;
	
	/**
	 * 参数不正确
	 */
	int INVALID_PARAM = START + 3;
}
