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
public class UserFullDto {

    private Long id;
    private String loginId;
    private int pwLength;
    private String nickname;
    private String email;
    private String roleToString;

    public static UserFullDto fromEntity(UserEntity entity) {

        return builder()
                .id(entity.getId())
                .loginId(entity.getLoginId())
                .pwLength(entity.getPasswordLength())
                .nickname(entity.getNickname())
                .roleToString(entity.getRole().name())
                .build();
    }
}
