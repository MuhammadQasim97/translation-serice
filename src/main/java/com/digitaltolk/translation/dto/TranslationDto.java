package com.digitaltolk.translation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Translation Data Transfer Object")
public class TranslationDto {
    
    @Schema(description = "Translation ID", example = "1")
    private Long id;
    
    @NotBlank(message = "Translation key is required")
    @Size(max = 255, message = "Translation key must not exceed 255 characters")
    @Schema(description = "Translation key", example = "welcome.message", required = true)
    private String key;
    
    @NotBlank(message = "Locale is required")
    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Schema(description = "Locale code", example = "en", required = true)
    private String locale;
    
    @NotBlank(message = "Content is required")
    @Schema(description = "Translation content", example = "Welcome to our application!", required = true)
    private String content;
    
    @Schema(description = "Associated tags", example = "[\"mobile\", \"desktop\"]")
    private Set<String> tags;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TranslationDto() {}
    
    public TranslationDto(String key, String locale, String content) {
        this.key = key;
        this.locale = locale;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
