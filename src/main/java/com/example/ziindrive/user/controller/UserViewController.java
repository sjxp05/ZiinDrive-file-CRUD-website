package com.example.ziindrive.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String getLoginView() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String getSignupView() {
        return "user/signup";
    }

    @GetMapping("/user/info")
    public String getUserInfoMainView() {
        return "user/info";
    }

    @GetMapping("/user/confirm")
    public String confirmPasswordView() {
        return "user/confirm";
    }

    @GetMapping("/user/nickname")
    public String changeNicknameView() {
        return "user/nickname";
    }

    @GetMapping("/user/email")
    public String changeEmailView() {
        return "user/email";
    }

    @GetMapping("/user/password")
    public String resetPasswordView() {
        return "user/password";
    }

    @GetMapping("/user/account")
    public String getAccountDeleteView() {
        return "user/delete";
    }
}
