package com.onlinelearning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.repo.SignUpRepository;


@Service
public class SignUpServiceImplement implements SignUpService {

	@Autowired
	SignUpRepository signuprepo;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Override
	public String saveUser(SignUpEntity SignUp) {
		
		//Check if email exists
		if (signuprepo.findByEmail(SignUp.getEmail()) != null) {
            return "Email already registered";
        }
		
		//Check if password is null before encoding
        if (SignUp.getPassword() == null) {
            return "Password cannot be null";
        }
		
		//Password hashing
		SignUp.setPassword(passwordEncoder.encode(SignUp.getPassword()));
		
		signuprepo.save(SignUp);
		return "Registration Success";
	}
	
	@Override
	public void deleteUserByEmail(String email) {
	    signuprepo.deleteByEmail(email);
	}
	
	@Override
    public SignUpEntity updateUser(String email,SignUpEntity updatedUser) {
        // Fetch the existing user based on the email.
        SignUpEntity existingUser = signuprepo.findByEmail(updatedUser.getEmail());
        
        // If the user doesn't exist, return null.
        if (existingUser == null) {
        	// Update user details.
        	throw new RuntimeException("User with email " + email + " not found.");
        	//return null;
           
        }
        existingUser.setfirstName(updatedUser.getfirstName());
        existingUser.setlastName(updatedUser.getlastName());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setphoneNumber(updatedUser.getphoneNumber());
        existingUser.setAddress(updatedUser.getLabel(),updatedUser.getDoorNumber(),updatedUser.getStreetName(),updatedUser.getCity(),updatedUser.getState(),updatedUser.getCountry());
        
        // Save and return the updated user.
        return signuprepo.save(existingUser);
        
        
    }

//	@Override
//	public SignUpEntity updateUser(SignUpEntity updatedUser) {
//		// TODO Auto-generated method stub
//		return null;
//	}
		
}
