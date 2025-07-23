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
    public FileEntity uploadFile(MultipartFile fileInput) throws IOException {

        if (fileInput == null) {
            return null;
        }

        String originalName = fileInput.getOriginalFilename();

        if (originalName == null) {
            return null;
        } else {
            originalName = originalName.trim();
        }

        String size = formatSize(Long.toString(fileInput.getSize()));
        String extension = originalName.lastIndexOf(".") == -1 ? ""
                : originalName.substring(originalName.lastIndexOf("."));
        String storedName = UUID.randomUUID().toString() + extension;

        File savedFile = new File(properties.getUploadPath(), storedName);
        String path = savedFile.getAbsolutePath();

        try {
            fileInput.transferTo(savedFile);
        } catch (Exception e) {
            System.out.println("저장 실패: " + e.getMessage());
        }

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

        holder.setFindAll(true);
        cachedFileList = repository.findAll(holder.getSort());
    }

    // read, search
    public void findWithOptions() {

        // 검색 및 캐시로 저장
        if (holder.isFindAll() == true || cachedFileList == null) {
            cachedFileList = repository.findAll(holder.getSort());

        } else {
            // Specification으로 null이 아닌 모든 검색조건 추가
            Specification<FileEntity> specs = List.of(
                    FileSpecifications.hasKeyword(holder.getKeyword()),
                    FileSpecifications.hasExtension(holder.getExtension()),
                    FileSpecifications.uploadedAfter(holder.getFrom()),
                    FileSpecifications.uploadedBefore(holder.getTo()))
                    .stream().reduce(Specification::and).orElse(null);

            cachedFileList = repository.findAll(specs, holder.getSort());
        }
    }

    // 캐시가 비었는지 확인
    public boolean isCacheEmpty() {
        return (cachedFileList.isEmpty());
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
    public void renameFile(Long id, String newName) {

        FileEntity file = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("file does not exist"));

        // 올바른 확장자가 붙어있지 않을 경우에만 추가
        if (newName.lastIndexOf(".") == -1
                || !newName.substring(newName.lastIndexOf(".")).equals(file.getExtension())) {
            newName += file.getExtension();
        }

        file.setOriginalName(newName); // Transactional 때문에 자동으로 저장, 새로고침됨
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

    // 사이즈 계산 및 포맷 함수
    public String formatSize(String size) {

        double sizeDouble = Double.parseDouble(size);

        String siUnit = "";
        String[] units = { "K", "M", "G" };

        for (String e : units) {

            if (sizeDouble / 1024 >= 1) {

                sizeDouble /= 1024;
                siUnit = e;

            } else {
                break;
            }
        }

        if (sizeDouble == (long) sizeDouble) {
            return String.format("%d ", sizeDouble) + siUnit + "B";

        } else {
            return String.format("%.1f ", sizeDouble) + siUnit + "B";
        }
    }
}
