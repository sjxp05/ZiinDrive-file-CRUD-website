package com.example.ziindrive.service;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ziindrive.config.*;
import com.example.ziindrive.dto.*;
import com.example.ziindrive.domain.FileEntity;
import com.example.ziindrive.repository.FileRepository;
import com.example.ziindrive.repository.FileRepository.FileSpecifications;
import com.example.ziindrive.util.FileUtils;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository repository;
    private final FileUploadProperties properties;
    private final SearchOptionHolder holder;

    private volatile List<FileResponseDto> cachedFiles = null;

    // create
    public FileEntity uploadFile(MultipartFile fileInput) throws Exception {

        if (fileInput == null) {
            return null;
        }

        String originalName = FileUtils.validateOriginalName(fileInput.getOriginalFilename());
        String size = FileUtils.formatSize(fileInput.getSize());
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String storedName = UUID.randomUUID().toString() + extension;

        File savedFile = new File(properties.getUploadPath(), storedName);
        fileInput.transferTo(savedFile);

        FileEntity entity = FileEntity.builder()
                .originalName(originalName)
                .storedName(storedName)
                .extension(extension)
                .path(properties.getUploadPath())
                .size(size)
                .build();

        return repository.save(entity);
    }

    // read, search
    public List<FileResponseDto> findWithOptions() {

        List<FileEntity> entities = null;

        if (holder.isFindAll()) {
            entities = repository.findAll(FileSpecifications.isActive(holder.isActive())
                    .and(FileSpecifications.isFavorited(holder.isFavorites())), holder.getSort());

        } else {
            // Specification으로 null이 아닌 모든 검색조건 추가
            Specification<FileEntity> specs = List.of(
                    FileSpecifications.hasKeyword(holder.getKeyword()),
                    FileSpecifications.hasExtension(holder.getExtension()),
                    FileSpecifications.uploadedAfter(holder.getFrom()),
                    FileSpecifications.uploadedBefore(holder.getTo()),
                    FileSpecifications.isActive(holder.isActive()),
                    FileSpecifications.isFavorited(holder.isFavorites()))
                    .stream().reduce(Specification::and).orElse(null);

            entities = repository.findAll(specs, holder.getSort());
        }

        // 받은 검색결과를 DTO 리스트로 만들어 캐싱+반환하기
        cachedFiles = entities == null ? Collections.emptyList()
                : entities.stream().map(FileResponseDto::fromEntity).toList();
        return cachedFiles;
    }

    // 정렬만 바뀌었을 때
    public List<FileResponseDto> changeSortOnly() {

        cachedFiles = cachedFiles.stream().sorted(holder.getSortToComparator()).toList();
        return cachedFiles;
    }

    // 다운로드에 필요한 정보 전달
    public FileDownloadDto getFileResource(Long id) throws IOException {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        if (file.getDeletedAt() != null) {
            throw new RuntimeException("file is not active");
        }

        Path storedPath = Paths.get(file.getPath(), file.getStoredName());
        if (!Files.exists(storedPath)) {
            throw new FileNotFoundException("file does not exist");
        }

        // 한글 이름 및 특수문자 등의 경우 인코딩 필요
        String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);

        FileDownloadDto dto = FileDownloadDto.builder()
                .encodedName(encodedName)
                .storedPath(storedPath.toString())
                .build();

        return dto;
    }

    // update (*파일이름(사용자에게 보이는 이름)만 수정 가능)
    public String renameFile(Long id, String newName) throws Exception {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 올바른 확장자가 붙어있지 않을 경우 추가한 뒤 validate 함수로 보내기
        if (!newName.endsWith(file.getExtension())) {
            newName += file.getExtension();
        }

        newName = FileUtils.validateOriginalName(newName); // 이름 길이 및 글자수 제한

        if (newName.equals(file.getOriginalName())) {
            return null;
        }

        file.setOriginalName(newName); // Transactional 때문에 자동으로 저장, 새로고침됨
        return newName; // validateOriginalName에서 다듬은 새 이름 보내주기
    }

    // 즐겨찾기 여부 수정
    public boolean favoriteFile(Long id, boolean change) throws Exception {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        if (change == true) {
            if (file.isFavorited() == false) {
                file.setFavorited(true);
                return true;

            } else {
                return false;
            }
        } else {
            if (file.isFavorited() == true) {
                file.setFavorited(false);
                return true;

            } else {
                return false;
            }
        }
    }

    // delete
    public void deleteFile(Long id) throws Exception {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 이미 삭제된 파일의 경우 막아놓기
        if (file.getDeletedAt() != null) {
            throw new Exception();
        }

        // 디스크 위치 이동
        Path source = Paths.get(file.getPath(), file.getStoredName());
        Path target = Paths.get(properties.getBinPath(), file.getStoredName());

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

        // DB에 기록된 파일 정보 수정 (경로 및 휴지통 여부)
        file.setPath(properties.getBinPath());
        file.setDeleted(); // Transactional 때문에 자동으로 저장, 새로고침됨
    }

    // 파일 복원
    public void restoreFile(Long id) throws Exception {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 삭제되지 않았으면 복구 안되게 막아놓기
        if (file.getDeletedAt() == null) {
            throw new Exception();
        }

        // 디스크 위치 이동
        Path source = Paths.get(file.getPath(), file.getStoredName());
        Path target = Paths.get(properties.getUploadPath(), file.getStoredName());

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

        // DB에 기록된 파일 정보 수정 (경로 및 휴지통 여부)
        file.setPath(properties.getUploadPath());
        file.setRestored(); // Transactional 때문에 자동으로 저장, 새로고침됨
    }

    // 영구삭제
    public void shredFile(Long id) throws Exception {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 삭제된 파일인지 먼저 확인
        if (file.getDeletedAt() == null) {
            throw new Exception("file cannot be shredded");
        }

        // 디스크에서 먼저 삭제
        File storedPath = new File(file.getPath(), file.getStoredName());
        if (storedPath.exists()) {
            storedPath.delete();
        }

        // DB에서 파일 메타데이터 삭제
        repository.delete(file);
    }

    // 휴지통 비우기
    public void shredAll() throws Exception {

        repository.delete(FileSpecifications.isActive(false));
    }
}
