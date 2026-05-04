package com.nexus.datapulse.domain.measurement;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MeasurementAggregate(UUID id, UUID dataSourceId, MetricDefinition metricDefinition,
                                   AggregationLevel aggregationLevel, Instant windowStart, Instant windowEnd,
                                   int sampleCount, int validSampleCount, int invalidSampleCount, Double minValue,
                                   Double maxValue, Double avgValue, Double lastValue) {

    public MeasurementAggregate(
            UUID id,
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            int sampleCount,
            int validSampleCount,
            int invalidSampleCount,
            Double minValue,
            Double maxValue,
            Double avgValue,
            Double lastValue
    ) {
        this.id = id;
        this.dataSourceId = Objects.requireNonNull(dataSourceId, "dataSourceId must not be null");
        this.metricDefinition = Objects.requireNonNull(metricDefinition, "metricDefinition must not be null");
        this.aggregationLevel = Objects.requireNonNull(aggregationLevel, "aggregationLevel must not be null");
        this.windowStart = Objects.requireNonNull(windowStart, "windowStart must not be null");
        this.windowEnd = Objects.requireNonNull(windowEnd, "windowEnd must not be null");
        this.sampleCount = sampleCount;
        this.validSampleCount = validSampleCount;
        this.invalidSampleCount = invalidSampleCount;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.avgValue = avgValue;
        this.lastValue = lastValue;

        validate();
    }

    private void validate() {
        if (!windowStart.isBefore(windowEnd)) {
            throw new IllegalArgumentException("windowStart must be before windowEnd");
        }

        if (sampleCount < 0 || validSampleCount < 0 || invalidSampleCount < 0) {
            throw new IllegalArgumentException("sample counts must not be negative");
        }

        if (validSampleCount + invalidSampleCount > sampleCount) {
            throw new IllegalArgumentException("valid + invalid sample count cannot exceed total sample count");
        }

        if (validSampleCount == 0) {
            if (minValue != null || maxValue != null || avgValue != null || lastValue != null) {
                throw new IllegalArgumentException("aggregate values must be null when there are no valid samples");
            }
        }

        if (minValue != null && maxValue != null && minValue > maxValue) {
            throw new IllegalArgumentException("minValue cannot be greater than maxValue");
        }

        if (avgValue != null && minValue != null && avgValue < minValue) {
            throw new IllegalArgumentException("avgValue cannot be smaller than minValue");
        }

        if (avgValue != null && maxValue != null && avgValue > maxValue) {
            throw new IllegalArgumentException("avgValue cannot be greater than maxValue");
        }
    }

    public boolean hasValidSamples() {
        return validSampleCount > 0;
    }
}
