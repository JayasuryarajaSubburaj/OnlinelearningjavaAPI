package com.onlinelearning.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.onlinelearning.entity.DeleteAddressTask;
import com.onlinelearning.entity.EditAddressTask;
import com.onlinelearning.entity.ResponseMessage;
import com.onlinelearning.entity.ResponseMessageWithAddresses;
import com.onlinelearning.entity.ResponseMessageWithToken;
import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.Address;
import com.onlinelearning.repo.SignUpRepository;
import com.onlinelearning.service.DeleteUserTask;
import com.onlinelearning.service.FetchUserDetailsTask;
import com.onlinelearning.service.GetAddressesTask;
import com.onlinelearning.service.LoginTask;
import com.onlinelearning.service.SignUpService;
import com.onlinelearning.service.SignUpServiceImplement;
import com.onlinelearning.service.SignUpTask;
import com.onlinelearning.service.UpdateAddressTask;
import com.onlinelearning.service.UpdateUserDetailsTask;

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
	SignUpServiceImplement signUpService1;

	@Autowired
	SignUpServiceImplement loginService1;

	@Autowired
	HttpServletRequest request; // Autowire HttpServletRequest to access cookies

	@Autowired
	HttpServletResponse response;

//	@Autowired
//	private ExecutorService executorService; // Assuming you've created a bean for this, shown later

	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Autowired
	private Semaphore semaphore;
	
	@Autowired
	@Qualifier("loginThreadPoolExecutor")
	private ThreadPoolExecutor loginThreadPoolExecutor;

	@Autowired
	@Qualifier("otherThreadPoolExecutor")
	private ThreadPoolExecutor otherThreadPoolExecutor;

//	@PostMapping("/signup")
//	public ResponseEntity<ResponseMessage> createuser(@RequestBody SignUpEntity signent) {
//		String result = signupservice.saveUser(signent);
//		if ("Registration Success".equals(result)) {
//			return new ResponseEntity<>(new ResponseMessage("Registration Success"), HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(new ResponseMessage(result), HttpStatus.BAD_REQUEST);
//		}
//	}
	
	@GetMapping("/demo")
	public String getMessage() {
		return "welcome jsr";
	}

	@PostMapping("/signup")
	public ResponseEntity<ResponseMessage> createUser(@RequestBody SignUpEntity signUpEntity) {
		synchronized (this) {
			if (!semaphore.tryAcquire()) {
				System.out.println("Rejected a user because all permits are in use.");
				return new ResponseEntity<>(new ResponseMessage("Server is busy. Please try again later."),
						HttpStatus.SERVICE_UNAVAILABLE);
			}
		}

		try {
			Callable<String> task = new SignUpTask(signUpEntity, signUpService1);
//			String responseMessage = threadPoolExecutor.submit(task).get();
			String responseMessage = otherThreadPoolExecutor.submit(task).get();
			return new ResponseEntity<>(new ResponseMessage(responseMessage), HttpStatus.CREATED);
		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(
					new ResponseMessage(
							"Server is currently processing maximum number of requests. Please try again later."),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return new ResponseEntity<>(new ResponseMessage("Error processing signup: " + ee.getCause().getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return new ResponseEntity<>(new ResponseMessage("Signup processing was interrupted. Please try again."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			semaphore.release();
		}
	}

//	@DeleteMapping("/delete-user")
//	public ResponseEntity<String> deleteUserByEmail() {
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in the cookie.");
//		}
//		String email = signupservice.decodeJWTAndGetEmail(token);
//		// Deleting the user details using the email extracted from the token
//		try {
//			signupservice.deleteUserByEmail(email);
//			return ResponseEntity.ok("Your account has been deleted");
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during deletion: " + e.getMessage());
//		}
//
//	}

	@DeleteMapping("/delete-user")
	public ResponseEntity<ResponseMessage> deleteUserByEmail() {
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessage("No JWT token found in the cookie."));
		}
		String email = signupservice.decodeJWTAndGetEmail(token);

		try {
			Callable<Boolean> task = new DeleteUserTask(email, signUpService1);
//			boolean result = threadPoolExecutor.submit(task).get();
			boolean result = otherThreadPoolExecutor.submit(task).get();

			if (result) {
				return ResponseEntity.ok(new ResponseMessage("Your account has been deleted"));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Error during deletion"));
			}

		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(
					new ResponseMessage(
							"Server is currently processing maximum number of requests. Please try again later."),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Error processing deletion"));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Deletion processing was interrupted. Please try again."));
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

//	@GetMapping("/user-details")
//	public ResponseEntity<SignUpEntity> getUserByEmail() {
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//		String email = signupservice.decodeJWTAndGetEmail(token);
//		SignUpEntity user = signupRepo.findByEmail(email);
//		if (user != null) {
//			SignUpEntity dto = new SignUpEntity();
//			dto.setfirstName(user.getfirstName());
//			dto.setlastName(user.getlastName());
//			dto.setGender(user.getGender());
//			dto.setphoneNumber(user.getphoneNumber());
//			dto.setEmail(user.getEmail());
//			return ResponseEntity.ok(dto);
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}

	@GetMapping("/user-details")
	public ResponseEntity<ResponseMessage> getUserByEmail() {
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessage("No JWT token found in the cookie.", null));
		}
		String email = signupservice.decodeJWTAndGetEmail(token);

		try {
			Callable<SignUpEntity> task = new FetchUserDetailsTask(email, signupRepo);
//			SignUpEntity user = threadPoolExecutor.submit(task).get();
			SignUpEntity user = otherThreadPoolExecutor.submit(task).get();

			if (user != null) {
				ResponseMessage response = new ResponseMessage("User details fetched successfully", user);
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}

		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(
					new ResponseMessage(
							"Server is currently processing maximum number of requests. Please try again later.", null),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Error processing request", null));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Request processing was interrupted. Please try again.", null));
		}
	}

