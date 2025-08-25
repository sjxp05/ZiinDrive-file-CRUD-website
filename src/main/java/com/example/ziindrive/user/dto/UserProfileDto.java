package com.example.ziindrive.user.dto;

import com.example.ziindrive.user.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private String userId;
    private String nickname;

    public static UserProfileDto fromEntity(UserEntity entity) {

        return builder()
                .userId(entity.getUserId())
                .nickname(entity.getNickname())
                .build();
    }
}
