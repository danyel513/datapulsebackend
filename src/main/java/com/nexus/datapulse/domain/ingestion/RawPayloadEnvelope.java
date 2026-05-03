package com.nexus.datapulse.domain.ingestion;

import java.time.Instant;
import java.util.Objects;

public record RawPayloadEnvelope(
        String payload,
        Instant receivedAt
) {
    public RawPayloadEnvelope {
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(receivedAt, "receivedAt must not be null");

        if (payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
    }
}
