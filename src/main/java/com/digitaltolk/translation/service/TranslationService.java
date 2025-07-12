package com.digitaltolk.translation.service;

import com.digitaltolk.translation.dto.TranslationDto;
import com.digitaltolk.translation.entity.Tag;
import com.digitaltolk.translation.entity.Translation;
import com.digitaltolk.translation.repository.TagRepository;
import com.digitaltolk.translation.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TranslationService {
    
    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;
    
    @Autowired
    public TranslationService(TranslationRepository translationRepository, TagRepository tagRepository) {
        this.translationRepository = translationRepository;
        this.tagRepository = tagRepository;
    }
    
    @CacheEvict(value = "translations", allEntries = true)
    public TranslationDto createTranslation(TranslationDto dto) {
        Translation translation = convertToEntity(dto);
        translation = translationRepository.save(translation);
        return convertToDto(translation);
    }
    
    @CacheEvict(value = "translations", allEntries = true)
    public TranslationDto updateTranslation(Long id, TranslationDto dto) {
        Translation translation = translationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Translation not found with id: " + id));
        
        translation.setKey(dto.getKey());
        translation.setLocale(dto.getLocale());
        translation.setContent(dto.getContent());
        
        // Update tags
        if (dto.getTags() != null) {
            Set<Tag> tags = dto.getTags().stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
            translation.setTags(tags);
        }
        
        translation = translationRepository.save(translation);
        return convertToDto(translation);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "translations", key = "#id")
    public Optional<TranslationDto> getTranslationById(Long id) {
        return translationRepository.findById(id)
            .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<TranslationDto> getTranslationByKeyAndLocale(String key, String locale) {
        return translationRepository.findByKeyAndLocale(key, locale)
            .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TranslationDto> searchTranslations(String key, String content, String locale, String tagName, Pageable pageable) {
        return translationRepository.searchTranslations(key, content, locale, tagName, pageable)
            .map(this::convertToDto);
    }
    
    @Cacheable(value = "translations", key = "#locale")
    @Transactional(readOnly = true)
    public Map<String, String> getTranslationsForLocale(String locale) {
        List<Translation> translations = translationRepository.findByLocaleOrderByKey(locale);
        return translations.stream()
            .collect(Collectors.toMap(
                Translation::getKey,
                Translation::getContent,
                (existing, replacement) -> existing,
                LinkedHashMap::new
            ));
    }
    
    @Transactional(readOnly = true)
    public List<String> getAvailableLocales() {
        return translationRepository.findDistinctLocales();
    }
    
    @CacheEvict(value = "translations", allEntries = true)
    public void deleteTranslation(Long id) {
        if (!translationRepository.existsById(id)) {
            throw new RuntimeException("Translation not found with id: " + id);
        }
        translationRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long getTranslationCount() {
        return translationRepository.countAll();
    }
    
    private Translation convertToEntity(TranslationDto dto) {
        Translation translation = new Translation(dto.getKey(), dto.getLocale(), dto.getContent());
        
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            Set<Tag> tags = dto.getTags().stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
            translation.setTags(tags);
        }
        
        return translation;
    }
    
    private TranslationDto convertToDto(Translation translation) {
        TranslationDto dto = new TranslationDto(translation.getKey(), translation.getLocale(), translation.getContent());
        dto.setId(translation.getId());
        dto.setCreatedAt(translation.getCreatedAt());
        dto.setUpdatedAt(translation.getUpdatedAt());
        
        if (translation.getTags() != null && !translation.getTags().isEmpty()) {
            Set<String> tagNames = translation.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
            dto.setTags(tagNames);
        }
        
        return dto;
    }
    
    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
            .orElseGet(() -> {
                Tag newTag = new Tag(tagName, "Auto-created tag");
                return tagRepository.save(newTag);
            });
    }
}
