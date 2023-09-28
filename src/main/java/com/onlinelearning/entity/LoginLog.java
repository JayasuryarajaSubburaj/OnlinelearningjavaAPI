package com.onlinelearning.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "loginLogs")
public class LoginLog {

	@Id
	private String id;

	private String email;
	private String loginTimestamp;
	private String password;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLoginTimestamp() {
		return loginTimestamp;
	}
	public void setLoginTimestamp(String loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

}
