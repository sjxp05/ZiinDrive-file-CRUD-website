package com.example.ziindrive.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.example.ziindrive.config.SearchOptionHolder;
import com.example.ziindrive.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileApiController {

    private final FileService service;
    private final SearchOptionHolder holder;
}
