package com.digitaltolk.translation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@TestConfiguration
@EnableJpaRepositories(basePackages = "com.digitaltolk.translation.repository")
@EntityScan(basePackages = "com.digitaltolk.translation.entity")
@ComponentScan(basePackages = "com.digitaltolk.translation")
public class TestJpaConfig {
}
