package com.onlinelearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.service.SignUpService;

@RestController
@RequestMapping("/onlinelearning")
public class SignUpController {

	
	@Autowired
	SignUpService signupservice;
	
	@PostMapping("/signup")
	public ResponseEntity<String> createuser(@RequestBody SignUpEntity signent) {
//		return new ResponseEntity<String>(signupservice.saveUser(signent),HttpStatus.CREATED);
		
		// Combine state with address
        String fullAddress = signent.getAddress() + ", " + signent.getState();
        signent.setAddress(fullAddress);

		String result = signupservice.saveUser(signent);

		if ("Registration Success".equals(result)) {
			return new ResponseEntity<String>(result, HttpStatus.CREATED);
		} 
		else {
			return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}

	}
	
	@DeleteMapping("/delete-user/{email}")
	public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
	    signupservice.deleteUserByEmail(email);
	    return ResponseEntity.ok("Your account has been deleted");
	}
	
}
