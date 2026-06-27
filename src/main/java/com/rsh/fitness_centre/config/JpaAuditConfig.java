package com.rsh.fitness_centre.config;

import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig implements AuditorAware<String> {

  /**
   * Returns the current auditor, which is used for @CreatedBy and @LastModifiedBy annotations.
   * Currently returns an empty Optional as we don't have user authentication.
   */
  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.empty();
  }
}
