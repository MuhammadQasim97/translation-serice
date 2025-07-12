package com.digitaltolk.translation.service;

import com.digitaltolk.translation.dto.TranslationDto;
import com.digitaltolk.translation.entity.Tag;
import com.digitaltolk.translation.entity.Translation;
import com.digitaltolk.translation.repository.TagRepository;
import com.digitaltolk.translation.repository.TranslationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {
    
    @Mock
    private TranslationRepository translationRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @InjectMocks
    private TranslationService translationService;
    
    private Translation translation;
    private TranslationDto translationDto;
    private Tag tag;
    
    @BeforeEach
    void setUp() {
        tag = new Tag("mobile", "Mobile context");
        tag.setId(1L);
        
        translation = new Translation("welcome.message", "en", "Welcome!");
        translation.setId(1L);
        translation.setTags(Set.of(tag));
        
        translationDto = new TranslationDto("welcome.message", "en", "Welcome!");
        translationDto.setId(1L);
        translationDto.setTags(Set.of("mobile"));
    }
    
    @Test
    void createTranslation_ShouldReturnCreatedTranslation() {
        // Given
        when(tagRepository.findByName("mobile")).thenReturn(Optional.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        
        // When
        TranslationDto result = translationService.createTranslation(translationDto);
        
        // Then
        assertNotNull(result);
        assertEquals("welcome.message", result.getKey());
        assertEquals("en", result.getLocale());
        assertEquals("Welcome!", result.getContent());
        assertTrue(result.getTags().contains("mobile"));
        
        verify(translationRepository).save(any(Translation.class));
    }
    
    @Test
    void getTranslationById_ShouldReturnTranslation_WhenExists() {
        // Given
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        
        // When
        Optional<TranslationDto> result = translationService.getTranslationById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("welcome.message", result.get().getKey());
        assertEquals("en", result.get().getLocale());
    }
    
    @Test
    void getTranslationById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(translationRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<TranslationDto> result = translationService.getTranslationById(1L);
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void searchTranslations_ShouldReturnPagedResults() {
        // Given - returns Page<Translation>
        List<Translation> translations = List.of(translation);
        Page<Translation> page = new PageImpl<>(translations, PageRequest.of(0, 10), 1);
        when(translationRepository.searchTranslations(anyString(), isNull(), isNull(),isNull(), any(Pageable.class)))
            .thenReturn(page);
        
        // When
        Page<TranslationDto> result = translationService.searchTranslations("welcome", null, null, null,PageRequest.of(0, 10));
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("welcome.message", result.getContent().get(0).getKey());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }
    
    @Test
    void searchTranslations_WithAllParameters_ShouldWork() {
        // Given
        List<Translation> translations = List.of(translation);
        Page<Translation> page = new PageImpl<>(translations, PageRequest.of(0, 10), 1);
        when(translationRepository.searchTranslations("welcome", "Welcome","en", "mobile", PageRequest.of(0, 10)))
            .thenReturn(page);
        
        // When
        Page<TranslationDto> result = translationService.searchTranslations("welcome", "Welcome","en", "mobile", PageRequest.of(0, 10));
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("welcome.message", result.getContent().get(0).getKey());
    }
    

    @Test
    void getTranslationsForLocale_ShouldReturnTranslationMap() {
        // Given
        List<Translation> translations = List.of(translation);
        when(translationRepository.findByLocaleOrderByKey("en")).thenReturn(translations);
        
        // When
        Map<String, String> result = translationService.getTranslationsForLocale("en");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Welcome!", result.get("welcome.message"));
    }
    
    @Test
    void updateTranslation_ShouldUpdateExistingTranslation() {
        // Given
        TranslationDto updateDto = new TranslationDto("welcome.message", "en", "Updated Welcome!");
        updateDto.setTags(Set.of("desktop"));
        
        Tag desktopTag = new Tag("desktop", "Desktop context");
        desktopTag.setId(2L);
        
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        when(tagRepository.findByName("desktop")).thenReturn(Optional.of(desktopTag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        
        // When
        TranslationDto result = translationService.updateTranslation(1L, updateDto);
        
        // Then
        assertNotNull(result);
        verify(translationRepository).save(any(Translation.class));
    }
    
    @Test
    void deleteTranslation_ShouldDeleteWhenExists() {
        // Given
        when(translationRepository.existsById(1L)).thenReturn(true);
        
        // When
        assertDoesNotThrow(() -> translationService.deleteTranslation(1L));
        
        // Then
        verify(translationRepository).deleteById(1L);
    }
    
    @Test
    void deleteTranslation_ShouldThrowException_WhenNotExists() {
        // Given
        when(translationRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> translationService.deleteTranslation(1L));
        verify(translationRepository, never()).deleteById(1L);
    }
    
    @Test
    void createTranslation_ShouldCreateNewTag_WhenTagDoesNotExist() {
        // Given
        TranslationDto dtoWithNewTag = new TranslationDto("test.key", "en", "Test Content");
        dtoWithNewTag.setTags(Set.of("newtag"));
        
        Tag newTag = new Tag("newtag", "Auto-created tag");
        newTag.setId(2L);
        
        when(tagRepository.findByName("newtag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);
        
        // When
        TranslationDto result = translationService.createTranslation(dtoWithNewTag);
        
        // Then
        assertNotNull(result);
        verify(tagRepository).save(any(Tag.class));
        verify(translationRepository).save(any(Translation.class));
    }
}
