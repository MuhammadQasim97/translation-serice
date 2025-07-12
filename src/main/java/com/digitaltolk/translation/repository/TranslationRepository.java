package com.digitaltolk.translation.repository;

import com.digitaltolk.translation.entity.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    
    Optional<Translation> findByKeyAndLocale(String key, String locale);
    
    List<Translation> findByLocale(String locale);
    
    @Query("SELECT t FROM Translation t WHERE t.locale = :locale ORDER BY t.key")
    List<Translation> findByLocaleOrderByKey(@Param("locale") String locale);
    
    @Query("SELECT t FROM Translation t WHERE t.key LIKE %:key% OR t.content LIKE %:content%")
    Page<Translation> searchByKeyOrContent(@Param("key") String key, @Param("content") String content, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM Translation t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<Translation> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM Translation t JOIN t.tags tag WHERE " +
           "(:key IS NULL OR t.key LIKE %:key%) AND " +
           "(:content IS NULL OR t.content LIKE %:content%) AND " +
           "(:locale IS NULL OR t.locale = :locale) AND " +
           "(:tagName IS NULL OR tag.name = :tagName)")
    Page<Translation> searchTranslations(
        @Param("key") String key,
        @Param("content") String content,
        @Param("locale") String locale,
        @Param("tagName") String tagName,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(t) FROM Translation t")
    long countAll();
    
    @Query("SELECT DISTINCT t.locale FROM Translation t ORDER BY t.locale")
    List<String> findDistinctLocales();
}
