package com.example.ziindrive.user.controller;

import java.util.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.example.ziindrive.user.dto.*;
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
            Long id = userService.validateLogin(loginInfo.get("loginId"), loginInfo.get("password"));
            if (id != null) {
                // 비밀번호 맞음: 200 OK
                return ResponseEntity.ok().body(Long.toString(id));
            } else {
                // 비밀번호 틀림: 401 UNAUTHORIZED
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("incorrect password");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원가입
    @PostMapping("/api/users/signup")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> signupInfo) {

        try {
            userService.createAccount(signupInfo);
            return ResponseEntity.ok().body(null);

        } catch (DataIntegrityViolationException e) {
            // 중복 id 또는 email의 경우: 409 CONFLICT
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            // 형식에 맞지 않을때: 404 BAD REQUEST
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // id 중복체크
    @PostMapping("/api/users/signup/id")
    public ResponseEntity<String> checkDuplicateId(@RequestBody Map<String, String> idCheckInfo) {

        try {
            if (userService.checkDuplicateId(idCheckInfo.get("loginId"))) {
                // 중복 없을 경우: 200 OK
                return ResponseEntity.ok().body(null);
            } else {
                // 중복 id의 경우: 409 CONFLICT
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 ID입니다.");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보 일부 조회 (id, 닉네임만 띄워주는 프로필 정보)
    @GetMapping("/api/users/profile/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable(name = "id") Long id) {

        try {
            UserProfileDto dto = userService.getUserProfile(id);
            return ResponseEntity.ok().body(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보 중 선택한 항목만 조회
    @PostMapping("/api/users/info")
    public ResponseEntity<?> getUserInfo(
            @RequestBody Map<String, String> info) {

        try {
            // UserFullDto dto = userService.getUserInfo(id);
            long id = Long.parseLong(info.get("id"));
            return ResponseEntity.ok().body(userService.getSelectedInfo(id, info.get("key")));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 비번 찾기 시 사용자 인증
    @PostMapping("/api/users/verify/id")
    public ResponseEntity<?> verifyUserById(@RequestBody Map<String, String> authInfo) {

        try {
            Long id = userService.verifyUserById(authInfo.get("loginId"), authInfo.get("email"));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(Map.of("id", id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 비밀번호변경, 회원탈퇴 전 비밀번호 확인
    @PostMapping("/api/users/verify/password")
    public ResponseEntity<String> checkPwBeforeDelete(@RequestBody Map<String, String> userInfo) {

        try {
            if (userService.verifyUserByPassword(Long.parseLong(userInfo.get("id")), userInfo.get("password"))) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다. 다시 시도해 주세요.");
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보수정
    @PatchMapping("/api/users/info")
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

    // 회원탈퇴
    @DeleteMapping("/api/users/account/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable(name = "id") Long id) {

        try {
            userService.deleteAccount(id);
            return ResponseEntity.ok().body(null);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
