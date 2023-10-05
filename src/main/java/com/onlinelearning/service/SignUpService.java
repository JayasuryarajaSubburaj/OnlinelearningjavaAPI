package com.onlinelearning.service;

import java.util.List;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.Address;


public interface SignUpService {

	String saveUser(SignUpEntity SignUp);

	void deleteUserByEmail(String email);

	SignUpEntity updateUser(String email, SignUpEntity updatedUser);
	
//	void updateAddress(String email, AddressUpdateRequest request);
	
	List<Address> addAddress(String email, Address newAddress);
	
	List<Address> editAddressById(String email, String addressId, Address updatedAddress);
	
	List<Address> deleteAddressById(String email, String addressId);
	
	List<Address> getAllAddressesByEmail(String email);
	
	boolean validateLogin(String email, String password);
	
	String getTokenForUser(String email);
	
	void logoutUser(String email);

	String decodeJWTAndGetEmail(String token);

}
