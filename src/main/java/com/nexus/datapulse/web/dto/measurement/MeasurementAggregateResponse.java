package com.nexus.datapulse.web.dto.measurement;

import java.time.Instant;
import java.util.UUID;

public record MeasurementAggregateResponse(
        UUID id,
        UUID dataSourceId,
        UUID metricDefinitionId,
        String metricKey,
        String displayName,
        String unit,
        String aggregationLevel,
        Instant windowStart,
        Instant windowEnd,
        int sampleCount,
        int validSampleCount,
        int invalidSampleCount,
        Double minValue,
        Double maxValue,
        Double avgValue,
        Double lastValue) {}
