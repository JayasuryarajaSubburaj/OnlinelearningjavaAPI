package com.onlinelearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.SignUpEntity;
import com.onlinelearning.service.ForgotPasswordService;
import com.onlinelearning.service.SignUpService;

@RestController
@RequestMapping("/onlinelearning/")
public class ForgetPasswordController {

	@Autowired
	SignUpService signupservice;
	
	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
	@PostMapping("/forgot-password")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody SignUpEntity signent) {
        if(forgotPasswordService.initiatePasswordReset(signent.getEmail())) {
        return ResponseEntity.ok("Password reset link has been sent to your email.");
        }
        else {
        	return ResponseEntity.status(401).body("Email not found");
        }
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody SignUpEntity signent) {
		if (forgotPasswordService.validateResetToken(signent.getResetToken())) {
			forgotPasswordService.resetPassword(signent.getResetToken(), signent.getNewPassword());
			return ResponseEntity.ok("Password has been reset.");
		} 
		else {
			return ResponseEntity.badRequest().body("Invalid or expired token.");
		}
	}
	
}
