package com.onlinelearning.entity;

import java.util.List;

import com.onlinelearning.entity.SignUpEntity.Address;

public class ResponseMessage {

	private String msg;
	private SignUpEntity userDetails;
	
//	private String jwtToken; // New field

//	public ResponseMessage(String msg,String jwtToken) {
//		this.msg = msg;
//		this.jwtToken = jwtToken;
//	}
	
	public ResponseMessage(String msg) {
		this.msg = msg;
	}
	
	public ResponseMessage(String msg, SignUpEntity userDetails) {
        this.msg = msg;
        this.userDetails = userDetails;
    }
	
	
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public SignUpEntity getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(SignUpEntity userDetails) {
		this.userDetails = userDetails;
	}
	
	


}
