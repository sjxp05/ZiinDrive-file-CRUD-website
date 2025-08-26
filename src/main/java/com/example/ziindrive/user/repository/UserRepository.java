package com.example.ziindrive.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ziindrive.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}