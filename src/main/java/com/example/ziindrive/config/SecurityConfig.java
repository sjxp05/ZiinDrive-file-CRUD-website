package com.example.ziindrive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // 시작 전 로그인 기능 끄기
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http
    // .csrf(csrf -> csrf.disable()) // CSRF 보호 끔 (API 용도일 땐 보통 disable)
    // .formLogin(form -> form.disable()) // 기본 /login 폼 막기
    // .httpBasic(basic -> basic.disable()) // 기본 HTTP Basic 인증 막기
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/api/auth/**").permitAll() // 회원가입/로그인 API는 누구나 접근
    // .anyRequest().authenticated() // 나머지는 인증 필요
    // );
    // return http.build();
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