//	@PutMapping("/update-user")
//	public ResponseEntity<SignUpEntity> updateUserDetails(@RequestBody SignUpEntity user) {
//		// Extracting the token from the cookie
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// Decoding the token to get the email
//		String email = signupservice.decodeJWTAndGetEmail(token);
//
//		// Updating the user details using the email extracted from the token
//		SignUpEntity updatedUser = signupservice.updateUser(email, user);
//		if (updatedUser != null) {
//			SignUpEntity dto = new SignUpEntity();
//			dto.setfirstName(user.getfirstName());
//			dto.setlastName(user.getlastName());
//			dto.setGender(user.getGender());
//			dto.setphoneNumber(user.getphoneNumber());
//			dto.setEmail(user.getEmail());
//			return ResponseEntity.ok(dto);
//		} else {
//			return ResponseEntity.notFound().build();
//		}
//	}

	@PutMapping("/update-user")
	public ResponseEntity<ResponseMessage> updateUserDetails(@RequestBody SignUpEntity user) {
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessage("No JWT token found in the cookie.", null));
		}

		String email = signupservice.decodeJWTAndGetEmail(token);

		try {
			Callable<SignUpEntity> task = new UpdateUserDetailsTask(email, user, signUpService1);
//			SignUpEntity updatedUser = threadPoolExecutor.submit(task).get();
			SignUpEntity updatedUser = otherThreadPoolExecutor.submit(task).get();

			if (updatedUser != null) {
				return ResponseEntity.ok(new ResponseMessage("User details updated successfully", updatedUser));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("User not found", null));
			}

		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage(
					"Server is currently processing maximum number of requests. Please try again later.", null));
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Error processing update", null));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessage("Update processing was interrupted. Please try again.", null));
		}
	}

