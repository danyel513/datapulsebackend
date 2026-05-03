package com.nexus.datapulse.domain.measurement;

import java.util.Objects;
import java.util.UUID;

public record MetricDefinition(UUID id, String metricKey, String displayName, String unit, MetricDataType dataType,
                               Double minValidValue, Double maxValidValue, boolean enabled) {

    public MetricDefinition(
            UUID id,
            String metricKey,
            String displayName,
            String unit,
            MetricDataType dataType,
            Double minValidValue,
            Double maxValidValue,
            boolean enabled
    ) {
        this.id = id;
        this.metricKey = Objects.requireNonNull(metricKey, "metricKey must not be null");
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
        this.unit = Objects.requireNonNull(unit, "unit must not be null");
        this.dataType = Objects.requireNonNull(dataType, "dataType must not be null");
        this.minValidValue = minValidValue;
        this.maxValidValue = maxValidValue;
        this.enabled = enabled;
    }

    public boolean accepts(Double value) {
        if (!enabled || value == null) {
            return false;
        }

        if (minValidValue != null && value < minValidValue) {
            return false;
        }

        if (maxValidValue != null && value > maxValidValue) {
            return false;
        }

        return true;
    }
}
