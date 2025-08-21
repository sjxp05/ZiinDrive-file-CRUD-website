package com.example.ziindrive.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String getLoginView() {
        return "login/loginView";
    }
}
