package com.example.ziindrive.config;

import java.time.LocalDate;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchOptionHolder {

    private boolean findAll = false; // 검색 조건 없을시 true (default: true)

    private String keyword;
    private String extension;
    private LocalDate from;
    private LocalDate to;

    private Sort sort = Sort.by(Sort.Order.desc("id")); // 현재 정렬 상태 (default: latest)

    public void setStringToSort(String sortString) { // sort기준을 문장에서 실제 Sort로 변환

        sort = switch (sortString) {
            case "name" -> Sort.by(Sort.Order.asc("originalName"));
            case "oldest" -> Sort.by(Sort.Order.asc("id"));
            default -> Sort.by(Sort.Order.desc("id"));
        };
    }

    public String getSortToString() { // 현재 저장된 sort기준을 문자열로 변환

        if (sort.equals(Sort.by(Sort.Order.desc("id")))) {
            return "latest";

        } else if (sort.equals(Sort.by(Sort.Order.asc("originalName")))) {
            return "name";

        } else {
            return "oldest";
        }
    }
}
