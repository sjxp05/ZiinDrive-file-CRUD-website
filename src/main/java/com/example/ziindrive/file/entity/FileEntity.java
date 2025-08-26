package com.example.ziindrive.file.entity;

import java.time.LocalDateTime;

import com.example.ziindrive.user.entity.UserEntity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    // 읽기 전용 FK
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    private String originalName;
    private String storedName;
    private String path;
    private String extension;
    private String size;
    private LocalDateTime uploadedAt;
    private LocalDateTime deletedAt = null;
    private boolean favorited = false;

    @Builder
    public FileEntity(
            UserEntity user, String originalName, String storedName,
            String path, String extension, String size) {

        this.user = user;
        this.originalName = originalName;
        this.storedName = storedName;
        this.path = path;
        this.extension = extension;
        this.size = size;
        this.uploadedAt = LocalDateTime.now();
    }

    /*
     * getter(모든 멤버에 있음, 롬복으로 자동 생성)
     * setter는 꼭 필요한것만 한정적으로 만들 예정
     */

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    // 삭제, 복원 함수
    public void setDeleted() {
        if (this.deletedAt != null) {
            return;
        }

        this.deletedAt = LocalDateTime.now();
    }

    public void setRestored() {
        this.deletedAt = null;
    }
}
