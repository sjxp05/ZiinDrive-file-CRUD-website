package com.example.ziindrive.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.ziindrive.domain.FileEntity;

import java.time.LocalDate;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {

    // 기본 CRUD 제공.

    // specification 만들기
    public class FileSpecifications {

        public static Specification<FileEntity> hasKeyword(String keyword) {
            return (root, query, cb) -> {
                if (keyword == null || keyword.isBlank()) {
                    return null;
                }
                return cb.like(root.get("originalName"), "%" + keyword + "%");
            };
        }

        public static Specification<FileEntity> hasExtension(String extension) {
            return (root, query, cb) -> {
                if (extension == null || extension.isBlank()) {
                    return null;
                }
                if (extension.equals("none")) {
                    return cb.equal(root.get("extension"), "");
                }
                return cb.like(root.get("extension"), "%." + extension);
            };
        }

        public static Specification<FileEntity> uploadedAfter(LocalDate from) {
            return (root, query, cb) -> {
                if (from == null) {
                    return null;
                }
                return cb.greaterThanOrEqualTo(root.get("uploadedAt"), from.atStartOfDay());
            };
        }

        public static Specification<FileEntity> uploadedBefore(LocalDate to) {
            return (root, query, cb) -> {
                if (to == null) {
                    return null;
                }
                return cb.lessThan(root.get("uploadedAt"), to.plusDays(1).atStartOfDay());
            };
        }
    }
}
