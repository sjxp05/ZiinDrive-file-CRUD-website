package com.example.ziindrive.user.controller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ziindrive.user.dto.UserFullDto;
import com.example.ziindrive.user.dto.UserProfileDto;
import com.example.ziindrive.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<String> validateLogin(@RequestBody Map<String, String> loginInfo) {

        try {
            if (userService.validateLogin(loginInfo.get("id"), loginInfo.get("password"))) {
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("incorrect password");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<String> createAccount(@RequestBody Map<String, String> signupInfo) {

        try {
            userService.createAccount(signupInfo);
            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // id 중복체크
    @PostMapping("/api/users/id")
    public ResponseEntity<String> checkDuplicateId(@RequestBody Map<String, String> idCheckInfo) {

        try {
            userService.checkDuplicateId(idCheckInfo.get("id"));
            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보 일부 조회 (id, 닉네임만 띄워주는 프로필 정보)
    @GetMapping("/api/users/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable(name = "userId") String userId) {

        try {
            UserProfileDto dto = userService.getUserProfile(userId);
            return ResponseEntity.ok().body(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보 전체 조회
    @GetMapping("/api/users/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable(name = "userId") String userId) {

        try {
            UserFullDto dto = userService.getUserInfo(userId);
            return ResponseEntity.ok().body(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보수정
    @PatchMapping("/api/users")
    public ResponseEntity<?> modifyUserInfo(@RequestBody Map<String, String> userInfo) {

        try {
            if (userService.modifyUserInfo(userInfo)) {
                return ResponseEntity.ok().body(null);

            } else { // 변경 사항이 없을 때
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원탈퇴 전 비밀번호 확인
    @PostMapping("/api/users/account")
    public ResponseEntity<?> checkPwBeforeDelete(@RequestBody Map<String, String> userInfo) {

        return validateLogin(userInfo);
    }

    // 회원탈퇴
    @DeleteMapping("/api/users/account/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable(name = "userId") String userId) {

        try {
            userService.deleteAccount(userId);
            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // // 로그아웃 구현 (필요시)
    // @PostMapping("/api/users/logout")
    // public ResponseEntity<?> logout(@RequestBody Map<String, String> payload) {

    // return ResponseEntity.ok().build();
    // }
}
