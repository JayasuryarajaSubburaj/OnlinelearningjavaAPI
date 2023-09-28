package com.onlinelearning.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
	
//	@Override
//	public void updateAddress(String email, AddressUpdateRequest request) {
//	    SignUpEntity user = signuprepo.findByEmail(email);
//	    if (user != null) {
////	        user.setAddress(request.getLabel(), request.getDoorNo() , request.getStreet(), request.getCity(), request.getState(), request.getCountry());
//	    	user.setAddress(request.getDoorNo() , request.getStreet(), request.getCity(), request.getState(), request.getCountry());
//	        signuprepo.save(user);
//	    } else {
//	        throw new RuntimeException("User not found with email: " + email);
//	    }
//}
	
	@Override
    public List<Address> addAddress(String email, Address newAddress) {
        SignUpEntity user = signuprepo.findByEmail(email);
        if (user != null) {
            user.getAddresses().add(newAddress);
            signuprepo.save(user);
            return user.getAddresses();  // Return the list of addresses
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }
	
	@Override
	public List<Address> editAddressByIndex(String email, int index, Address updatedAddress) {
	    SignUpEntity user = signuprepo.findByEmail(email);
	    if (user != null && user.getAddresses() != null && index >= 0 && index < user.getAddresses().size()) {
	        user.getAddresses().set(index, updatedAddress);  // Set the address at the specified index
	        signuprepo.save(user);
	        return user.getAddresses();  // Return the list of addresses
	    } else {
	        throw new RuntimeException("User not found with email: " + email + " or invalid address index.");
	    }
	}
	
	@Override
	public List<Address> deleteAddressByIndex(String email, int index) {
	    SignUpEntity user = signuprepo.findByEmail(email);
	    if (user != null && user.getAddresses() != null && index >= 0 && index < user.getAddresses().size()) {
	        user.getAddresses().remove(index);  // Remove the address at the specified index
	        signuprepo.save(user);
	        return user.getAddresses();  // Return the list of addresses
	    } else {
	        throw new RuntimeException("User not found with email: " + email + " or invalid address index.");
	    }
	}
	
	@Override
	public List<Address> getAllAddressesByEmail(String email) {
	    SignUpEntity user = signuprepo.findByEmail(email);
	    if (user != null) {
	        return user.getAddresses();  // Return the list of addresses for the user
	    } else {
	        throw new RuntimeException("User not found with email: " + email);
	    }
	}
	
	@Override
	public boolean validateLogin(String email, String password) {
	    SignUpEntity user = signuprepo.findByEmail(email);
	    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
	        // Store the successful login attempt
	        LoginLog log = new LoginLog();
	        log.setEmail(email);
	        log.setLoginTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	        //log.setPassword(password);
	        log.setPassword(passwordEncoder.encode(password));
	        //SignUp.setPassword(passwordEncoder.encode(SignUp.getPassword()));
	        loginLogRepo.save(log);

	        return true;  // Login successful
	    }
	    return false;  // Login failed
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
}