//	@PutMapping("/update-address")
//	public ResponseEntity<List<Address>> addAddress(@RequestBody Address address) {
//		// Extracting the token from the cookie
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// Decoding the token to get the email
//		String email = signupservice.decodeJWTAndGetEmail(token);
//
//		// Adding the address using the email extracted from the token
//		try {
//			List<Address> addresses = signupservice.addAddress(email, address);
//			return ResponseEntity.ok(addresses); // Return the updated list of addresses
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(null);
//		}
//	}

	@PutMapping("/update-address")
	public ResponseEntity<ResponseMessageWithAddresses> addAddress(@RequestBody Address address) {
		// Extracting the token from the cookie
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessageWithAddresses("Unauthorized", null));
		}

		// Decoding the token to get the email
		String email = signupservice.decodeJWTAndGetEmail(token);

		// Adding the address using the email extracted from the token
		try {
			Callable<List<Address>> task = new UpdateAddressTask(email, address, signupservice);
//			List<Address> addresses = threadPoolExecutor.submit(task).get();
			List<Address> addresses = otherThreadPoolExecutor.submit(task).get();
			return ResponseEntity.ok(new ResponseMessageWithAddresses("User address updated successfully", addresses));
		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(
					new ResponseMessageWithAddresses(
							"Server is currently processing maximum number of requests. Please try again later.", null),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessageWithAddresses("Error processing request", null));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ResponseMessageWithAddresses("Request processing was interrupted. Please try again.", null));
		}
	}

//	@PutMapping("/edit-address/{addressId}")
//	public ResponseEntity<List<Address>> editAddressByIndex(@PathVariable String addressId,
//			@RequestBody Address address) {
//		// 1. Extract the token from the cookie
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// 2. Decode the token to get the email
//		String email;
//		try {
//			email = signupservice.decodeJWTAndGetEmail(token);
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// 3. Use that email to edit the address
//		try {
//			List<Address> addresses = signupservice.editAddressById(email, addressId, address);
//			return ResponseEntity.ok(addresses); // Return the list of addresses
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(null);
//		}
//	}

	@PutMapping("/edit-address/{addressId}")
	public ResponseEntity<ResponseMessageWithAddresses> editAddressByIndex(@PathVariable String addressId,
			@RequestBody Address address) {
		// Extracting the token from the cookie
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessageWithAddresses("Unauthorized", null));
		}

		// Decoding the token to get the email
		String email;
		try {
			email = signupservice.decodeJWTAndGetEmail(token);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessageWithAddresses("Invalid token", null));
		}

		// Edit the address using multithreading
		try {
			Callable<List<Address>> task = new EditAddressTask(email, addressId, address, signUpService1);
//			List<Address> addresses = threadPoolExecutor.submit(task).get();
			List<Address> addresses = otherThreadPoolExecutor.submit(task).get();
			return ResponseEntity.ok(new ResponseMessageWithAddresses("Address updated successfully", addresses));
		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(
					new ResponseMessageWithAddresses(
							"Server is currently processing maximum number of requests. Please try again later.", null),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessageWithAddresses("Error processing request", null));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ResponseMessageWithAddresses("Request processing was interrupted. Please try again.", null));
		}
	}

