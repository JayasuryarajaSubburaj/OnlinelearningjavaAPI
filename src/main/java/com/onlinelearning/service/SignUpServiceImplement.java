package com.onlinelearning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.AddressUpdateRequest;
import com.onlinelearning.repo.SignUpRepository;

@Service
public class SignUpServiceImplement implements SignUpService {

	@Autowired
	SignUpRepository signuprepo;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Override
	public String saveUser(SignUpEntity SignUp) {

		// Check if email exists
		if (signuprepo.findByEmail(SignUp.getEmail()) != null) {
			return "Email already registered";
		}

		// Check if password is null before encoding
		if (SignUp.getPassword() == null) {
			return "Password cannot be null";
		}

		// Password hashing
		SignUp.setPassword(passwordEncoder.encode(SignUp.getPassword()));

		signuprepo.save(SignUp);
		return "Registration Success";
	}

	@Override
	public void deleteUserByEmail(String email) {
		signuprepo.deleteByEmail(email);
	}
	
	@Override
	public SignUpEntity updateUser(String email, SignUpEntity updatedUser) {
	    SignUpEntity existingUser = signuprepo.findByEmail(email);
	    
	    if (existingUser == null) {
	        throw new RuntimeException("User with email " + email + " not found.");
	    }
	    
	    // Update the required fields
	    existingUser.setfirstName(updatedUser.getfirstName());
	    existingUser.setlastName(updatedUser.getlastName());
	    existingUser.setGender(updatedUser.getGender());
	    existingUser.setphoneNumber(updatedUser.getphoneNumber());
	    
	    return signuprepo.save(existingUser);

}
	
	@Override
	public void updateAddress(String email, AddressUpdateRequest request) {
	    SignUpEntity user = signuprepo.findByEmail(email);
	    if (user != null) {
	        user.setAddress(request.getLabel(), request.getDoorNo() , request.getStreet(), request.getCity(), request.getState(), request.getCountry());
	    	 //user.setAddress(request.getLabel(),request.getCity(), request.getState(), request.getCountry());
	        signuprepo.save(user);
	    } else {
	        throw new RuntimeException("User not found with email: " + email);
	    }
}
}
