package com.onlinelearning.entity;

import java.util.List;
import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity.Address;
import com.onlinelearning.service.SignUpServiceImplement;

public class DeleteAddressTask implements Callable<List<Address>> {
	

    private String email;
    private String addressId;
    private SignUpServiceImplement signUpService1;

    public DeleteAddressTask(String email, String addressId, SignUpServiceImplement signUpService1) {
        this.email = email;
        this.addressId = addressId;
        this.signUpService1 = signUpService1;
    }

    @Override
    public List<Address> call() throws Exception {
        return signUpService1.deleteAddressById(email, addressId);
    }

}
