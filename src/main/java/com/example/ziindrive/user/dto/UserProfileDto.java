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

    private Long id;
    private String loginId;
    private String nickname;

    public static UserProfileDto fromEntity(UserEntity entity) {

        return builder()
                .id(entity.getId())
                .loginId(entity.getLoginId())
                .nickname(entity.getNickname())
                .build();
    }
}
