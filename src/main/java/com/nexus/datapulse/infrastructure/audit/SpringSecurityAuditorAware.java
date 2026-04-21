package com.nexus.datapulse.infrastructure.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("system");
    }

    /*
    TODO: implement spring security and use the code below for user's email

    private static final String SYSTEM_AUDITOR = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(SYSTEM_AUDITOR);
        }

        String auditor = authentication.getName();

        if (auditor == null || auditor.isBlank()) {
            return Optional.of(SYSTEM_AUDITOR);
        }

        return Optional.of(auditor);
    }
     */
}