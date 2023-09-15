package com.onlinelearning.service;

import com.onlinelearning.entity.SignUpEntity;

public interface SignUpService {
	
	String saveUser(SignUpEntity SignUp);
	void deleteUserByEmail(String email);

}
