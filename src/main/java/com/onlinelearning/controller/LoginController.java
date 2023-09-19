package com.onlinelearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.service.LoginService;

@RestController
@RequestMapping("/onlinelearning")
public class LoginController {

	@Autowired
	LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody SignUpEntity user) {
		if (loginService.validateUser(user.getEmail(), user.getPassword())) {
			return ResponseEntity.ok("Login Successfully");
		} else {
			return ResponseEntity.status(401).body("Invalid Credentials");
		}
	}
	
}
