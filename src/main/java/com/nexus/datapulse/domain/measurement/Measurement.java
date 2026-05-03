package com.nexus.datapulse.domain.measurement;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


public record Measurement(UUID dataSourceId, MetricDefinition metricDefinition, Double value, Instant measuredAt,
                          MeasurementQuality quality) {
    public Measurement(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            Double value,
            Instant measuredAt,
            MeasurementQuality quality
    ) {
        this.dataSourceId = Objects.requireNonNull(dataSourceId, "dataSourceId must not be null");
        this.metricDefinition = Objects.requireNonNull(metricDefinition, "metricDefinition must not be null");
        this.value = value;
        this.measuredAt = Objects.requireNonNull(measuredAt, "measuredAt must not be null");
        this.quality = Objects.requireNonNull(quality, "quality must not be null");
    }

    public boolean isValid() {
        return quality == MeasurementQuality.VALID;
    }
}
