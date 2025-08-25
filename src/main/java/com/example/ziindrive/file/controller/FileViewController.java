package com.example.ziindrive.file.controller;

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

        holder.setFindAll(true); // 모든 파일 검색하도록 설정
        holder.setActive(true); // 휴지통에 없는 파일만 가능
        holder.setFavoritesMenu(false); // 즐겨찾기 여부 상관없음

        // 휴지통 -> 메인으로 갈 경우 최신순 정렬
        if (holder.getSortToString().equals("deleted")) {
            holder.setStringToSort("latest");
        }

        return "files/filesView";
    }

    // 검색 get (파라미터 주소창에 띄움!)
    @GetMapping("/files/search")
    public String searchFiles(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "extension", required = false) String extension,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {

        holder.setFindAll(false);
        holder.setActive(true);
        holder.setFavoritesMenu(false);

        // 검색 조건 바꾸기
        holder.setKeyword(keyword);
        holder.setExtension(extension);
        holder.setFrom(from);
        holder.setTo(to);

        // 휴지통 -> 메인으로 갈 경우 최신순 정렬
        if (holder.getSortToString().equals("deleted")) {
            holder.setStringToSort("latest");
        }

        return "files/filesView";
    }

    // 즐겨찾기 뷰 보기
    @GetMapping("/favorites")
    public String viewFavorites() {

        holder.setFindAll(true);
        holder.setActive(true);
        holder.setFavoritesMenu(true); // 즐겨찾기한 파일만 선택됨

        // 휴지통 -> 즐찾으로 갈 경우 최신순 정렬
        if (holder.getSortToString().equals("deleted")) {
            holder.setStringToSort("latest");
        }

        return "files/favorites";
    }

    // 즐겨찾기 항목 중 검색 get (파라미터 주소창에 띄움!)
    @GetMapping("/favorites/search")
    public String searchFavorites(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "extension", required = false) String extension,
            @RequestParam(name = "from", required = false) LocalDate from,
            @RequestParam(name = "to", required = false) LocalDate to) {

        holder.setFindAll(false);
        holder.setActive(true);
        holder.setFavoritesMenu(true);

        // 검색 조건 바꾸기
        holder.setKeyword(keyword);
        holder.setExtension(extension);
        holder.setFrom(from);
        holder.setTo(to);

        // 휴지통 -> 메인으로 갈 경우 최신순 정렬
        if (holder.getSortToString().equals("deleted")) {
            holder.setStringToSort("latest");
        }

        return "files/favorites";
    }

    // 휴지통 뷰 보기
    @GetMapping("/bin")
    public String viewTrashBin() {

        holder.setFindAll(true);
        holder.setActive(false);
        holder.setFavoritesMenu(false);

        // 삭제된 순서대로 보기
        holder.setStringToSort("deleted");

        return "files/trashBin";
    }
}
