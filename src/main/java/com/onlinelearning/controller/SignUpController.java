package com.onlinelearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.Address;
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
	public ResponseEntity<String>	 createuser(@RequestBody SignUpEntity signent) {
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
	
//	@PutMapping("/update-address/{email}")
//	public ResponseEntity<String> updateAddress(@PathVariable String email, @RequestBody AddressUpdateRequest request) {
//	    try {
//	        signupservice.updateAddress(email, request);
//	        return ResponseEntity.ok("Address updated successfully");
//	    } catch (RuntimeException ex) {
//	        return ResponseEntity.badRequest().body(ex.getMessage());
//	    }
//	}
	
	@PutMapping("/update-address/{email}")
    public ResponseEntity<List<Address>> addAddress(@PathVariable String email, @RequestBody Address address) {
        try {
        	 List<Address> addresses =signupservice.addAddress(email, address);
        	 return ResponseEntity.ok(addresses);  // Return the list of addresses
        } catch (Exception e) {
        	return ResponseEntity.badRequest().body(null);
        }
    }
	
	@PutMapping("/edit-address/{email}/{index}")
	public ResponseEntity<List<Address>> editAddressByIndex(@PathVariable String email, @PathVariable int index, @RequestBody Address address) {
	    try {
	        List<Address> addresses = signupservice.editAddressByIndex(email, index, address);
	        return ResponseEntity.ok(addresses);  // Return the list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}
	
	@DeleteMapping("/delete-address/{email}/{index}")
	public ResponseEntity<List<Address>> deleteAddressByIndex(@PathVariable String email, @PathVariable int index) {
	    try {
	        List<Address> addresses = signupservice.deleteAddressByIndex(email, index);
	        return ResponseEntity.ok(addresses);  // Return the list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}
	
	@GetMapping("/get-addresses/{email}")
	public ResponseEntity<List<Address>> getAllAddressesByEmail(@PathVariable String email) {
	    try {
	        List<Address> addresses = signupservice.getAllAddressesByEmail(email);
	        return ResponseEntity.ok(addresses);  // Return the list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}
	
	@PostMapping("/Login")
	public ResponseEntity<String> loginUser(@RequestBody SignUpEntity userCredentials) {
	    boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
	    if (isValid) {
	        return ResponseEntity.ok("Login successful");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
	    }
	}
	
	@DeleteMapping("/logout")
	public ResponseEntity<String> logoutUser(@RequestBody SignUpEntity userCredentials) {
	    try {
	        signupservice.logoutUser(userCredentials.getEmail());
	        return ResponseEntity.ok("Logout successful");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during logout: " + e.getMessage());
	    }
	}

}