//	@DeleteMapping("/delete-address/{addressId}")
//	public ResponseEntity<List<Address>> deleteAddressById(@PathVariable String addressId) {
//		// 1. Extract the token from the cookie
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// 2. Decode the token to get the email
//		String email;
//		try {
//			email = signupservice.decodeJWTAndGetEmail(token);
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// 3. Use that email to delete the address
//		try {
//			List<Address> addresses = signupservice.deleteAddressById(email, addressId);
//			return ResponseEntity.ok(addresses); // Return the updated list of addresses
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(null);
//		}
//	}

	@DeleteMapping("/delete-address/{addressId}")
	public ResponseEntity<ResponseMessageWithAddresses> deleteAddressById(@PathVariable String addressId) {
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
			Callable<List<Address>> task = new DeleteAddressTask(email, addressId, signUpService1);
//			List<Address> addresses = threadPoolExecutor.submit(task).get();
			List<Address> addresses = otherThreadPoolExecutor.submit(task).get();
			return ResponseEntity.ok(new ResponseMessageWithAddresses("Address Deleted successfully", addresses));
			// return ResponseEntity.ok(addresses); // Return the updated list of addresses
		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			// return new ResponseEntity<>(new ArrayList<>(),
			// HttpStatus.SERVICE_UNAVAILABLE);
			return new ResponseEntity<>(
					new ResponseMessageWithAddresses(
							"Server is currently processing maximum number of requests. Please try again later.", null),
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

//	@GetMapping("/get-addresses")
//	public ResponseEntity<List<Address>> getAllAddressesByEmail() {
//		// Extracting the token from the cookie
//		String token = extractTokenFromCookie(request);
//		if (token == null) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//
//		// Decoding the token to get the email
//		String email = signupservice.decodeJWTAndGetEmail(token);
//
//		// Fetching all addresses using the email extracted from the token
//		try {
//			List<Address> addresses = signupservice.getAllAddressesByEmail(email);
//			return ResponseEntity.ok(addresses); // Return the list of addresses
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(null);
//		}
//	}

	@GetMapping("/get-addresses")
	public ResponseEntity<ResponseMessageWithAddresses> getAllAddressesByEmail() {
		String token = extractTokenFromCookie(request);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseMessageWithAddresses("Unauthorized", null));
		}

		String email = signupservice.decodeJWTAndGetEmail(token);

		try {
			Callable<List<Address>> task = new GetAddressesTask(email, signUpService1);
//			List<Address> addresses = threadPoolExecutor.submit(task).get();
			List<Address> addresses = otherThreadPoolExecutor.submit(task).get();
			return ResponseEntity
					.ok(new ResponseMessageWithAddresses("User addresses fetched successfully", addresses));
		} catch (RejectedExecutionException ree) {
			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
			return new ResponseEntity<>(new ResponseMessageWithAddresses(
					"Server is currently processing maximum number of requests. Please try again later.",
					new ArrayList<>()), HttpStatus.SERVICE_UNAVAILABLE);
		} catch (ExecutionException ee) {
			System.out.println("Execution exception: " + ee.getCause().getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseMessageWithAddresses("Error processing request", null));
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt(); // Preserve the interrupt status
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ResponseMessageWithAddresses("Request processing was interrupted. Please try again.", null));
		}
	}

//	@PostMapping("/login")
//	public ResponseEntity<ResponseMessage> loginUser(@RequestBody SignUpEntity userCredentials,
//			HttpServletResponse response) {
//		boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
//		if (isValid) {
//			String token = signupservice.getTokenForUser(userCredentials.getEmail());
//			Cookie jwtCookie = new Cookie("jwt-token", token);
//			jwtCookie.setHttpOnly(true);
//			jwtCookie.setMaxAge(1 * 24 * 60 * 60);
//			response.addCookie(jwtCookie);
//
//			return new ResponseEntity<>(new ResponseMessage("Login successful"), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>(new ResponseMessage("Invalid email or password"), HttpStatus.UNAUTHORIZED);
//		}
//	}

