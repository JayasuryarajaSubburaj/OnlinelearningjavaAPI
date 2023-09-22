package com.onlinelearning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlinelearning")
public class CountryController {

	@GetMapping("/country")
	public ResponseEntity<String> getCountry() {
		return ResponseEntity.ok("India");
	}
}

