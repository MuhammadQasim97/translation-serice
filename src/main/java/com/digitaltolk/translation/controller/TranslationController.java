package com.digitaltolk.translation.controller;

import com.digitaltolk.translation.dto.TranslationDto;
import com.digitaltolk.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/translations")
@SecurityRequirement(name = "bearerAuth")
public class TranslationController {
    
    private final TranslationService translationService;
    
    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }
    
    @PostMapping
    public ResponseEntity<TranslationDto> createTranslation(@Valid @RequestBody TranslationDto translationDto) {
        TranslationDto created = translationService.createTranslation(translationDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TranslationDto> updateTranslation(
            @Parameter(description = "Translation ID") @PathVariable Long id,
            @Valid @RequestBody TranslationDto translationDto) {
        TranslationDto updated = translationService.updateTranslation(id, translationDto);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TranslationDto> getTranslationById(
            @Parameter(description = "Translation ID") @PathVariable Long id) {
        return translationService.getTranslationById(id)
            .map(translation -> ResponseEntity.ok(translation))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<TranslationDto>> searchTranslations(
            @Parameter(description = "Translation key to search") @RequestParam(required = false) String key,
            @Parameter(description = "Content to search") @RequestParam(required = false) String content,
            @Parameter(description = "Locale to filter") @RequestParam(required = false) String locale,
            @Parameter(description = "Tag name to filter") @RequestParam(required = false) String tag,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<TranslationDto> results = translationService.searchTranslations(key, content, locale, tag, pageable);
        return ResponseEntity.ok(results);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(
            @Parameter(description = "Translation ID") @PathVariable Long id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/export/{locale}")
    public ResponseEntity<Map<String, String>> exportTranslations(
            @Parameter(description = "Locale code", example = "en") @PathVariable String locale) {
        Map<String, String> translations = translationService.getTranslationsForLocale(locale);
        return ResponseEntity.ok(translations);
    }
    
    @GetMapping("/locales")
    public ResponseEntity<java.util.List<String>> getAvailableLocales() {
        return ResponseEntity.ok(translationService.getAvailableLocales());
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getTranslationCount() {
        return ResponseEntity.ok(translationService.getTranslationCount());
    }
}
