package com.onlinelearning.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaRunner {
    public static void main(String[] args) {
        // Step 1: Compile the Java program
        ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "/home/surya/hello.java");
        try {
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // Check if compilation was successful
            if (compileProcess.exitValue() != 0) {
                System.out.println("Compilation error!");
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println("ERROR: " + errorLine);          
                }
                return;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // Step 2: Run the compiled Java program
        ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-cp", "/home/surya", "HelloWorld");
        try {
            Process runProcess = runProcessBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to exit
            runProcess.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
