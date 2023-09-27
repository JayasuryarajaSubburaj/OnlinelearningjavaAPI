package com.onlinelearning.service;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.AddressUpdateRequest;

public interface SignUpService {

	String saveUser(SignUpEntity SignUp);

	void deleteUserByEmail(String email);

	SignUpEntity updateUser(String email, SignUpEntity updatedUser);
	
	void updateAddress(String email, AddressUpdateRequest request);

}
