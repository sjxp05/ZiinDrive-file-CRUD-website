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

    private volatile List<FileEntity> cachedFileList = null; // 쿼리로 찾은 파일 정보들을 캐싱하는 리스트

    // create
    public FileEntity uploadFile(MultipartFile fileInput) throws Exception {

        if (fileInput == null) {
            return null;
        }

        String originalName = FileUtils.checkOriginalName(fileInput.getOriginalFilename());
        String size = FileUtils.formatSize(fileInput.getSize());
        String extension = FileUtils.extractExtension(originalName);
        String storedName = UUID.randomUUID().toString() + extension;

        File savedFile = new File(properties.getUploadPath(), storedName);
        String path = savedFile.getAbsolutePath();

        fileInput.transferTo(savedFile);

        FileEntity entity = FileEntity.builder()
                .originalName(originalName)
                .storedName(storedName)
                .extension(extension)
                .path(path)
                .size(size)
                .build();

        return repository.save(entity);
    }

    // read
    public void findAll() {

        cachedFileList = repository.findAll(holder.getSort());
    }

    // read, search
    public void findWithOptions() {

        // Specification으로 null이 아닌 모든 검색조건 추가
        Specification<FileEntity> specs = List.of(
                FileSpecifications.hasKeyword(holder.getKeyword()),
                FileSpecifications.hasExtension(holder.getExtension()),
                FileSpecifications.uploadedAfter(holder.getFrom()),
                FileSpecifications.uploadedBefore(holder.getTo()))
                .stream().reduce(Specification::and).orElse(null);

        // 검색 및 캐시로 저장
        cachedFileList = repository.findAll(specs, holder.getSort());

    }

    // 캐시 값 반환
    public List<FileResponseDto> getCachedFiles() {
        return cachedFileList == null ? Collections.emptyList()
                : cachedFileList.stream().map(FileResponseDto::fromEntity).toList();
    }

    // 다운로드에 필요한 정보 전달
    public FileDownloadDto getFileResource(Long id) throws IOException {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        Path storedPath = Paths.get(file.getPath());

        if (!Files.exists(storedPath)) {
            throw new FileNotFoundException("file does not exist");
        }

        // 한글 이름일경우 인코딩 필요
        String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8);

        FileDownloadDto dto = FileDownloadDto.builder()
                .encodedName(encodedName)
                .storedPath(storedPath.toString())
                .build();

        return dto;
    }

    // update (*파일이름(사용자에게 보이는 이름)만 수정 가능)
    public boolean renameFile(Long id, String newName) {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 올바른 확장자가 붙어있지 않을 경우 추가
        if (!newName.endsWith(file.getExtension())) {
            newName += file.getExtension();
        }

        if (newName.equals(file.getOriginalName())) {
            return false;
        }

        file.setOriginalName(newName); // Transactional 때문에 자동으로 저장, 새로고침됨
        return true;
    }

    // delete
    public void deleteFile(Long id) {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 디스크에서 먼저 삭제
        File storedPath = new File(file.getPath());
        if (storedPath.exists()) {
            System.out.println(storedPath.delete()); // test
        }

        // DB에서 파일 메타데이터 삭제
        repository.delete(file);
    }
}
