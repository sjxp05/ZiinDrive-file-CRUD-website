package com.example.ziindrive.dto;

import java.time.format.DateTimeFormatter;

import com.example.ziindrive.domain.FileEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDto {

    Long id;
    String originalName;
    String size;
    String formattedDate;
    boolean favorited;

    public static FileResponseDto fromEntity(FileEntity file) {

        return FileResponseDto.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .size(file.getSize())
                .formattedDate(file.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy. M. d H:mm")))
                .favorited(file.isFavorited())
                .build();
    }

}
