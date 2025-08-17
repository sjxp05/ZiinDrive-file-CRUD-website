package com.example.ziindrive.controller.api;

import java.nio.file.Paths;
import java.util.*;

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
    public ResponseEntity<List<FileResponseDto>> getFileData(
            @RequestParam(name = "sort", required = false) String sort) {

        if (sort == null) { // 정렬 변경 없이 새 페이지만 로드되었을 때: 200 OK + 파일 목록
            return ResponseEntity.ok().body(service.findWithOptions());

        } else { // 정렬 버튼을 눌렀을 때
            if (sort.equals(holder.getSortToString())) { // 이전 정렬 상태와 같음: 204 No Content
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

            } else { // 정렬상태가 달라짐: 200 OK + 정렬만 바꿔서 다시 구한 파일 캐시

                holder.setStringToSort(sort);
                return ResponseEntity.ok().body(service.changeSortOnly());
            }
        }
    }

    // 현재 정렬 상태 get
    @GetMapping("/api/files/sort")
    public ResponseEntity<String> getSort() {

        return ResponseEntity.ok().body(holder.getSortToString());
    }

    // 업로드 post
    @PostMapping("/api/files")
    public ResponseEntity<String> uploadFile(@RequestParam("fileInput") MultipartFile fileInput) {
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
    @PatchMapping("/api/files/{id}")
    public ResponseEntity<?> renameFile(@PathVariable("id") Long id, @RequestBody Map<String, String> renameInfo) {
        try {
            String validatedName = service.renameFile(id, renameInfo.get("newName"));
            /*
             * 이름이 달라졌을때: 바뀐 이름 반환
             * 이름이 기존과 같을때: null 반환
             * 길이나 예약어 조건에 맞지 않을 경우: Exception 발생
             */

            if (validatedName == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 이름 바꿀 필요 없을때: 204 No content

            } else {
                // 이름순 정렬이 새롭게 필요할때: 200 OK + 정렬된 데이터 (json)
                if (holder.getSortToString().equals("name")) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .body(service.findWithOptions());
                }
                // 이름만 바꾸면 되고 정렬 새로 필요 없을때: 200 OK + 새로 정한 이름만 (text)
                service.findWithOptions(); // 캐시만 새로고침하기

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                        .body(validatedName);
            }

        } catch (Exception e) { // 파일 이름이 조건에 맞지 않을때: 400 Bad Request + 에러 메시지
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 즐겨찾기 patch
    @PatchMapping("/api/files/favorite/{id}")
    public ResponseEntity<String> favoriteFile(@PathVariable("id") Long id,
            @RequestBody Map<String, String> favoriteInfo) {

        boolean change = Boolean.parseBoolean(favoriteInfo.get("change"));

        try {
            if (service.favoriteFile(id, change)) {
                service.findWithOptions(); // 캐시 새로고침
                return ResponseEntity.ok().body("즐겨찾기 반영 성공");

            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("존재하지 않는 파일");
        }
    }

    // 삭제 delete
    @DeleteMapping("/api/files/{id}")
    public ResponseEntity<List<FileResponseDto>> deleteFile(@PathVariable("id") Long id) {
        try {
            service.deleteFile(id);
            // 삭제한거 빼고 다시 검색한 결과 보내기
            return ResponseEntity.ok().body(service.findWithOptions());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 즐겨찾기 목록 업데이트 patch
    @PatchMapping("/api/favorites/{id}")
    public ResponseEntity<List<FileResponseDto>> reloadFavorites(@PathVariable("id") Long id) {
        try {
            if (service.favoriteFile(id, false)) {
                return ResponseEntity.ok().body(service.findWithOptions());

            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 휴지통 복원 patch
    @PatchMapping("/api/bin/{id}")
    public ResponseEntity<List<FileResponseDto>> restoreFile(@PathVariable("id") Long id) {
        try {
            service.restoreFile(id);
            return ResponseEntity.ok().body(service.findWithOptions());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 휴지통 영구삭제 delete
    @DeleteMapping("/api/bin/{id}")
    public ResponseEntity<List<FileResponseDto>> shredFile(@PathVariable("id") Long id) {
        try {
            service.shredFile(id);
            return ResponseEntity.ok().body(service.findWithOptions());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 휴지통 비우기 delete
    @DeleteMapping("/api/bin")
    public ResponseEntity<?> shredAll() {
        try {
            service.shredAll();
            return ResponseEntity.ok().body(Collections.emptyList());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
