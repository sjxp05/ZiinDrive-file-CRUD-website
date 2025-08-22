package com.example.ziindrive.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ziindrive.user.entity.UserEntity;
import com.example.ziindrive.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 아이디 및 비번 인증 절차
    public String validateLogin(String id, String rawPassword) throws Exception {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("account does not exist"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new Exception("uncorrect password");
        }

        return "OK";
    }

    // 회원가입을 위한 정보 검사 및 등록 절차
    public void createAccount(String newId, String rawPassword, String nickname, String email) throws Exception {

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

        // String checkIdWarning = "ID 중복확인을 먼저 진행해 주세요!";
        String pwRuleWarning = "비밀번호는 8~20자로 영문, 숫자, 특수기호 등을 혼합하여 만들어 주세요.";
        String nicknameLengthWarning = "닉네임은 20자 이내로 만들어 주세요. (선택)";

        // 아이디 중복검사가 되었는지 확인 (얘는 아마 js에서 세션으로 처리해야할듯)

        // 비밀번호 검사
        if (rawPassword.length() < 8 || rawPassword.length() > 20) {
            throw new Exception(pwRuleWarning);
        }

        for (char i : rawPassword.toCharArray()) {
            if (i < 32 || i > 126) {
                throw new Exception(pwRuleWarning);
            }
        }

        // 비밀번호 해싱
        String encodedPw = passwordEncoder.encode(rawPassword);

        // 닉네임 글자수 등 검사
        if (nickname.length() > 20) {
            throw new Exception(nicknameLengthWarning);
        }

        // 닉네임 안정했을때 랜덤 생성
        if (nickname.length() == 0) {
            nickname = "User"
                    + UUID.randomUUID()
                            .toString()
                            .toUpperCase()
                            .substring(0, 6);
        }

        // 이메일 검사

        UserEntity user = UserEntity.builder()
                .userId(newId)
                .password(encodedPw)
                .nickname(nickname)
                .email(email)
                .build();

        userRepository.save(user);
    }

    /*
     * id 조건 및 중복 확인
     * id 중복확인 버튼을 눌렀을 때 작동
     */
    public void checkDuplicateId(String newId) throws Exception {

        String idRuleWarning = "ID는 4~20자로 영문 대소문자와 숫자를 혼합하여 만들어 주세요.";
        String existingIdWarning = "이미 사용 중인 ID입니다.";

        if (newId.length() < 4 || newId.length() > 20) {
            throw new Exception(idRuleWarning);
        }

        for (char i : newId.toCharArray()) {

            if ((i < 'A' || i > 'Z') && (i < 'a' || i > 'z') && (i < '0' || i > '9')) {
                throw new Exception(idRuleWarning);
            }
        }

        if (userRepository.existsById(newId)) {
            throw new Exception(existingIdWarning);
        }
    }
}
