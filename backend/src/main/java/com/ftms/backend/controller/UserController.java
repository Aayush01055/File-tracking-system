package com.ftms.backend.controller;

import com.ftms.backend.entity.User;
import com.ftms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(value = "roles", required = false) String roles,
            @RequestHeader("User-Id") String userId) {
        if (roles == null) {
            return ResponseEntity.ok(userRepository.findAll());
        }
        List<String> roleList = Arrays.asList(roles.split(","));
        List<User> users = userRepository.findByRoleIn(roleList);
        return ResponseEntity.ok(users);
    }
}