package com.example.ziindrive.domain;

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

    @Builder
    public UserEntity(String userId, String password, String nickname, String email) {

        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
}
