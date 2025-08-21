package com.example.ziindrive.file.dto;

import java.time.format.DateTimeFormatter;

import com.example.ziindrive.common.util.FileUtils;
import com.example.ziindrive.file.entity.FileEntity;

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
    String truncatedName;
    String size;
    String formattedDate;
    boolean favorited;

    public static FileResponseDto fromEntity(FileEntity file) {

        return FileResponseDto.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .truncatedName(FileUtils.getTruncatedName(file.getOriginalName()))
                .size(file.getSize())
                .formattedDate(file.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy. M. d H:mm")))
                .favorited(file.isFavorited())
                .build();
    }
}
