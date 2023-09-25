package com.onlinelearning.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlinelearning.entity.CodeRequestEntity;
import com.onlinelearning.util.PythonRunner;

@RestController
@RequestMapping("/onlinelearning/")
public class CompilerController {

	@PostMapping("/execute")
	public String executeCode(@RequestBody CodeRequestEntity request) {
		switch (request.getType()) {
		case "python":
			PythonRunner.runPythonCode();
			break;
		case "c":
			// call the CRunner code here
			break;
		case "cpp":
			// call the CppRunner code here
			break;
		case "java":
			// call the JavaRunner code here
			break;
		default:
			return "Invalid type!";
		}
		return "Execution finished!";
	}
}
