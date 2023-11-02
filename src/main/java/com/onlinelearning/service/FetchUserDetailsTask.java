package com.onlinelearning.service;

import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.repo.SignUpRepository;

public class FetchUserDetailsTask implements Callable<SignUpEntity> {

    private String email;
    private SignUpRepository signUpRepository;

    public FetchUserDetailsTask(String email, SignUpRepository signUpRepository) {
        this.email = email;
        this.signUpRepository = signUpRepository;
    }

//    @Override
//    public SignUpEntity call() {
//        return signUpService1.updateUser(email, null);
//    }
    
    @Override
    public SignUpEntity call() {
        return signUpRepository.findByEmail(email);
    }
}
