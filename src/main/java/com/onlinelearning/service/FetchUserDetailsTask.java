package com.onlinelearning.service;

import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity;

public class FetchUserDetailsTask implements Callable<SignUpEntity> {

    private String email;
    private SignUpServiceImplement signUpService1;

    public FetchUserDetailsTask(String email, SignUpServiceImplement signUpService1) {
        this.email = email;
        this.signUpService1 = signUpService1;
    }

    @Override
    public SignUpEntity call() {
        return signUpService1.updateUser(email, null);
    }
}
