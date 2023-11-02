package com.onlinelearning.service;

import java.util.concurrent.Callable;

public class DeleteUserTask implements Callable<Boolean> {

    private String email;
    private SignUpServiceImplement signUpService1;

    public DeleteUserTask(String email, SignUpServiceImplement signUpService1) {
        this.email = email;
        this.signUpService1 = signUpService1;
    }

    @Override
    public Boolean call() {
        try {
            signUpService1.deleteUserByEmail(email);
            return true; 
        } catch(Exception e) {
            // Log the exception or handle as required
            return false;
        }
    }
}
