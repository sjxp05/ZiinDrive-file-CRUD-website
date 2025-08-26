package com.example.ziindrive.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

import com.example.ziindrive.user.entity.UserEntity;

@SpringBootTest
public class UserApiTest {

    UserEntity mock;

    @BeforeEach
    void makeMockAccount() {

        mock = UserEntity.builder()
                .loginId("mockId")
                .password("passsss_word")
                .nickname("mock")
                .email("amugae@portal.com")
                .build();
    }

    @Test
    void signupTest() {

    }

    @Test
    void loginTest() {

    }

    @Test
    void getProfileTest() {

    }

    @Test
    void getFullInfoTest() {

    }

    @Test
    void modifyInfoTest() {

    }

    @Test
    void deleteAccountTest() {

    }
}
