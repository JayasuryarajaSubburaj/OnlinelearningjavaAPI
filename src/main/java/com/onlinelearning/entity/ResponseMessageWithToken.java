package com.onlinelearning.entity;

import java.util.List;

import com.onlinelearning.entity.SignUpEntity.Address;

public class ResponseMessageWithToken {

	private String msg;
    private String Token;
    private SignUpEntity userDetails;
    private List<Address> addresses;

    public ResponseMessageWithToken(String msg, String Token) {
        this.msg = msg;
        this.Token = Token;
    }
    
    public ResponseMessageWithToken(SignUpEntity userDetails, String msg) {
        this.userDetails = userDetails;
        this.msg = msg;
    }
    
    public ResponseMessageWithToken(List<Address> addresses, String msg) {
        this.addresses = addresses;
        this.msg = msg;
    }
    
    public ResponseMessageWithToken(String msg) {
        this.msg = msg;
    }

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public SignUpEntity getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(SignUpEntity userDetails) {
		this.userDetails = userDetails;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String Token) {
		this.Token = Token;
	}
    
    
}
