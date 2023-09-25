package com.onlinelearning.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonRunner {
	 public static void runPythonCode() {
		
		ProcessBuilder processBuilder = new ProcessBuilder("python3", "/home/surya/main.py");
		try {
			Process process = processBuilder.start();

			// Read the output
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// Wait for the process to exit
			process.waitFor();

		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}

