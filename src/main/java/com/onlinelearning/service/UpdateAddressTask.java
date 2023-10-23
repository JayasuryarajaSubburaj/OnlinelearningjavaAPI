package com.onlinelearning.service;

import java.util.List;
import java.util.concurrent.Callable;

import com.onlinelearning.entity.SignUpEntity.Address;

public class UpdateAddressTask implements Callable<List<Address>> {

	private String email;
	private Address address;
	private SignUpService signUpService;

	public UpdateAddressTask(String email, Address address, SignUpService signUpService) {
		this.email = email;
		this.address = address;
		this.signUpService = signUpService;
	}

	@Override
	public List<Address> call() throws Exception {
		return signUpService.addAddress(email, address);
	}

}
