package com.example.ziindrive.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.ziindrive.user.entity.UserEntity;
import com.example.ziindrive.user.repository.UserRepository;
import com.example.ziindrive.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    UserService userService;

    @Test
    void signupSuccess() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "mockId2",
                "password", "passsss_word",
                "nickname", "",
                "email", "amuna@udinga.com");

        // user 객체 만들어진것을 저장했을때
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        try { // 객체 생성, 테이블에 저장
            userService.createAccount(signupInfo);
        } catch (Exception e) {
        }

        // then
        verify(userRepository).save(any(UserEntity.class));

        assertThat(userRepository.findByLoginId("mockId")
                .orElseThrow(() -> new EntityNotFoundException())
                .getEmail().equals("amuna@udinga.com"));

        // verify(userRepository).findByLoginId("mockId");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void signupFailByLoginId() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "사용자아이디",
                "password", "password486",
                "nickname", "",
                "email", "amuna@udinga.com");

        assertThatThrownBy(() -> userService.createAccount(signupInfo))
                .hasMessageContaining("ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void signupFailByExistingId() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "myid",
                "password", "password486",
                "nickname", "",
                "email", "amuna@udinga.com");

        assertThatThrownBy(() -> userService.createAccount(signupInfo))
                .isInstanceOf(DataIntegrityViolationException.class);

        verifyNoInteractions(userRepository);
    }

    @Test
    void signupFailByNicknameLength() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "mockId",
                "password", "password_486",
                "nickname", "thissssisssssmyyyyyyniiiiiiicknnnnnnammeeeeeeeeeee",
                "email", "amuna@udinga.com");

        assertThatThrownBy(() -> userService.createAccount(signupInfo))
                .hasMessageContaining("닉네임");

        verifyNoInteractions(userRepository);
    }

    @Test
    void signupFailByEmail() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "mockId",
                "password", "password_486",
                "nickname", "",
                "email", "udinga.com");

        assertThatThrownBy(() -> userService.createAccount(signupInfo))
                .hasMessageContaining("이메일");

        verifyNoInteractions(userRepository);
    }

    @Test
    void checkDuplicateIdSuccess() {

        try {
            assertThat(userService.checkDuplicateId("sampleId")).isEqualTo(true);
        } catch (Exception e) {
        }

        verify(userRepository).existsByLoginId("sampleId");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void checkIdFailByRules() {

        assertThatThrownBy(() -> userService.checkDuplicateId("아이디"))
                .hasMessageContaining("ID");

        verifyNoInteractions(userRepository);
    }

    @Test
    void checkIdFailWhenDuplicate() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "myid",
                "password", "password486",
                "nickname", "",
                "email", "amuna@udinga.com");

        try {
            userService.createAccount(signupInfo);
        } catch (Exception e) {
        }

        try {
            assertThat(userService.checkDuplicateId("myid")).isEqualTo(false);
        } catch (Exception e) {
        }

        verify(userRepository).existsByLoginId("myid");
    }

    @Test
    void modifyInfoSuccess() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "mockId2",
                "password", "passsss_word",
                "nickname", "",
                "email", "amuna@udinga.com");

        try {
            userService.createAccount(signupInfo);
        } catch (Exception e) {
        }

        Map<String, String> info = Map.of(
                "id", "2",
                "password", "password_486");

        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        try {
            assertThat(userService.modifyUserInfo(info)).isEqualTo(true);
        } catch (Exception e) {
        }

        verify(userRepository).findById(2L);
        verify(userRepository, times(2)).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void modifyFailByPasswordRule() {

        Map<String, String> signupInfo = Map.of(
                "loginId", "mockId2",
                "password", "passsss_word",
                "nickname", "",
                "email", "amuna@udinga.com");

        try {
            userService.createAccount(signupInfo);
        } catch (Exception e) {
        }

        Map<String, String> info = Map.of(
                "id", "2",
                "password", "ps486");

        assertThatThrownBy(() -> userService.modifyUserInfo(info))
                .hasMessageContaining("비밀번호");

        verify(userRepository).findById(2L);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void modifyFailWhenIdNotExist() {

        Map<String, String> info = Map.of(
                "id", "99",
                "password", "passs486",
                "nickname", "메가우쿨렐레");

        assertThatThrownBy(() -> userService.modifyUserInfo(info))
                .hasMessageContaining("id does not exist");

        verify(userRepository).findById(99L);
        verify(userRepository, never()).save(any());
    }
}
