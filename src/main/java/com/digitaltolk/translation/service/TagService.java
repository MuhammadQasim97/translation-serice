package com.digitaltolk.translation.service;

import com.digitaltolk.translation.dto.TagDto;
import com.digitaltolk.translation.entity.Tag;
import com.digitaltolk.translation.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TagService {
    
    private final TagRepository tagRepository;
    
    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    public TagDto createTag(TagDto dto) {
        if (tagRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Tag with name '" + dto.getName() + "' already exists");
        }
        
        Tag tag = new Tag(dto.getName(), dto.getDescription());
        tag = tagRepository.save(tag);
        return convertToDto(tag);
    }
    
    public TagDto updateTag(Long id, TagDto dto) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!tag.getName().equals(dto.getName()) && tagRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Tag with name '" + dto.getName() + "' already exists");
        }
        
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        
        tag = tagRepository.save(tag);
        return convertToDto(tag);
    }
    
    @Transactional(readOnly = true)
    public Optional<TagDto> getTagById(Long id) {
        return tagRepository.findById(id).map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Optional<TagDto> getTagByName(String name) {
        return tagRepository.findByName(name).map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TagDto> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(this::convertToDto);
    }
    
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
    
    private TagDto convertToDto(Tag tag) {
        TagDto dto = new TagDto(tag.getName(), tag.getDescription());
        dto.setId(tag.getId());
        dto.setCreatedAt(tag.getCreatedAt());
        return dto;
    }
}
