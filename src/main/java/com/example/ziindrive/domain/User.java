package com.example.ziindrive.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private String userId;

    private String password;
    private String nickname;

    @Builder
    public User(String userId, String password, String nickname) {

        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
    }
}
