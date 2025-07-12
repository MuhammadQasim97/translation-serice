package com.digitaltolk.translation.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "translations", indexes = {
    @Index(name = "idx_translation_key", columnList = "translation_key"),
    @Index(name = "idx_locale", columnList = "locale"),
    @Index(name = "idx_key_locale", columnList = "translation_key, locale"),
    @Index(name = "idx_content", columnList = "content")
})
@EntityListeners(AuditingEntityListener.class)
public class Translation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "translation_key", nullable = false, length = 255)
    private String key;
    
    @Column(nullable = false, length = 10)
    private String locale;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
        name = "translation_tags",
        joinColumns = @JoinColumn(name = "translation_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"),
        indexes = {
            @Index(name = "idx_translation_tags_translation", columnList = "translation_id"),
            @Index(name = "idx_translation_tags_tag", columnList = "tag_id")
        }
    )
    private Set<Tag> tags = new HashSet<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Translation() {}
    
    public Translation(String key, String locale, String content) {
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
    
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
