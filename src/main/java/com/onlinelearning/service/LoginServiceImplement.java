package com.onlinelearning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.repo.SignUpRepository;

@Service
public class LoginServiceImplement implements LoginService {

	@Autowired
	SignUpRepository signuprepo;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Override
	public boolean validateUser(String email, String password) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return true;
		}
		return false;
	}

}
