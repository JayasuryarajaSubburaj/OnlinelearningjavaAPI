package com.onlinelearning.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.repo.SignUpRepository;

@Service
public class ForgotPasswordService {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private SignUpRepository signupRepo;
	
	public boolean initiatePasswordReset(String email) {
		SignUpEntity user = signupRepo.findByEmail(email);
		if (user != null) {
			user.setResetToken(UUID.randomUUID().toString());
			user.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Token expires in 1 hour
			signupRepo.save(user);

			// You should implement the sendResetPasswordEmail method in your EmailService.
			String resetLink = user.getResetToken();
			emailService.sendResetPasswordEmail(email, resetLink);
			return true;
		}
		return false;
	}

	public boolean validateResetToken(String token) {
		SignUpEntity user = signupRepo.findByResetToken(token);
		return user != null && user.getTokenExpiryDate().isAfter(LocalDateTime.now());
	}

	public void resetPassword(String token, String newPassword) {
		SignUpEntity user = signupRepo.findByResetToken(token);
		if (user != null && user.getTokenExpiryDate().isAfter(LocalDateTime.now())) {
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setResetToken(null); // Clear token
			user.setTokenExpiryDate(null); // Clear token expiry
			signupRepo.save(user);
		} 
		else {
			throw new RuntimeException("Invalid or expired reset token.");
		}
	}

}