//	@PostMapping("/login")
//	public ResponseEntity<ResponseMessage> loginUser(@RequestBody SignUpEntity userCredentials,
//			HttpServletResponse response) {
//		synchronized (this) {
//			if (!semaphore.tryAcquire()) {
//				System.out.println("Rejected a user login because all permits are in use.");
//				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//						.body(new ResponseMessage("Server is busy. Please try again later."));
//			}
//		}
//
//		try {
//			Callable<Boolean> task = new LoginTask(userCredentials, signUpService1);
//			boolean isValid = threadPoolExecutor.submit(task).get();
//
//			if (isValid) {
//				String token = signUpService1.getTokenForUser(userCredentials.getEmail());
//				Cookie jwtCookie = new Cookie("jwt-token", token);
//				jwtCookie.setHttpOnly(true);
//				jwtCookie.setMaxAge(1 * 24 * 60 * 60);
//				response.addCookie(jwtCookie);
//				return ResponseEntity.ok(new ResponseMessage("Login successful"));
//			} else {
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//						.body(new ResponseMessage("Invalid email or password"));
//			}
//		} catch (RejectedExecutionException ree) {
//			System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
//			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage(
//					"Server is currently processing maximum number of requests. Please try again later."));
//		} catch (ExecutionException ee) {
//			System.out.println("Execution exception: " + ee.getCause().getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(new ResponseMessage("Error processing login: " + ee.getCause().getMessage()));
//		} catch (InterruptedException ie) {
//			Thread.currentThread().interrupt(); // Preserve the interrupt status
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(new ResponseMessage("Login processing was interrupted. Please try again."));
//		} finally {
//			semaphore.release();
//		}
//	}
	
