package com.example.ziindrive.common.util;

import java.util.UUID;

public class UserInfoUtils {

    public static final String ID_RULE_WARNING = "ID는 4~20자로 영문 대소문자와 숫자를 혼합하여 만들어 주세요.",
            PW_RULE_WARNING = "비밀번호는 8~20자로 영문, 숫자, 특수기호 등을 혼합하여 만들어 주세요.",
            NICKNAME_LENGTH_WARNING = "닉네임은 20자 이내로 만들어 주세요. (선택)",
            EMAIL_RULE_WARNING = "실제로 사용하는 이메일을 입력해 주세요.";

    public static boolean checkIdRule(String id) {

        if (id.length() < 4 || id.length() > 20) {
            return false;
        }

        for (char i : id.toCharArray()) {

            if ((i < 'A' || i > 'Z') && (i < 'a' || i > 'z') && (i < '0' || i > '9')) {
                // 영문, 숫자 외 다른 문자일 때
                return false;
            }
        }

        return true;
    }

    public static boolean checkPasswordRule(String rawPassword) {

        if (rawPassword.length() < 8 || rawPassword.length() > 20) {
            return false;
        }

        for (char i : rawPassword.toCharArray()) {
            if (i < 32 || i > 126) {
                return false;
            }
        }

        return true;
    }

    public static String validateOrGenerateNickname(String nickname) {

        if (nickname.length() > 20) {
            return null;
        }

        // 닉네임 안정했을때 랜덤 생성
        if (nickname.length() == 0 || nickname.isBlank() || nickname == null) {
            nickname = "User"
                    + UUID.randomUUID()
                            .toString()
                            .toUpperCase()
                            .substring(0, 6);
            // test
            System.out.println("랜덤 닉네임: " + nickname);
        }

        return nickname;
    }

    public static boolean checkEmailRule(String email) {

        if (email.length() == 0) {
            return false;
        }

        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")
                || email.startsWith("@") || email.endsWith("@")) {
            /*
             * 1. @ 기호가 없는경우
             * 2. @ 기호가 2개 이상인 경우
             * 3. @ 기호의 앞뒤에 다른 문자가 없는 경우
             */
            return false;
        }

        for (char i : email.toCharArray()) {
            if (i <= 32 || i > 126) {
                return false;
            }
        }

        return true;
    }
}
