package com.onlinelearning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineLearningApplication.class, args);
	}

//	@Bean
//	public ExecutorService executorService() {
//		return Executors.newFixedThreadPool(8);
//	}
//
	@Bean
	public ThreadPoolExecutor threadPoolExecutor() {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
	}

	@Bean
	public Semaphore semaphore() {
		return new Semaphore(8); // 8 permits
	}

}
