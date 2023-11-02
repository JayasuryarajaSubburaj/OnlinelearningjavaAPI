package com.onlinelearning.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.onlinelearning.entity.LoginLog;

public interface LoginLogRepository extends MongoRepository<LoginLog, String> {

	 LoginLog findByEmail(String email);
}
