package com.ryyde.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ryyde.user.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}
