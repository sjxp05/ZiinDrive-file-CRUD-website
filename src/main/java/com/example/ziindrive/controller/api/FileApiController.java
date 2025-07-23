package com.example.ziindrive.controller.api;

import java.nio.file.Paths;

import org.springframework.core.io.*;
import org.springframework.http.*;

import org.springframework.web.multipart.MultipartFile;

import com.example.ziindrive.config.SearchOptionHolder;
import com.example.ziindrive.dto.FileDownloadDto;
import com.example.ziindrive.service.FileService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileApiController {

    private final FileService service;
    private final SearchOptionHolder holder;

    // 정렬 post
    @PostMapping("/api/files/{sort}")
    public ResponseEntity<Resource> setSort(@PathVariable("sort") String sort, HttpSession session) {

        // test
        System.out.println("received POST request (Sort)");

        if (!sort.equals(session.getAttribute("sort"))) {

            session.setAttribute("sort", sort);
            holder.setStringToSort(sort);
            service.findWithOptions();

        }

        return ResponseEntity.ok().body(null);
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

    // 이름수정 put
    @PutMapping("/api/files/{id}/{newName}")
    public ResponseEntity<Resource> renameFile(@PathVariable("id") Long id,
            @PathVariable("newName") String newName) {

        // test
        System.out.println("received PUT request");

        service.renameFile(id, newName);
        return ResponseEntity.ok().body(null);
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
