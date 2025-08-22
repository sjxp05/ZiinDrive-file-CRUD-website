package com.example.ziindrive.user.controller;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ziindrive.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/login")
    public ResponseEntity<String> validateLogin(@RequestBody Map<String, String> loginInfo) {

        try {
            String loginStatus = userService.validateLogin(loginInfo.get("id"), loginInfo.get("password"));
            return ResponseEntity.ok().body(loginStatus);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/signup")
    public ResponseEntity<String> createAccount(@RequestBody Map<String, String> signupInfo) {

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/api/signup/id")
    public ResponseEntity<String> checkDuplicateId(@RequestBody Map<String, String> idCheckInfo) {

        try {
            userService.checkDuplicateId(idCheckInfo.get("id"));
            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
