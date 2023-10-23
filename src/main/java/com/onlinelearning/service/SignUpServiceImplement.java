package com.onlinelearning.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.onlinelearning.entity.LoginLog;
import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.entity.SignUpEntity.Address;
import com.onlinelearning.repo.LoginLogRepository;
import com.onlinelearning.repo.SignUpRepository;

@Service
public class SignUpServiceImplement implements SignUpService {

	@Autowired
	SignUpRepository signuprepo;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	LoginLogRepository loginLogRepo;
	

	@Override
	public String saveUser(SignUpEntity SignUp) {

		// Check if email exists
		if (signuprepo.findByEmail(SignUp.getEmail()) != null) {
			return "Email already registered";
		}

		// Check if password is null before encoding
		if (SignUp.getPassword() == null) {
			return "Password cannot be null";
		}

		// Password hashing
		SignUp.setPassword(passwordEncoder.encode(SignUp.getPassword()));

		signuprepo.save(SignUp);
		return "Registration Success";
	}

	@Override
	public void deleteUserByEmail(String email) {
		signuprepo.deleteByEmail(email);
	}

	@Override
	public SignUpEntity updateUser(String email, SignUpEntity updatedUser) {
		SignUpEntity existingUser = signuprepo.findByEmail(email);

		if (existingUser == null) {
			throw new RuntimeException("User with email " + email + " not found.");
		}

		// Update the required fields
		existingUser.setfirstName(updatedUser.getfirstName());
		existingUser.setlastName(updatedUser.getlastName());
		existingUser.setGender(updatedUser.getGender());
		existingUser.setphoneNumber(updatedUser.getphoneNumber());

		return signuprepo.save(existingUser);

	}

	@Override
	public List<Address> addAddress(String email, Address newAddress) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null) {
			user.getAddresses().add(newAddress);
			signuprepo.save(user);
			return user.getAddresses(); // Return the list of addresses
		} else {
			throw new RuntimeException("User not found with email: " + email);
		}
	}

	@Override
	public List<Address> editAddressById(String email, String addressId, Address updatedAddress) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null) {
			List<Address> addresses = user.getAddresses();
			boolean found = false;

			for (Address addr : addresses) {
				if (addr.getId().equals(addressId)) {
					addr.setDoorNo(updatedAddress.getDoorNo());
					addr.setStreet(updatedAddress.getStreet());
					addr.setCity(updatedAddress.getCity());
					addr.setState(updatedAddress.getState());
					addr.setCountry(updatedAddress.getCountry());

					found = true;
					break;

				}
			}
			if (!found) {
				throw new RuntimeException("Address not found with id: " + addressId);
			}

			signuprepo.save(user);
			return user.getAddresses(); // Return the list of addresses

		} else {
			throw new RuntimeException("User not found with email: " + email);
		}
	}

	@Override
	public List<Address> deleteAddressById(String email, String addressId) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null) {
			List<Address> addresses = user.getAddresses();

			addresses.removeIf(addr -> addr.getId().equals(addressId));

			signuprepo.save(user);
			return user.getAddresses(); // Return the updated list of addresses
		} else {
			throw new RuntimeException("User not found with email: " + email);
		}
	}

	@Override
	public List<Address> getAllAddressesByEmail(String email) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null) {
			return user.getAddresses(); // Return the list of addresses for the user
		} else {
			throw new RuntimeException("User not found with email: " + email);
		}
	}

	private String secret = "Gyr0nebonlinele@rninG2023#";

	@Override
	public boolean validateLogin(String email, String password) {
		SignUpEntity user = signuprepo.findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPassword())) {

			// Generate JWT Token
//	            Algorithm algorithm = Algorithm.HMAC256(secret);
//	            String token = JWT.create()
//	                .withIssuer("online-learning-app")
//	                .withClaim("email", email)
//	                //.withClaim("password", password)
//	                .sign(algorithm);

			// Store the successful login attempt
			LoginLog log = new LoginLog();
			log.setEmail(email);
			log.setLoginTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//	            log.setJwtToken(token); // Set the JWT token
			//loginLogRepo.save(log);

			return true; // Login successful
		}
		return false; // Login failed
	}

	@Override
	public String getTokenForUser(String email) {
		// You can generate the token similarly to how you do it in validateLogin
		Algorithm algorithm = Algorithm.HMAC256(secret);
		String token = JWT.create().withIssuer("online-learning-app").withClaim("email", email).sign(algorithm);
		return token;
	}

	@Override
	public void logoutUser(String email) {
		LoginLog log = loginLogRepo.findByEmail(email);
		if (log != null) {
			loginLogRepo.delete(log);
		} else {
			throw new RuntimeException("User not found in login logs with email: " + email);
		}
	}

	public String decodeJWTAndGetEmail(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer("online-learning-app").build();
			DecodedJWT jwt = verifier.verify(token);
			return jwt.getClaim("email").asString();
		} catch (Exception exception) {
			throw new RuntimeException("Invalid token: " + exception.getMessage());
		}
	}

}
