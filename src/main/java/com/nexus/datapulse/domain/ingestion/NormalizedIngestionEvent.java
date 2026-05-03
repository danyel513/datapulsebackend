package com.nexus.datapulse.domain.ingestion;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record NormalizedIngestionEvent(
        String sourceKey,
        Instant measuredAt,
        Instant receivedAt,
        List<NormalizedMetricValue> metrics
) {
    public NormalizedIngestionEvent {
        Objects.requireNonNull(sourceKey, "sourceKey must not be null");
        Objects.requireNonNull(measuredAt, "measuredAt must not be null");
        Objects.requireNonNull(receivedAt, "receivedAt must not be null");
        Objects.requireNonNull(metrics, "metrics must not be null");

        if (sourceKey.isBlank()) {
            throw new IllegalArgumentException("sourceKey must not be blank");
        }

        if (metrics.isEmpty()) {
            throw new IllegalArgumentException("metrics must not be empty");
        }

        metrics = List.copyOf(metrics);
    }

    public int metricCount() {
        return metrics.size();
    }

    public boolean hasMetric(String metricKey) {
        return metrics.stream().anyMatch(metric -> metric.metricKey().equals(metricKey));
    }
}
