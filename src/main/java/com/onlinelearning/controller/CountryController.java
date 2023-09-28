package com.onlinelearning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.util.Country;

@RestController
@RequestMapping("/onlinelearning")
public class CountryController {

//	@GetMapping("/country")
//	public ResponseEntity<String> getCountry() {
//		return ResponseEntity.ok("India");
//	}
	
	@GetMapping("/country")
	public ResponseEntity<List<String>> getCountry(){
		return ResponseEntity.ok(Country.COUNTRY);
	}
}

