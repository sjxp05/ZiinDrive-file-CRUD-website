package com.example.ziindrive.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ziindrive.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 아이디(unique)로 찾기

    Optional<UserEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    // 이메일(unique) 이미 가입되었는지 확인

    boolean existsByEmail(String email);
}