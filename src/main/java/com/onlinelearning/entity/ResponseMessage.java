package com.onlinelearning.entity;

public class ResponseMessage {

	private String msg;
//	private String jwtToken; // New field

//	public ResponseMessage(String msg,String jwtToken) {
//		this.msg = msg;
//		this.jwtToken = jwtToken;
//	}
	
	public ResponseMessage(String msg) {
		this.msg = msg;
	}

	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
