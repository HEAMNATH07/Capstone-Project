package com.example.User.service.Repository;

import com.example.User.service.Model.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserInfo,Long> {
    Optional<UserInfo> findByName(String username);

    Optional<UserInfo> findByEmail(String email);
}