package com.rsh.fitness_centre.config;

import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

/**
 * Auditor configuration for application auditing.
 * Note: JPA auditing has been replaced with JDBC, so @EnableJpaAuditing is no longer used.
 * This class provides audit information for manual timestamp handling in JDBC repositories.
 */
@Configuration
public class JpaAuditConfig implements AuditorAware<String> {

  /**
   * Returns the current auditor, which would be used for audit tracking.
   * Currently returns an empty Optional as we don't have user authentication integration.
   */
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.empty();
  }
}
