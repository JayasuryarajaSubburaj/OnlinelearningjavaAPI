package com.onlinelearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.AddressUpdateRequest;
import com.onlinelearning.repo.SignUpRepository;
import com.onlinelearning.service.SignUpService;

@RestController
@RequestMapping("/onlinelearning")
public class SignUpController {

	@Autowired
	private SignUpRepository signupRepo;

	@Autowired
	SignUpService signupservice;

	@PostMapping("/signup")
	public ResponseEntity<String> createuser(@RequestBody SignUpEntity signent) {
		String result = signupservice.saveUser(signent);
		if ("Registration Success".equals(result)) {
			return new ResponseEntity<String>(result, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/delete-user/{email}")
	public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
		signupservice.deleteUserByEmail(email);
		return ResponseEntity.ok("Your account has been deleted");
	}

	@GetMapping("/user-details/{email}")
	public ResponseEntity<SignUpEntity> getUserByEmail(@PathVariable String email) {
		SignUpEntity user = signupRepo.findByEmail(email);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/update-user/{email}")
	public ResponseEntity<SignUpEntity> updateUserDetails(@PathVariable String email, @RequestBody SignUpEntity user) {
	    SignUpEntity updatedUser = signupservice.updateUser(email, user);
	    if (updatedUser != null) {
	        return ResponseEntity.ok(updatedUser);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@PutMapping("/update-address/{email}")
	public ResponseEntity<String> updateAddress(@PathVariable String email, @RequestBody AddressUpdateRequest request) {
	    try {
	        signupservice.updateAddress(email, request);
	        return ResponseEntity.ok("Address updated successfully");
	    } catch (RuntimeException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage());
	    }
	}

}
