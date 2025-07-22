package com.example.ziindrive.dto;

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

    public static FileResponseDto fromEntity(FileEntity file) {

        return FileResponseDto.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .size(file.getSize())
                .formattedDate(file.getFormattedDate())
                .build();
    }

}
