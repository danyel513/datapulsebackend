package com.nexus.datapulse.domain.ingestion;

import java.util.Objects;

public record NormalizedMetricValue(
        String metricKey,
        Double value,
        String unit
) {
    public NormalizedMetricValue {
        Objects.requireNonNull(metricKey, "metricKey must not be null");
        Objects.requireNonNull(value, "value must not be null");

        if (metricKey.isBlank()) {
            throw new IllegalArgumentException("metricKey must not be blank");
        }

        if (unit != null && unit.isBlank()) {
            throw new IllegalArgumentException("unit must not be blank when provided");
        }
    }

    public boolean hasUnit() {
        return unit != null;
    }
}
