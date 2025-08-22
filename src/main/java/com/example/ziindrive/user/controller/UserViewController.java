package com.example.ziindrive.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String getLoginView() {
        return "login/loginView";
    }

    @GetMapping("/signup")
    public String getSignupView() {
        return "login/signupView";
    }
}
