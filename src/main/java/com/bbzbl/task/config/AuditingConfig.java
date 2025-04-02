package com.bbzbl.task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA auditing.
 * JPA auditing allows automatic population of audit-related fields such as
 * createdDate and lastModifiedDate in entities.
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
    // No implementation needed — annotation-based configuration only.
}
