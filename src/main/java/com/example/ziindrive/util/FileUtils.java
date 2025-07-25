package com.example.ziindrive.util;

import java.util.Set;

public class FileUtils {

    // 허용되지 않는 문자
    private static final Set<Character> INVALID_CHARS = Set.of(
            '<', '>', ':', '"', '/', '\\', '|', '?', '*');

    // 허용되는 모든 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".txt", ".docx", ".hwp", ".xlsx", ".pptx", ".pdf", ".md", // 문서
            ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp", // 이미지
            ".mp3", ".m4a", ".wav", // 오디오
            ".mp4", ".avi", ".mkv", ".mov", // 동영상
            ".zip", ".tar", ".gz", ".7z", // 압축 파일
            ".c", ".cpp", ".py", ".java", ".js", ".json", ".html", ".xml", // 소스코드
            ".csv", ".yml" // 기타
    );

    // 윈도우 예약어 (이름 바꿀때 넣지 못하게)
    private static final Set<String> RESERVED_WINDOWS_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

    // 파일 이름 검사 함수
    public static String validateOriginalName(String fileName) throws Exception {

        if (fileName == null) {
            throw new Exception("올바르지 않은 파일명입니다!");
        }

        fileName = fileName.trim();

        String extension = extractExtension(fileName);
        fileName = fileName.substring(0, fileName.lastIndexOf(".")).trim(); // 확장자 제거한 파일이름만 검사

        if (fileName.length() == 0) {
            throw new Exception("올바르지 않은 파일명입니다!");

        } else if (fileName.length() > 255) {
            throw new Exception("파일명이 너무 깁니다!");

        } else if (RESERVED_WINDOWS_NAMES.contains(fileName)) { // 윈도우 예약어인지 검사
            throw new Exception("'" + fileName + "' 는 파일 이름으로 사용할 수 없습니다.");

        } else {
            for (char c : fileName.toCharArray()) {
                if (INVALID_CHARS.contains(c)) {
                    throw new Exception("파일명에는 다음과 같은 문자를 사용할 수 없습니다.\n< > : \" / \\ | ? *");
                }
            }

            return fileName + extension;
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
    public static String formatSize(long size) throws Exception {

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

        // 용량 제한 (yml설정에는 넉넉하게 100기가로 잡아놓고 실제 가능한 파일은 1기가 미만)
        if (siUnit.equals("G")) {
            throw new Exception("1 GB 미만의 파일만 업로드 가능합니다!");
        }

        if (sizeDouble == (long) sizeDouble) {
            // 정수일땐 .0 없이 출력
            return String.format("%d ", (long) sizeDouble) + siUnit + "B";

        } else {
            // 최대 소수 1자리까지 출력
            return String.format("%.1f ", sizeDouble) + siUnit + "B";
        }
    }
}
