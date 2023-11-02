package com.onlinelearning.entity;

import java.util.List;

import com.onlinelearning.entity.SignUpEntity.Address;

public class ResponseMessageWithAddresses {
	
	private String msg;
    private List<Address> addresses;
    
	public ResponseMessageWithAddresses(String msg, List<Address> addresses) {
		this.msg = msg;
		this.addresses = addresses;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
    
    

}
