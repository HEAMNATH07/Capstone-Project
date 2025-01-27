package com.example.User.service.Controller;

import com.example.User.service.Model.UserInfo;
import com.example.User.service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserInfo registerUser(@RequestBody UserInfo userInfo) {
        return userService.createUser(userInfo);
    }

    @GetMapping("/{email}")
    public UserInfo getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email).orElse(null);
    }
}