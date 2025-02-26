package com.example.rho_interview_challenge.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.rho_interview_challenge.userDocument.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUserName(String username);

    Optional<User> findByUserName(String username);

}