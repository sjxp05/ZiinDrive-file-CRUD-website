package com.example.ziindrive.user.entity;

import com.example.ziindrive.common.enums.Role;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user")
public class UserEntity {

    @Id
    private String userId;

    private String password;
    private String nickname;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public UserEntity(String userId, String password, String nickname, String email) {

        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = Role.MANAGER;
    }

    // 비밀번호 글자 수만 전달 (보안 목적)
    public int getPasswordLength() {
        return this.password.length();
    }

    // setter (필요한 것만)

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
