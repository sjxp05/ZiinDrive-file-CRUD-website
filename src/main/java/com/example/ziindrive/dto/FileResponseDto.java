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
    String truncatedName;
    String size;
    String formattedDate;
    boolean favorited;

    public static FileResponseDto fromEntity(FileEntity file) {

        String nameWithoutExt = file.getOriginalName().substring(0, file.getOriginalName().lastIndexOf("."));

        return FileResponseDto.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .truncatedName((nameWithoutExt.length() > 30 ? nameWithoutExt.substring(0, 30) + " ... "
                        : nameWithoutExt) + file.getExtension())
                .size(file.getSize())
                .formattedDate(file.getUploadedAt().format(DateTimeFormatter.ofPattern("yyyy. M. d H:mm")))
                .favorited(file.isFavorited())
                .build();
    }

}
