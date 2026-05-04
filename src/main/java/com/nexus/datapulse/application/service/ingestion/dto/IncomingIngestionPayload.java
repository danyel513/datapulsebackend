package com.nexus.datapulse.application.service.ingestion.dto;
import java.time.Instant;
import java.util.List;

public record IncomingIngestionPayload(
        String sourceKey,
        Instant measuredAt,
        List<IncomingMetricValue> metrics
) {
}
