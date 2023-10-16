package com.onlinelearning.service;

import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity;

public class LoginTask implements Callable<Boolean> {
	
	private SignUpEntity userCredentials;
	private SignUpServiceImplement loginService;

	public LoginTask(SignUpEntity userCredentials, SignUpServiceImplement loginService) {
		this.userCredentials = userCredentials;
		this.loginService = loginService;
	}

	@Override
	public Boolean call() {
		return loginService.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
	}

}
