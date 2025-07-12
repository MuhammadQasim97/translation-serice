package com.digitaltolk.translation.service;

import com.digitaltolk.translation.dto.TagDto;
import com.digitaltolk.translation.entity.Tag;
import com.digitaltolk.translation.repository.TagRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    
    @Mock
    private TagRepository tagRepository;
    
    @InjectMocks
    private TagService tagService;
    
    private Tag tag;
    private TagDto tagDto;
    
    @BeforeEach
    void setUp() {
        tag = new Tag("mobile", "Mobile context");
        tag.setId(1L);
        
        tagDto = new TagDto("mobile", "Mobile context");
        tagDto.setId(1L);
    }
    
    @Test
    void createTag_ShouldReturnCreatedTag() {
        // Given
        when(tagRepository.existsByName("mobile")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        
        // When
        TagDto result = tagService.createTag(tagDto);
        
        // Then
        assertNotNull(result);
        assertEquals("mobile", result.getName());
        assertEquals("Mobile context", result.getDescription());
        
        verify(tagRepository).save(any(Tag.class));
    }
    
    @Test
    void createTag_ShouldThrowException_WhenTagExists() {
        // Given
        when(tagRepository.existsByName("mobile")).thenReturn(true);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> tagService.createTag(tagDto));
        verify(tagRepository, never()).save(any(Tag.class));
    }
    
    @Test
    void getTagById_ShouldReturnTag_WhenExists() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        
        // When
        Optional<TagDto> result = tagService.getTagById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("mobile", result.get().getName());
    }
    
    @Test
    void getTagById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<TagDto> result = tagService.getTagById(1L);
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void getAllTags_ShouldReturnPagedResults() {
        // Given - returns Page<Tag>
        List<Tag> tags = List.of(tag);
        Page<Tag> page = new PageImpl<>(tags, PageRequest.of(0, 10), 1);
        when(tagRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        // When
        Page<TagDto> result = tagService.getAllTags(PageRequest.of(0, 10));
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("mobile", result.getContent().get(0).getName());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteTag_ShouldDeleteWhenExists() {
        // Given
        when(tagRepository.existsById(1L)).thenReturn(true);
        
        // When
        assertDoesNotThrow(() -> tagService.deleteTag(1L));
        
        // Then
        verify(tagRepository).deleteById(1L);
    }
    
    @Test
    void deleteTag_ShouldThrowException_WhenNotExists() {
        // Given
        when(tagRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> tagService.deleteTag(1L));
        verify(tagRepository, never()).deleteById(1L);
    }
}
