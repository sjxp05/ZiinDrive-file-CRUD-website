package com.example.ziindrive.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "metadata_files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;
    private String storedName;
    private String path;
    private String extension;
    private String size;
    private LocalDateTime uploadedAt;
    private String formattedDate;
    private LocalDateTime deletedAt = null;

    private boolean active = true;

    @Builder
    public FileEntity(String originalName, String storedName, String path, String extension, String size) {

        this.originalName = originalName;
        this.storedName = storedName;
        this.path = path;
        this.extension = extension;
        this.size = size;
        this.uploadedAt = LocalDateTime.now();
        this.formattedDate = formatDate(uploadedAt);

    }

    // 날짜 포맷 함수
    public static String formatDate(LocalDateTime uploadedAt) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d H:mm");
        return uploadedAt.format(formatter);
    }

    /*
     * getter(모든 멤버에 있음, 롬복으로 자동 생성)
     * setter는 꼭 필요한것만 한정적으로 만들 예정
     */

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
