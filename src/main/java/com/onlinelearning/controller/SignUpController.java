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

import com.onlinelearning.entity.ResponseMessage;
import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.Address;
import com.onlinelearning.repo.SignUpRepository;
import com.onlinelearning.service.SignUpService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/onlinelearning")
public class SignUpController {

	@Autowired
	private SignUpRepository signupRepo;

	@Autowired
	SignUpService signupservice;
	
	@Autowired
	HttpServletRequest request; // Autowire HttpServletRequest to access cookies
	
	@Autowired
	HttpServletResponse response;



//	@PostMapping("/signup")
//	public ResponseEntity<String> createuser(@RequestBody SignUpEntity signent) {
//		String result = signupservice.saveUser(signent);
//		if ("Registration Success".equals(result)) {
//			return new ResponseEntity<String>(result, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
//		}
//	}
	
	@PostMapping("/signup")
	public ResponseEntity<ResponseMessage> createuser(@RequestBody SignUpEntity signent) {
	    String result = signupservice.saveUser(signent);
	    if ("Registration Success".equals(result)) {
	        return new ResponseEntity<>(new ResponseMessage("Registration Success"), HttpStatus.CREATED);
	    } else {
	        return new ResponseEntity<>(new ResponseMessage(result), HttpStatus.BAD_REQUEST);
	    }
	}


//	@DeleteMapping("/delete-user/{email}")
//	public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
//		signupservice.deleteUserByEmail(email);
//		return ResponseEntity.ok("Your account has been deleted");
//	}
	