//Default login method
//	@PostMapping("/login")
//	public ResponseEntity<ResponseMessage> loginUser(@RequestBody SignUpEntity userCredentials,
//	                                                 HttpServletResponse response) {
//	    synchronized (this) {
//	        if (!semaphore.tryAcquire()) {
//	            System.out.println("Rejected a user login because all permits are in use.");
//	            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//	                                 .body(new ResponseMessage("Server is busy. Please try again later."));
//	        }
//	    }
//
//	    try {
//	        Callable<Boolean> task = new LoginTask(userCredentials, signUpService1);
//	        boolean isValid = loginThreadPoolExecutor.submit(task).get();  // use the loginThreadPoolExecutor here
//
//	        if (isValid) {
//	            String token = signUpService1.getTokenForUser(userCredentials.getEmail());
//	            Cookie jwtCookie = new Cookie("jwt-token", token);
//	            jwtCookie.setHttpOnly(true);
//	            jwtCookie.setMaxAge(1 * 24 * 60 * 60);
//	            response.addCookie(jwtCookie);
//	            return ResponseEntity.ok(new ResponseMessage("Login successful"));
//	        } else {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//	                                 .body(new ResponseMessage("Invalid email or password"));
//	        }
//	    } catch (RejectedExecutionException ree) {
//	        System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
//	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage(
//	            "Server is currently processing maximum number of requests. Please try again later."));
//	    } catch (ExecutionException ee) {
//	        System.out.println("Execution exception: " + ee.getCause().getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                             .body(new ResponseMessage("Error processing login: " + ee.getCause().getMessage()));
//	    } catch (InterruptedException ie) {
//	        Thread.currentThread().interrupt(); // Preserve the interrupt status
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                             .body(new ResponseMessage("Login processing was interrupted. Please try again."));
//	    } finally {
//	        semaphore.release();
//	    }
//	}
	
	//Arun kumar userEmail,userPassword
	@PostMapping("/login")
	public ResponseEntity<ResponseMessage> loginUser(@RequestBody SignUpEntity userCredentials,
	                                                 HttpServletResponse response) {
	    synchronized (this) {
	        if (!semaphore.tryAcquire()) {
	            System.out.println("Rejected a user login because all permits are in use.");
	            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	                                 .body(new ResponseMessage("Server is busy. Please try again later."));
	        }
	    }

	    try {
	        Callable<Boolean> task = new LoginTask(userCredentials, signUpService1);
	        boolean isValid = loginThreadPoolExecutor.submit(task).get();  // use the loginThreadPoolExecutor here

	        if (isValid) {
	            String token = signUpService1.getTokenForUser(userCredentials.getUserEmail());
	            Cookie jwtCookie = new Cookie("jwt-token", token);
	            jwtCookie.setHttpOnly(true);
	            jwtCookie.setMaxAge(1 * 24 * 60 * 60);
	            response.addCookie(jwtCookie);
	            return ResponseEntity.ok(new ResponseMessage("Login successful"));
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                                 .body(new ResponseMessage("Invalid email or password"));
	        }
	    } catch (RejectedExecutionException ree) {
	        System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessage(
	            "Server is currently processing maximum number of requests. Please try again later."));
	    } catch (ExecutionException ee) {
	        System.out.println("Execution exception: " + ee.getCause().getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Error processing login: " + ee.getCause().getMessage()));
	    } catch (InterruptedException ie) {
	        Thread.currentThread().interrupt(); // Preserve the interrupt status
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessage("Login processing was interrupted. Please try again."));
	    } finally {
	        semaphore.release();
	    }
	}

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
			return new ResponseEntity<>(new ResponseMessage("Error during logout: " + e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}

	// Local JWT token API'S

	// login API with JWT token local variable
	
	//Original
//	@PostMapping("/Login")
//	public ResponseEntity<ResponseMessageWithToken> loginUser(@RequestBody SignUpEntity userCredentials) {
//		boolean isValid = signupservice.validateLogin(userCredentials.getEmail(), userCredentials.getPassword());
//		if (isValid) {
//			String token = signupservice.getTokenForUser(userCredentials.getEmail());
//			return new ResponseEntity<>(new ResponseMessageWithToken("Login successfully", token), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>(new ResponseMessageWithToken("Invalid email or password", null),
//					HttpStatus.UNAUTHORIZED);
//		}
//	}
	
	//Arun useremail,userpassword
//	@PostMapping("/Login")
//	public ResponseEntity<ResponseMessageWithToken> loginUser(@RequestBody SignUpEntity userCredentials) {
//		boolean isValid = signupservice.validateLogin(userCredentials.getUserEmail(), userCredentials.getUserPassword());
//		if (isValid) {
//			String token = signupservice.getTokenForUser(userCredentials.getEmail());
//			return new ResponseEntity<>(new ResponseMessageWithToken("Login successfully", token), HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>(new ResponseMessageWithToken("Invalid email or password", null),
//					HttpStatus.UNAUTHORIZED);
//		}
//	}
	
	@PostMapping("/Login")
	public ResponseEntity<ResponseMessageWithToken> loginUser(@RequestBody SignUpEntity userCredentials) {
	    // Acquiring the semaphore
	    synchronized (this) {
	        if (!semaphore.tryAcquire()) {
	            System.out.println("Rejected a user login because all permits are in use.");
	            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
	                                 .body(new ResponseMessageWithToken("Server is busy. Please try again later.", null));
	        }
	    }

	    try {
	        // Submitting the login task to the thread pool
	        Callable<Boolean> task = () -> signupservice.validateLogin(userCredentials.getUserEmail(), userCredentials.getUserPassword());
	        boolean isValid = loginThreadPoolExecutor.submit(task).get();

	        if (isValid) {
	            String token = signupservice.getTokenForUser(userCredentials.getUserEmail());
	            return new ResponseEntity<>(new ResponseMessageWithToken("Login successfully", token), HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(new ResponseMessageWithToken("Invalid email or password", null),
	                                        HttpStatus.UNAUTHORIZED);
	        }
	    } catch (RejectedExecutionException ree) {
	        System.out.println("Rejected by ThreadPoolExecutor: " + ree.getMessage());
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseMessageWithToken(
	            "Server is currently processing maximum number of requests. Please try again later.", null));
	    } catch (ExecutionException ee) {
	        System.out.println("Execution exception: " + ee.getCause().getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessageWithToken("Error processing login: " + ee.getCause().getMessage(), null));
	    } catch (InterruptedException ie) {
	        Thread.currentThread().interrupt(); // Preserve the interrupt status
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(new ResponseMessageWithToken("Login processing was interrupted. Please try again.", null));
	    } finally {
	        semaphore.release();
	    }
	}

	// Get-addresses API with JWT token local variable
	@GetMapping("/Get-addresses/{token}")
	public ResponseEntity<ResponseMessageWithToken> getAllAddressesByToken(@PathVariable String token) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Fetch all addresses using the email decoded from the token
			List<Address> addresses = signupservice.getAllAddressesByEmail(email);

			ResponseMessageWithToken response = new ResponseMessageWithToken(addresses,
					"User address fetched successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Delete-address API with JWT token local variable
	@DeleteMapping("/Delete-address/{token}/{addressId}")
	public ResponseEntity<ResponseMessageWithToken> deleteAddressByToken(@PathVariable String token,
			@PathVariable String addressId) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Use the email to delete the address by addressId
			List<Address> remainingAddresses = signupservice.deleteAddressById(email, addressId);

			ResponseMessageWithToken response = new ResponseMessageWithToken(remainingAddresses,
					"User address deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Edit-address API with JWT token local variable
	@PutMapping("/Edit-address/{token}/{addressId}")
	public ResponseEntity<ResponseMessageWithToken> editAddressByToken(@PathVariable String token,
			@PathVariable String addressId, @RequestBody Address address) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Use the email to edit the address
			List<Address> addresses = signupservice.editAddressById(email, addressId, address);

			ResponseMessageWithToken response = new ResponseMessageWithToken(addresses,
					"User address edited successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Update-address API with JWT token local variable
	@PutMapping("/Update-address/{token}")
	public ResponseEntity<ResponseMessageWithToken> updateAddressByToken(@PathVariable String token,
			@RequestBody Address address) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Add the address using the email extracted from the token
			List<Address> updatedAddresses = signupservice.addAddress(email, address);

			ResponseMessageWithToken response = new ResponseMessageWithToken(updatedAddresses,
					"User address updated successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	// Update-user API with JWT token local variable
	@PutMapping("/Update-user/{token}")
	public ResponseEntity<ResponseMessageWithToken> updateUserDetailsByToken(@PathVariable String token,
			@RequestBody SignUpEntity updatedUser) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Update the user details using the email extracted from the token
			SignUpEntity existingUser = signupservice.updateUser(email, updatedUser);

			if (existingUser != null) {
				SignUpEntity dto = new SignUpEntity();
				dto.setfirstName(existingUser.getfirstName());
				dto.setlastName(existingUser.getlastName());
				dto.setGender(existingUser.getGender());
				dto.setphoneNumber(existingUser.getphoneNumber());
				dto.setEmail(existingUser.getEmail());
				ResponseMessageWithToken response = new ResponseMessageWithToken(dto, "User updated successfully");
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// user-details API with JWT token local variable
	@GetMapping("/User-details/{token}")
	public ResponseEntity<ResponseMessageWithToken> getUserDetailsByToken(@PathVariable String token) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Fetch user details from the database using the extracted email
			SignUpEntity user = signupRepo.findByEmail(email);

			if (user != null) {
				SignUpEntity dto = new SignUpEntity();
				dto.setfirstName(user.getfirstName());
				dto.setlastName(user.getlastName());
				dto.setGender(user.getGender());
				dto.setphoneNumber(user.getphoneNumber());
				dto.setEmail(user.getEmail());
				ResponseMessageWithToken response = new ResponseMessageWithToken(dto,
						"User details fetched successfully");
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// Delete API with JWT token local variable
	@DeleteMapping("/Delete-user/{token}")
	public ResponseEntity<ResponseMessageWithToken> deleteUserByToken(@PathVariable String token) {
		try {
			// 1. Decode the token to get the email
			String email = signupservice.decodeJWTAndGetEmail(token);

			// 2. Delete the user from the database using the extracted email
			signupservice.deleteUserByEmail(email);

			ResponseMessageWithToken response = new ResponseMessageWithToken("User account deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessageWithToken("Error during deletion: " + e.getMessage()));
		}
	}

}
