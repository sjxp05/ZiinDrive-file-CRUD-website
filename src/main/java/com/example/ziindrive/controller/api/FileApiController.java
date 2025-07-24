package com.example.ziindrive.controller.api;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.*;
import org.springframework.http.*;

import org.springframework.web.multipart.MultipartFile;

import com.example.ziindrive.config.SearchOptionHolder;
import com.example.ziindrive.dto.*;
import com.example.ziindrive.service.FileService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
public class FileApiController {

    private final FileService service;
    private final SearchOptionHolder holder;

    // 파일 불러오기 get
    @GetMapping("/api/files")
    public ResponseEntity<List<FileResponseDto>> getFileData() {

        // test
        System.out.println("received GET request (view all)");

        service.findWithOptions();
        return ResponseEntity.ok().body(service.getCachedFiles());
    }

    // 현재 정렬 상태 get
    @GetMapping("/api/files/sort")
    public ResponseEntity<String> getSort() {

        // test
        System.out.println("received GET request (Current Sort)");

        return ResponseEntity.ok().body(holder.getSortToString());
    }

    // 정렬된 결과 get
    @GetMapping("/api/files/sort/{sort}")
    public ResponseEntity<List<FileResponseDto>> setSort(@PathVariable("sort") String sort) {

        // test
        System.out.println("received GET request (Sort)");

        if (!sort.equals(holder.getSortToString())) {

            holder.setStringToSort(sort);
            service.findWithOptions();

            return ResponseEntity.ok().body(service.getCachedFiles());

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    // 업로드 post
    @PostMapping("/api/files")
    public ResponseEntity<String> uploadFile(@RequestParam("fileInput") MultipartFile fileInput) {

        // test
        System.out.println("received POST request (Upload)");

        try {
            if (service.uploadFile(fileInput) == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            System.out.println("업로드 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    // 다운로드 get
    @GetMapping("/api/files/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") Long id) {

        // test
        System.out.println("received GET request (Download)");

        try {
            FileDownloadDto dto = service.getFileResource(id);
            Resource resource = new UrlResource(Paths.get(dto.getStoredPath()).toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getEncodedName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.out.println("다운로드 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    // 이름수정 patch
    @PatchMapping("/api/files")
    public ResponseEntity<List<FileResponseDto>> renameFile(@RequestBody FileRenameDto dto) {

        // test
        System.out.println("received PATCH request");

        if (service.renameFile(dto.getId(), dto.getNewName())) {
            service.findWithOptions();
            return ResponseEntity.ok().body(service.getCachedFiles()); // 이름이 바뀌었을때: 200 OK

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 이름 바꿀 필요 없을때: 204 No content
        }
    }

    // 삭제 delete
    @DeleteMapping("/api/files/{id}")
    public ResponseEntity<List<FileResponseDto>> deleteFile(@PathVariable("id") Long id) {

        // test
        System.out.println("received DELETE request");

        try {
            service.deleteFile(id);
            service.findWithOptions(); // 삭제한거 빼고 다시 검색하기

            return ResponseEntity.ok().body(service.getCachedFiles());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
