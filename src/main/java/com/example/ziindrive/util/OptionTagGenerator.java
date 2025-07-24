package com.example.ziindrive.util;

import java.util.List;

public class OptionTagGenerator {

    // 별건 아니고 그냥 옵션태그 노가다를 돌리기위해 쓴것...
    public static void main(String[] args) {

        List<String> ALLOWED_EXTENSIONS = List.of(
                ".txt", ".docx", ".hwp", ".xlsx", ".pptx", ".pdf", ".md", // 문서
                ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp", // 이미지
                ".mp3", ".m4a", ".wav", // 오디오
                ".mp4", ".avi", ".mkv", ".mov", // 동영상
                ".zip", ".tar", ".gz", // 압축 파일
                ".c", ".cpp", ".py", ".java", ".js", ".json", ".html", ".xml", // 소스코드
                ".csv", ".yml" // 기타
        );

        ALLOWED_EXTENSIONS = ALLOWED_EXTENSIONS.stream().map(ext -> ext.substring(1)).toList();

        for (String ext : ALLOWED_EXTENSIONS) {
            System.out.println("<option value=\"" + ext + "\">" + ext + "</option>");
        }
    }
}
