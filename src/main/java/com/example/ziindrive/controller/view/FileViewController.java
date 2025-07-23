package com.example.ziindrive.controller.view;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.ziindrive.config.SearchOptionHolder;
import com.example.ziindrive.service.FileService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FileViewController {

    private final FileService service;
    private final SearchOptionHolder holder;

    // 모든 파일 get
    @GetMapping("/files")
    public String getAllFiles(Model model) {

        // test
        System.out.println("received GET request (AllFiles)");

        service.findAll(); // 모든 파일 검색

        model.addAttribute("fileList", service.getCachedFiles());
        model.addAttribute("currentSort", holder.getSortToString());

        return "files/filesView";
    }

    // 검색 get (파라미터 주소창에 띄움!)
    @GetMapping("/files/search")
    public String searchFiles(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "extension", required = false) String extension,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to,
            Model model) {

        // test
        System.out.println("received GET request (Search)");

        // 검색 조건 바꾸기
        holder.setFindAll(false);

        holder.setKeyword(keyword);
        holder.setExtension(extension);
        holder.setFrom(from);
        holder.setTo(to);

        service.findWithOptions(); // 조건 검색

        model.addAttribute("fileList", service.getCachedFiles());
        model.addAttribute("currentSort", holder.getSortToString());

        return "files/filesView";
    }

}
