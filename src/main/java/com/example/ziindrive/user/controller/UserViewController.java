package com.example.ziindrive.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/user/settings")
    public String getUserSettingsView() {
        return "user/settings";
    }

    @GetMapping("/user/verify/id")
    public String verifyByIdView() {
        return "user/verifyId";
    }

    @GetMapping("/user/verify/password")
    public String verifyByPasswordView() {
        return "user/verifyPassword";
    }

    @GetMapping("/user/reset")
    public String resetPasswordView(@RequestParam("key") String key) {

        if (key.equals("password")) {
            return "user/reset/password";
        } else if (key.equals("nickname")) {
            return "user/reset/nickname";
        } else if (key.equals("email")) {
            return "user/reset/email";
        } else {
            return "error/4xx";
        }
    }

    @GetMapping("/user/account")
    public String getAccountDeleteView() {
        return "user/delete";
    }
}
