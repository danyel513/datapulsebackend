package com.nexus.datapulse.web.dto.measurement;

import java.time.Instant;
import java.util.UUID;

public record LatestMeasurementSummaryResponse(
        UUID dataSourceId,
        UUID metricDefinitionId,
        String metricKey,
        String displayName,
        String unit,
        String aggregationLevel,
        Instant windowStart,
        Instant windowEnd,
        Double lastValue,
        Double avgValue,
        Double minValue,
        Double maxValue,
        int sampleCount,
        int validSampleCount,
        int invalidSampleCount
) {
}