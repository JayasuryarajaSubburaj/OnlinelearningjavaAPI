package com.onlinelearning.entity;

import java.util.List;
import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity.Address;
import com.onlinelearning.service.SignUpServiceImplement;

public class EditAddressTask implements Callable<List<Address>> {

	private String email;
    private String addressId;
    private Address address;
    private SignUpServiceImplement signUpService;

    public EditAddressTask(String email, String addressId, Address address, SignUpServiceImplement signUpService) {
        this.email = email;
        this.addressId = addressId;
        this.address = address;
        this.signUpService = signUpService;
    }

    @Override
    public List<Address> call() {
        return signUpService.editAddressById(email, addressId, address);
    }
}
