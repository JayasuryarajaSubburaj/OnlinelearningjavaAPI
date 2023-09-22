package com.onlinelearning.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlinelearning/")
public class CompilerController {

	@PostMapping("/executeCode")
	public String executePythonCode(@RequestBody String typeOfCode,String Code) {
		if(typeOfCode.equals("python")) {
			runintotime.process("python3 main.py");
		}
		else if (typeOfCode.equals("java")) {
			runintotqime.process("javac main.java");
//			javac main.java
//			java main.java
		}
		else if(typeOfCode.equals("c")) {
			
		}
		else if(typeOfCode.equals("c++")) {
//			g++ main.cpp
//			./a.out
		}
		else if(typeOfCode.equals("react")) {
//			npm start
		}
	}

//	private String executePython(String code) {
//		try {
//			// Create a temporary file and write the code to it.
//			File temp = File.createTempFile("tempfile", ".py");
//			FileWriter writer = new FileWriter(temp);
//			writer.write(code);
//			writer.close();
//
//			// Run the python interpreter on the file.
//			Process p = Runtime.getRuntime().exec("python " + temp.getAbsolutePath());
//
//			// Read the output
//			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String ret = in.readLine();
//
//			temp.delete(); // Delete temporary file
//
//			return ret;
//		} catch (Exception e) {
//			return "Error: " + e.getMessage();
//		}
//	}
}
