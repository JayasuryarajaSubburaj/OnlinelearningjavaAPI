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
		
}