package com.nexus.datapulse.domain.ingestion;

import java.util.List;
import java.util.Objects;

public record PayloadValidationResult(
        boolean valid,
        List<String> errors
) {
    public PayloadValidationResult {
        Objects.requireNonNull(errors, "errors must not be null");
        errors = List.copyOf(errors);

        if (valid && !errors.isEmpty()) {
            throw new IllegalArgumentException("A valid result cannot contain errors");
        }
    }

    public static PayloadValidationResult success() {
        return new PayloadValidationResult(true, List.of());
    }

    public static PayloadValidationResult failure(List<String> errors) {
        return new PayloadValidationResult(false, errors);
    }

    public static PayloadValidationResult failure(String error) {
        return new PayloadValidationResult(false, List.of(error));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}