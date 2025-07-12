-- Test data for integration tests
-- Password is 'admin123' encoded with BCrypt
INSERT INTO users (username, email, password, role, created_at) VALUES 
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO tags (name, description, created_at) VALUES 
('mobile', 'Mobile application context', CURRENT_TIMESTAMP),
('desktop', 'Desktop application context', CURRENT_TIMESTAMP),
('web', 'Web application context', CURRENT_TIMESTAMP),
('api', 'API context', CURRENT_TIMESTAMP),
('ui', 'User interface context', CURRENT_TIMESTAMP);

-- Sample translations for testing
INSERT INTO translations (translation_key, locale, content, created_at, updated_at) VALUES 
('welcome.message', 'en', 'Welcome to our application!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('welcome.message', 'fr', 'Bienvenue dans notre application!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('welcome.message', 'es', 'Bienvenido a nuestra aplicación!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('button.submit', 'en', 'Submit', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('button.submit', 'fr', 'Soumettre', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('button.cancel', 'en', 'Cancel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('button.cancel', 'fr', 'Annuler', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('error.notfound', 'en', 'Resource not found', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('error.notfound', 'fr', 'Ressource non trouvée', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Link some translations with tags
INSERT INTO translation_tags (translation_id, tag_id) VALUES 
(1, 1), -- welcome.message (en) -> mobile
(1, 2), -- welcome.message (en) -> desktop
(4, 1), -- button.submit (en) -> mobile
(6, 2); -- button.cancel (en) -> desktop
