package com.nexus.datapulse.application.service.ingestion.orchestration;

import com.nexus.datapulse.domain.measurement.AggregationLevel;

import java.time.Instant;
import java.util.UUID;

public record MeasurementWindowKey(
        UUID dataSourceId,
        UUID metricDefinitionId,
        AggregationLevel aggregationLevel,
        Instant windowStart,
        Instant windowEnd
) {
}
