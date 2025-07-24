package com.example.ziindrive.controller.view;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;

import com.example.ziindrive.config.SearchOptionHolder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FileViewController {

    private final SearchOptionHolder holder;

    // 모든 파일 get
    @GetMapping("/files")
    public String getAllFiles() {

        // test
        System.out.println("received GET request (AllFiles)");

        holder.setFindAll(true); // 모든 파일 검색하도록 설정
        holder.setActive(true); // 휴지통에 없는 파일만 가능

        return "files/filesView";
    }

    // 검색 get (파라미터 주소창에 띄움!)
    @GetMapping("/files/search")
    public String searchFiles(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "extension", required = false) String extension,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {

        // test
        System.out.println("received GET request (Search)");

        holder.setFindAll(false);
        holder.setActive(true);

        // 검색 조건 바꾸기
        holder.setKeyword(keyword);
        holder.setExtension(extension);
        holder.setFrom(from);
        holder.setTo(to);

        return "files/filesView";
    }

    // 휴지통 뷰 보기
    @GetMapping("/files/bin")
    public String viewTrashBin() {

        holder.setActive(false);
        return "files/trashBin";
    }
}
