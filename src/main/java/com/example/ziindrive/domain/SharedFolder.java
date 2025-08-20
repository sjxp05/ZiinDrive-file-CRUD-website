package com.example.ziindrive.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "shared_folder")
public class SharedFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;

    private String folderName;
    private LocalDateTime createdAt;

    @Builder
    public SharedFolder(String folderName) {

        this.folderName = folderName;
        this.createdAt = LocalDateTime.now();

    }
}
