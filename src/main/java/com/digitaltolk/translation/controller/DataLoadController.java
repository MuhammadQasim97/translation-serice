package com.digitaltolk.translation.controller;

import com.digitaltolk.translation.service.AuthService;
import com.digitaltolk.translation.service.DataLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utility")
public class DataLoadController {

    private final DataLoaderService dataLoaderService;

    @Autowired
    public DataLoadController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @PostMapping("/loadPreDefindData")
    public ResponseEntity<String> loadPreDefindData() throws Exception {
        return ResponseEntity.ok(dataLoaderService.load());
    }
}
