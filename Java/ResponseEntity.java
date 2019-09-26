package com.meizu.lichee.api.action;

public class ResponseEntity {

	/// 默认成功
	public static final ResponseEntity DEFAULT_SUCCESS_RESPONSE_ENTITY = new ResponseEntity();

	// 默认失败，code可自行定义(非200)
	public static final ResponseEntity DEFAULT_FAILED_RESPONSE_ENTITY = new ResponseEntity(500,"");

	private int code = 200;

	private String message = "";

	public ResponseEntity() {
	}

	public ResponseEntity(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
