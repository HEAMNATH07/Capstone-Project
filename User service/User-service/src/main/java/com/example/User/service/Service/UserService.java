package com.example.User.service.Service;

import com.example.User.service.Model.UserInfo;
import com.example.User.service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfo createUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        return userRepository.save(userInfo);
    }

    public Optional<UserInfo> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}