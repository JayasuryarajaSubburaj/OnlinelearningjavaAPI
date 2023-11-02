package com.onlinelearning.service;

import java.util.List;
import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity.Address;

public class GetAddressesTask implements Callable<List<Address>> {

    private String email;
    private SignUpServiceImplement signUpService1;

    public GetAddressesTask(String email, SignUpServiceImplement signUpService1) {
        this.email = email;
        this.signUpService1 = signUpService1;
    }

    @Override
    public List<Address> call() {
        return signUpService1.getAllAddressesByEmail(email);
    }
}
