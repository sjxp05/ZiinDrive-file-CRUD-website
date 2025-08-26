package com.example.ziindrive.user.service;

import java.util.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ziindrive.common.util.UserInfoUtils;
import com.example.ziindrive.user.dto.*;
import com.example.ziindrive.user.entity.UserEntity;
import com.example.ziindrive.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 아이디 및 비번 인증 절차
    public Long validateLogin(String loginId, String rawPassword) throws Exception {

        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("account with this id does not exist"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return null;
        }

        return user.getId();
    }

    // 회원가입을 위한 정보 검사 및 등록 절차
    public void createAccount(Map<String, String> signupInfo) throws Exception {
        /*
         * id 조건
         * - 영문 대소문자 또는 숫자 혼합해서 4자 이상~20자 이하
         * pw 조건
         * - 영문 대소문자, 숫자, 기호, 각종 특수문자 등을 혼합해서 8자 이상~20자 이하
         * - ASCII로 나타낼 수 있는 모든 문자 및 공백 가능 (줄바꿈 제외)
         * 닉네임 조건
         * - 문자 종류 상관없이 20자 이하 근데이제 깨져도 책임안짐ㅋㅋ
         * 이메일? 조건
         * - 실제 사용할수있는 도메인 주소 사용 (몇개만 따로 select option 만들고 직접쓰기도 가능하게)
         */

        String loginId = signupInfo.get("loginId");
        String rawPassword = signupInfo.get("password");
        String nickname = signupInfo.get("nickname");
        String email = signupInfo.get("email");

        // 아이디 중복검사가 되었는지 확인 (얘는 아마 js에서 세션으로 처리하거나 미리 api를 호출하거나 해야할듯)

        // 아이디 검사
        if (!UserInfoUtils.checkIdRule(loginId)) {
            throw new Exception(UserInfoUtils.ID_RULE_WARNING);
        }

        // 비밀번호 검사
        if (!UserInfoUtils.checkPasswordRule(rawPassword)) {
            throw new Exception(UserInfoUtils.PW_RULE_WARNING);
        }
        // 비밀번호 해싱
        String encodedPw = passwordEncoder.encode(rawPassword);

        // 닉네임 글자수 검사 또는 랜덤 생성
        if ((nickname = UserInfoUtils.validateOrGenerateNickname(nickname)) == null) {
            throw new Exception(UserInfoUtils.NICKNAME_LENGTH_WARNING);
        }

        // 이메일 검사
        if (!UserInfoUtils.checkEmailRule(email)) {
            throw new Exception(UserInfoUtils.EMAIL_RULE_WARNING);
        }

        UserEntity user = UserEntity.builder()
                .loginId(loginId)
                .password(encodedPw)
                .nickname(nickname)
                .email(email)
                .build();

        try {
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            // loginId(UNIQUE) 가 중복될 경우
            throw e;
        }
    }

    /*
     * id 조건 및 중복 확인
     * id 중복확인 버튼을 눌렀을 때 작동
     */
    public boolean checkDuplicateId(String loginId) throws Exception {

        if (!UserInfoUtils.checkIdRule(loginId)) {
            throw new Exception(UserInfoUtils.ID_RULE_WARNING);
        }

        if (userRepository.existsByLoginId(loginId)) {
            return false;
        }
        return true;
    }

    // 회원정보 불러오기 (id, 닉네임 등 프로필 표시에 필요한 것만)
    public UserProfileDto getUserProfile(Long id) throws Exception {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("account with this id does not exist"));

        return UserProfileDto.fromEntity(user);
    }

    // 회원정보 전체 불러오기
    public UserFullDto getUserInfo(Long id) throws Exception {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("account with this id does not exist"));

        return UserFullDto.fromEntity(user);
    }

    // 회원정보 수정
    public boolean modifyUserInfo(Map<String, String> userInfo) throws Exception {

        UserEntity user = userRepository.findById(Long.parseLong(userInfo.get("id")))
                .orElseThrow(() -> new RuntimeException("account with this id does not exist"));

        boolean hasKeys = false;

        if (userInfo.containsKey("password")) {

            if (!hasKeys) {
                hasKeys = true;
            }

            String password = userInfo.get("password");

            if (UserInfoUtils.checkPasswordRule(password)) {
                user.setPassword(password);
            } else {
                throw new Exception(UserInfoUtils.PW_RULE_WARNING);
            }
        }

        if (userInfo.containsKey("nickname")) {

            if (!hasKeys) {
                hasKeys = true;
            }

            String nickname = UserInfoUtils.validateOrGenerateNickname(userInfo.get("nickname"));

            if (nickname == null) {
                user.setNickname(nickname);
            } else {
                throw new Exception(UserInfoUtils.NICKNAME_LENGTH_WARNING);
            }
        }

        if (userInfo.containsKey("email")) {

            if (!hasKeys) {
                hasKeys = true;
            }

            String email = userInfo.get("email");

            if (UserInfoUtils.checkEmailRule(email)) {
                user.setEmail(email);
            } else {
                throw new Exception(UserInfoUtils.EMAIL_RULE_WARNING);
            }
        }

        return hasKeys;
    }

    // 회원 계정 삭제
    public void deleteAccount(Long id) throws Exception {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("account with this id does not exist"));

        userRepository.delete(user);
    }
}
