package com.onlinelearning.service;

import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity;

public class UpdateUserDetailsTask implements Callable<SignUpEntity> {

	private String email;
    private SignUpEntity userDetails;
    private SignUpServiceImplement signUpService;

    public UpdateUserDetailsTask(String email, SignUpEntity userDetails, SignUpServiceImplement signUpService) {
        this.email = email;
        this.userDetails = userDetails;
        this.signUpService = signUpService;
    }

    @Override
    public SignUpEntity call() {
        return signUpService.updateUser(email, userDetails);
    }
}
