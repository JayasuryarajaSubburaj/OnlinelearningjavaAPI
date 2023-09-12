package com.onlinelearning.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class SignUpEntity {
	
	@Id
	private String id;
	private String name;
	private String email;
	private String password;
	private String GeneratedId;
	
	private String resetToken;
	private LocalDateTime tokenExpiryDate;
	private String newPassword;
	
	//Getters and Setters

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public static String generatedid() {
		return UUID.randomUUID().toString();
	}
	public String getGeneratedId() {
		return generatedid();
	}
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public LocalDateTime getTokenExpiryDate() {
		return tokenExpiryDate;
	}
	public void setTokenExpiryDate(LocalDateTime tokenExpiryDate) {
		this.tokenExpiryDate = tokenExpiryDate;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	//Default Constructor
	
	public SignUpEntity() {
		this.GeneratedId = generatedid(); 
	}
	
}
