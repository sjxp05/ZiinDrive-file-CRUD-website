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
        System.out.println("received GET request (Send Files)");

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
        System.out.println("received GET request (Set Sort)");

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
    public ResponseEntity<?> renameFile(@RequestBody FileRenameDto dto) {

        // test
        System.out.println("received PATCH request (Rename)");

        try {
            String validatedName = service.renameFile(dto.getId(), dto.getNewName());
            /*
             * 이름이 달라졌을때: 바뀐 이름 반환
             * 이름이 기존과 같을때: null 반환
             * 길이나 예약어 조건에 맞지 않을 경우: Exception 발생
             */

            if (validatedName != null) {
                // 이름순 정렬이 새롭게 필요할때: 200 OK + 정렬된 데이터 (json)
                if (holder.getSortToString().equals("name")) {

                    service.findWithOptions();
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .body(service.getCachedFiles());
                }
                // 이름만 바꾸면 되고 정렬 새로 필요 없을때: 200 OK + 새로 정한 이름만 (text)
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                        .body(validatedName);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 이름 바꿀 필요 없을때: 204 No content
            }

        } catch (Exception e) { // 파일 이름이 조건에 맞지 않을때: 400 Bad Request + 에러 메시지
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 삭제 delete
    @DeleteMapping("/api/files/{id}")
    public ResponseEntity<List<FileResponseDto>> deleteFile(@PathVariable("id") Long id) {

        // test
        System.out.println("received DELETE request (Delete)");

        try {
            service.deleteFile(id);
            service.findWithOptions(); // 삭제한거 빼고 다시 검색하기

            return ResponseEntity.ok().body(service.getCachedFiles());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
