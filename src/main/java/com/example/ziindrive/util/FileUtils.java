package com.example.ziindrive.util;

import java.util.Set;

public class FileUtils {

    // 허용되는 모든 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".txt", ".docx", ".hwp", ".xlsx", ".pptx", ".pdf", ".md", // 문서
            ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp", // 이미지
            ".mp3", ".m4a", ".wav", // 오디오
            ".mp4", ".avi", ".mkv", ".mov", // 동영상
            ".zip", ".tar", ".gz", // 압축 파일
            ".c", ".cpp", ".py", ".java", ".js", ".json", ".html", ".xml", // 소스코드
            ".csv", ".yml" // 기타
    );

    // 파일 이름 검사 함수
    public static String checkOriginalName(String fileName) throws Exception {

        if (fileName == null) {
            throw new Exception("올바르지 않은 파일명입니다!");
        }

        fileName = fileName.trim();

        if (fileName.length() == 0) {
            throw new Exception("올바르지 않은 파일명입니다!");
        } else {
            return fileName;
        }
    }

    // 확장자 추출 및 제한 함수
    public static String extractExtension(String fileName) throws Exception {

        String extension;

        if (fileName.lastIndexOf(".") == -1) {
            throw new Exception("올바르지 않은 형식의 파일입니다!");
        }

        extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (ALLOWED_EXTENSIONS.contains(extension)) {
            return extension;

        } else {
            throw new Exception("올바르지 않은 형식의 파일입니다!");
        }
    }

    // 사이즈 계산 및 포맷 함수
    public static String formatSize(long size) {

        double sizeDouble = (double) size;

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
            return String.format("%d ", (long) sizeDouble) + siUnit + "B";

        } else {
            return String.format("%.1f ", sizeDouble) + siUnit + "B";
        }
    }
}
