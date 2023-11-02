package com.onlinelearning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.onlinelearning.util.Constants;

@RestController
@RequestMapping("/onlinelearning")
public class StateController {

	@GetMapping("/states")
	public ResponseEntity<List<String>> getIndianStates() {
		return ResponseEntity.ok(Constants.INDIAN_STATES);
	}
}
