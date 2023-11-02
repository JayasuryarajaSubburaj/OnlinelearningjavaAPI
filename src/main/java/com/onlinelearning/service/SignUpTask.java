package com.onlinelearning.service;

import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity;

public class SignUpTask implements Callable<String> {

	private SignUpEntity signUpEntity;
	private SignUpServiceImplement signUpService1;

	public SignUpTask(SignUpEntity signUpEntity, SignUpServiceImplement signUpService1) {
		this.signUpEntity = signUpEntity;
		this.signUpService1 = signUpService1;
	}

	@Override
	public String call() {
		return signUpService1.saveUser(signUpEntity);
	}
}