	@DeleteMapping("/delete-user")
	public ResponseEntity<String> deleteUserByEmail() {
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in the cookie.");
	    }
	    String email = signupservice.decodeJWTAndGetEmail(token);
	 // Deleting the user details using the email extracted from the token
	    try {
	        signupservice.deleteUserByEmail(email);
	        return ResponseEntity.ok("Your account has been deleted");
	    } catch(Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during deletion: " + e.getMessage());
	    }

	}

	
	public String extractTokenFromCookie(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("jwt-token".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}

//	@GetMapping("/user-details/{email}")
//	public ResponseEntity<SignUpEntity> getUserByEmail(@PathVariable String email) {
//		SignUpEntity user = signupRepo.findByEmail(email);
//		if (user != null) {
//			return ResponseEntity.ok(user);
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}
	
	@GetMapping("/user-details")
	public ResponseEntity<SignUpEntity> getUserByEmail() {
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }
	    String email = signupservice.decodeJWTAndGetEmail(token);
	    SignUpEntity user = signupRepo.findByEmail(email);
	    if (user != null) {
//	        return ResponseEntity.ok(user);
	    	SignUpEntity dto = new SignUpEntity();
	        dto.setfirstName(user.getfirstName());
	        dto.setlastName(user.getlastName());
	        dto.setGender(user.getGender());
	        dto.setphoneNumber(user.getphoneNumber());
	        dto.setEmail(user.getEmail());
	        return ResponseEntity.ok(dto);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
//	@PutMapping("/update-user/{email}")
//	public ResponseEntity<SignUpEntity> updateUserDetails(@PathVariable String email, @RequestBody SignUpEntity user) {
//	    SignUpEntity updatedUser = signupservice.updateUser(email, user);
//	    if (updatedUser != null) {
//	        return ResponseEntity.ok(updatedUser);
//	    } else {
//	        return ResponseEntity.notFound().build();
//	    }
//	}
	
	@PutMapping("/update-user")
	public ResponseEntity<SignUpEntity> updateUserDetails(@RequestBody SignUpEntity user) {
	    // Extracting the token from the cookie
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // Decoding the token to get the email
	    String email = signupservice.decodeJWTAndGetEmail(token);

	    // Updating the user details using the email extracted from the token
	    SignUpEntity updatedUser = signupservice.updateUser(email, user);
	    if (updatedUser != null) {
//	        return ResponseEntity.ok(updatedUser);
	    	SignUpEntity dto = new SignUpEntity();
	        dto.setfirstName(user.getfirstName());
	        dto.setlastName(user.getlastName());
	        dto.setGender(user.getGender());
	        dto.setphoneNumber(user.getphoneNumber());
	        dto.setEmail(user.getEmail());
	        return ResponseEntity.ok(dto);
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
	
//	@PutMapping("/update-address/{email}")
//    public ResponseEntity<List<Address>> addAddress(@PathVariable String email, @RequestBody Address address) {
//        try {
//        	 List<Address> addresses =signupservice.addAddress(email, address);
//        	 return ResponseEntity.ok(addresses);  // Return the list of addresses
//        } catch (Exception e) {
//        	return ResponseEntity.badRequest().body(null);
//        }
//    }
	
	@PutMapping("/update-address")
	public ResponseEntity<List<Address>> addAddress(@RequestBody Address address) {
	    // Extracting the token from the cookie
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // Decoding the token to get the email
	    String email = signupservice.decodeJWTAndGetEmail(token);

	    // Adding the address using the email extracted from the token
	    try {
	        List<Address> addresses = signupservice.addAddress(email, address);
	        return ResponseEntity.ok(addresses);  // Return the updated list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}

	
//	@PutMapping("/edit-address/{email}/{addressId}")
//	public ResponseEntity<List<Address>> editAddressByIndex(@PathVariable String email, @PathVariable String addressId, @RequestBody Address address) {
//	    try {
//	        List<Address> addresses = signupservice.editAddressById(email, addressId, address);
//	        return ResponseEntity.ok(addresses);  // Return the list of addresses
//	    } catch (Exception e) {
//	        return ResponseEntity.badRequest().body(null);
//	    }
//	}
	
	@PutMapping("/edit-address/{addressId}")
	public ResponseEntity<List<Address>> editAddressByIndex(@PathVariable String addressId, @RequestBody Address address) {
	    // 1. Extract the token from the cookie
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // 2. Decode the token to get the email
	    String email;
	    try {
	        email = signupservice.decodeJWTAndGetEmail(token);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // 3. Use that email to edit the address
	    try {
	        List<Address> addresses = signupservice.editAddressById(email, addressId, address);
	        return ResponseEntity.ok(addresses);  // Return the list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}

	
//	@DeleteMapping("/delete-address/{email}/{addressId}")
//	public ResponseEntity<List<Address>> deleteAddressById(@PathVariable String email, @PathVariable String addressId) {
//	    try {
//	        List<Address> addresses = signupservice.deleteAddressById(email, addressId);
//	        return ResponseEntity.ok(addresses);  // Return the updated list of addresses
//	    } catch (Exception e) {
//	        return ResponseEntity.badRequest().body(null);
//	    }
//	}
	
	@DeleteMapping("/delete-address/{addressId}")
	public ResponseEntity<List<Address>> deleteAddressById(@PathVariable String addressId) {
	    // 1. Extract the token from the cookie
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // 2. Decode the token to get the email
	    String email;
	    try {
	        email = signupservice.decodeJWTAndGetEmail(token);
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // 3. Use that email to delete the address
	    try {
	        List<Address> addresses = signupservice.deleteAddressById(email, addressId);
	        return ResponseEntity.ok(addresses);  // Return the updated list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}


	
//	@GetMapping("/get-addresses/{email}")
//	public ResponseEntity<List<Address>> getAllAddressesByEmail(@PathVariable String email) {
//	    try {
//	        List<Address> addresses = signupservice.getAllAddressesByEmail(email);
//	        return ResponseEntity.ok(addresses);  // Return the list of addresses
//	    } catch (Exception e) {
//	        return ResponseEntity.badRequest().body(null);
//	    }
//	}
	
	@GetMapping("/get-addresses")
	public ResponseEntity<List<Address>> getAllAddressesByEmail() {
	    // Extracting the token from the cookie
	    String token = extractTokenFromCookie(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    // Decoding the token to get the email
	    String email = signupservice.decodeJWTAndGetEmail(token);

	    // Fetching all addresses using the email extracted from the token
	    try {
	        List<Address> addresses = signupservice.getAllAddressesByEmail(email);
	        return ResponseEntity.ok(addresses);  // Return the list of addresses
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}

	
//	@PostMapping("/Login")
//	public ResponseEntity<String> loginUser(@RequestBody SignUpEntity userCredentials,) {
//	    boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
//	    if (isValid) {
//	        return ResponseEntity.ok("Login successful");
//	    } else {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
//	    }
//	}
	
//	@PostMapping("/Login")
//	public ResponseEntity<String> loginUser(@RequestBody SignUpEntity userCredentials, HttpServletResponse response) {
//	    boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
//	    if (isValid) {
//	        String token = signupservice.getTokenForUser(userCredentials.getEmail()); // get the token for the user
//	        Cookie jwtCookie = new Cookie("jwt-token", token);  // create a cookie with the token
//	        jwtCookie.setHttpOnly(true);
//	        jwtCookie.setMaxAge(2 * 24 * 60 * 60);  // setting the cookie for 7 days
//	        response.addCookie(jwtCookie);  // add the cookie to the response
//	        return ResponseEntity.ok("Login successful");
//	    } else {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
//	    }
//	}
	
	@PostMapping("/Login")
	public ResponseEntity<ResponseMessage> loginUser(@RequestBody SignUpEntity userCredentials, HttpServletResponse response) {
	    boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
	    if (isValid) {
	        String token = signupservice.getTokenForUser(userCredentials.getEmail());
	        Cookie jwtCookie = new Cookie("jwt-token", token);
	        jwtCookie.setHttpOnly(true);
	        jwtCookie.setMaxAge(2 * 24 * 60 * 60); 
	        response.addCookie(jwtCookie);  

	        return new ResponseEntity<>(new ResponseMessage("Login successful"), HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(new ResponseMessage("Invalid email or password"), HttpStatus.UNAUTHORIZED);
	    }
	}

	
	
//	@DeleteMapping("/logout")
//	public ResponseEntity<String> logoutUser(@RequestBody SignUpEntity userCredentials) {
//	    try {
//	        signupservice.logoutUser(userCredentials.getEmail());
//	        return ResponseEntity.ok("Logout successful");
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during logout: " + e.getMessage());
//	    }
//	}
	
//	@DeleteMapping("/logout")
//	public ResponseEntity<String> logoutUser() {
//	    try {
//	        // Getting the JWT token cookie
//	        Cookie[] cookies = request.getCookies();
//	        Cookie jwtCookie = null;
//	        if (cookies != null) {
//	            for (Cookie cookie : cookies) {
//	                if ("jwt-token".equals(cookie.getName())) {
//	                    jwtCookie = cookie;
//	                    break;
//	                }
//	            }
//	        }
//
//	        // If the JWT token cookie exists, we "delete" it by setting its max age to 0
//	        if (jwtCookie != null) {
//	            jwtCookie.setMaxAge(0);
//	            jwtCookie.setPath("/onlinelearning");  // Ensure you're setting the path, especially if you set it when creating the cookie
//	            ((HttpServletResponse) response).addCookie(jwtCookie);
//	        }
//
//	        // Return the logout success message
//	        return ResponseEntity.ok("Logout successful");
//	    } catch (Exception e) {
//	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during logout: " + e.getMessage());
//	    }
//	}
	
	@DeleteMapping("/logout")
	public ResponseEntity<ResponseMessage> logoutUser() {
	    try {
	        // Getting the JWT token cookie
	        Cookie[] cookies = request.getCookies();
	        Cookie jwtCookie = null;
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if ("jwt-token".equals(cookie.getName())) {
	                    jwtCookie = cookie;
	                    break;
	                }
	            }
	        }

	        // If the JWT token cookie exists, we "delete" it by setting its max age to 0
	        if (jwtCookie != null) {
	            jwtCookie.setMaxAge(0);
	            jwtCookie.setPath("/onlinelearning");  
	            ((HttpServletResponse) response).addCookie(jwtCookie);
	        }

	        return new ResponseEntity<>(new ResponseMessage("Logout successful"), HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(new ResponseMessage("Error during logout: " + e.getMessage()), HttpStatus.BAD_REQUEST);
	    }
	}



}
