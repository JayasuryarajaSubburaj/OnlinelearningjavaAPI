package com.onlinelearning.service;

import java.util.List;

import com.onlinelearning.entity.SignUpEntity;

public interface SignUpService {
	
	String saveUser(SignUpEntity SignUp);
	void deleteUserByEmail(String email);
//	SignUpEntity updateUser(SignUpEntity updatedUser);
	SignUpEntity updateUser(String email, SignUpEntity updatedUser);

}
