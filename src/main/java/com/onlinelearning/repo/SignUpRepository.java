package com.onlinelearning.repo;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.onlinelearning.entity.SignUpEntity;

@Document
public interface SignUpRepository extends MongoRepository<SignUpEntity,String>{

	SignUpEntity findByEmail(String email);
	
	SignUpEntity findByResetToken(String resetToken);
	
	void deleteByEmail(String email);

}
