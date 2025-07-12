package com.digitaltolk.translation.service;

import com.digitaltolk.translation.entity.Tag;
import com.digitaltolk.translation.entity.Translation;
import com.digitaltolk.translation.entity.User;
import com.digitaltolk.translation.repository.TagRepository;
import com.digitaltolk.translation.repository.TranslationRepository;
import com.digitaltolk.translation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataLoaderService {

    private final TranslationRepository translationRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String[] LOCALES = {"en", "fr", "es"};
    private static final String[] TAG_NAMES = {"mobile", "desktop", "web", "api", "ui", "error", "success", "warning"};
    private static final String[] KEY_PREFIXES = {
            "welcome", "error", "success", "button", "label", "message", "title", "description",
            "navigation", "form", "validation", "notification", "dialog", "menu", "footer", "header"
    };

    @Autowired
    public DataLoaderService(TranslationRepository translationRepository, TagRepository tagRepository,
                             UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.translationRepository = translationRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String load() throws Exception {
            System.out.println("Starting data seeding...");
            seedData();
            System.out.println("Data seeding completed!");
            return "Data loaded";

    }

    private void createDefaultUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin/admin123");
        }
    }

    private void seedData() {
        createDefaultUser();

        // Create tags
        List<Tag> tags = createTags();
        System.out.println("Created " + tags.size() + " tags");

        // Create translations
        int translationCount = createTranslations(tags);
        System.out.println("Created " + translationCount + " translations");
    }

    private List<Tag> createTags() {
        List<Tag> tags = new ArrayList<>();

        for (String tagName : TAG_NAMES) {
            if (!tagRepository.existsByName(tagName)) {
                Tag tag = new Tag(tagName, "Auto-generated tag for " + tagName);
                tags.add(tagRepository.save(tag));
            } else {
                tags.add(tagRepository.findByName(tagName).orElse(null));
            }
        }

        return tags;
    }

    private int createTranslations(List<Tag> tags) {
        List<Translation> translations = new ArrayList<>();
        Random random = ThreadLocalRandom.current();

        // Generate 100k+ translations
        int targetCount = 100000;
        int batchSize = 1000;
        int totalCreated = 0;

        for (int batch = 0; batch < targetCount / batchSize; batch++) {
            translations.clear();

            for (int i = 0; i < batchSize; i++) {
                String keyPrefix = KEY_PREFIXES[random.nextInt(KEY_PREFIXES.length)];
                String key = keyPrefix + "." + (batch * batchSize + i + 1);

                for (String locale : LOCALES) {
                    Translation translation = new Translation(key, locale, generateContent(key, locale));

                    // Add random tags (0-3 tags per translation)
                    int tagCount = random.nextInt(4);
                    Set<Tag> translationTags = new HashSet<>();
                    for (int j = 0; j < tagCount; j++) {
                        translationTags.add(tags.get(random.nextInt(tags.size())));
                    }
                    translation.setTags(translationTags);

                    translations.add(translation);
                }
            }

            translationRepository.saveAll(translations);
            totalCreated += translations.size();

            if (batch % 10 == 0) {
                System.out.println("Created " + totalCreated + " translations so far...");
            }
        }

        return totalCreated;
    }

    private String generateContent(String key, String locale) {
        Map<String, Map<String, String>> contentTemplates = Map.of(
                "en", Map.of(
                        "welcome", "Welcome to our application",
                        "error", "An error occurred",
                        "success", "Operation completed successfully",
                        "button", "Click here",
                        "label", "Field label",
                        "message", "This is a message",
                        "title", "Page title",
                        "description", "This is a description"
                ),
                "fr", Map.of(
                        "welcome", "Bienvenue dans notre application",
                        "error", "Une erreur s'est produite",
                        "success", "Opération terminée avec succès",
                        "button", "Cliquez ici",
                        "label", "Libellé du champ",
                        "message", "Ceci est un message",
                        "title", "Titre de la page",
                        "description", "Ceci est une description"
                ),
                "es", Map.of(
                        "welcome", "Bienvenido a nuestra aplicación",
                        "error", "Ocurrió un error",
                        "success", "Operación completada exitosamente",
                        "button", "Haz clic aquí",
                        "label", "Etiqueta del campo",
                        "message", "Este es un mensaje",
                        "title", "Título de la página",
                        "description", "Esta es una descripción"
                )
        );

        String keyPrefix = key.split("\\.")[0];
        Map<String, String> localeTemplates = contentTemplates.get(locale);

        if (localeTemplates != null && localeTemplates.containsKey(keyPrefix)) {
            return localeTemplates.get(keyPrefix) + " (" + key + ")";
        }

        return "Content for " + key + " in " + locale;
    }
}
