package com.example.ziindrive.controller.api;

import java.nio.file.Paths;

import org.springframework.core.io.*;
import org.springframework.http.*;

import org.springframework.web.multipart.MultipartFile;

import com.example.ziindrive.config.SearchOptionHolder;
import com.example.ziindrive.dto.*;
import com.example.ziindrive.service.FileService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileApiController {

    private final FileService service;
    private final SearchOptionHolder holder;

    // 정렬 get
    @GetMapping("/api/files")
    public ResponseEntity<Resource> setSort(@RequestParam("sort") String sort) {

        // test
        System.out.println("received GET request (Sort)");

        if (!sort.equals(holder.getSortToString())) {

            holder.setStringToSort(sort);
            return ResponseEntity.ok().build();

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

    }

    // 업로드 post
    @PostMapping("/api/files")
    public ResponseEntity<Resource> uploadFile(@RequestParam("fileInput") MultipartFile fileInput) {

        // test
        System.out.println("received POST request (Upload)");

        try {
            service.uploadFile(fileInput);
        } catch (Exception e) {
            System.out.println("업로드 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(null);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    // 이름수정 patch
    @PatchMapping("/api/files")
    public ResponseEntity<?> renameFile(@RequestBody FileRenameDto dto) {

        // test
        System.out.println("received PATCH request");

        if (service.renameFile(dto.getId(), dto.getNewName())) {
            return ResponseEntity.ok().build(); // 이름이 바뀌었을때: 200 OK

        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 이름 바꿀 필요 없을때: 204 No content
        }
    }

    // 삭제 delete
    @DeleteMapping("/api/files/{id}")
    public ResponseEntity<Resource> deleteFile(@PathVariable("id") Long id) {

        // test
        System.out.println("received DELETE request");

        service.deleteFile(id);
        return ResponseEntity.ok().body(null);
    }
}
