package com.example.ziindrive.file.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.ziindrive.file.entity.FileEntity;

import java.time.LocalDate;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {

    // 기본 CRUD 제공.

    // specification 만들기
    public class FileSpecifications {

        public static Specification<FileEntity> isUserId(Long userId) {

            return (root, query, cb) -> {
                return cb.equal(root.get("userId"), userId);
            };
        }

        public static Specification<FileEntity> hasKeyword(String keyword) {

            return (root, query, cb) -> {

                if (keyword == null || keyword.isBlank()) {
                    return null;
                }

                return cb.like(cb.lower(cb.function("REGEXP_REPLACE", String.class,
                        root.get("originalName"),
                        cb.literal("[ _\\'\\\"\\-\\+]"),
                        cb.literal(""))),
                        "%" + keyword.replaceAll("[ _\\'\\\"\\-\\+]", "").toLowerCase() + "%");
            };
        }

        public static Specification<FileEntity> hasExtension(String extension) {

            return (root, query, cb) -> {
                if (extension == null || extension.isBlank()) {
                    return null;
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

        public static Specification<FileEntity> isActive(boolean active) {

            return (root, query, cb) -> {
                if (active) {
                    return cb.isNull(root.get("deletedAt"));

                } else {
                    return cb.isNotNull(root.get("deletedAt"));
                }
            };
        }

        public static Specification<FileEntity> isFavoritesMenu(boolean isFavoritesMenu) {

            return (root, query, cb) -> {
                if (isFavoritesMenu) {
                    return cb.isTrue(root.get("favorited"));

                } else {
                    return null;
                }
            };
        }
    }
}
