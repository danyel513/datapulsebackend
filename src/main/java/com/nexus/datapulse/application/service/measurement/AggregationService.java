package com.nexus.datapulse.application.service.measurement;

import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.Measurement;
import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.domain.measurement.MeasurementQuality;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AggregationService {

    public MeasurementAggregate aggregate(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<Measurement> measurements
    ) {
        validateMeasurements(dataSourceId, metricDefinition, aggregationLevel, windowStart, windowEnd, measurements);

        List<Measurement> snapshot = List.copyOf(measurements);

        List<Measurement> validMeasurements = snapshot.stream()
                .filter(Measurement::isValid)
                .filter(m -> m.value() != null)
                .toList();

        int sampleCount = snapshot.size();
        int validSampleCount = validMeasurements.size();
        int invalidSampleCount = (int) snapshot.stream()
                .filter(measurement -> measurement.quality() == MeasurementQuality.INVALID)
                .count();

        Double minValue = null;
        Double maxValue = null;
        Double avgValue = null;
        Double lastValue = null;

        if (!validMeasurements.isEmpty()) {
            double sum = 0.0;
            Instant latestMeasuredAt = null;

            for (Measurement measurement : validMeasurements) {
                Double value = measurement.value();

                if (minValue == null || value < minValue) {
                    minValue = value;
                }

                if (maxValue == null || value > maxValue) {
                    maxValue = value;
                }

                sum += value;

                if (latestMeasuredAt == null || measurement.measuredAt().isAfter(latestMeasuredAt)) {
                    latestMeasuredAt = measurement.measuredAt();
                    lastValue = value;
                }
            }

            avgValue = sum / validMeasurements.size();
        }

        return new MeasurementAggregate(
                null,
                dataSourceId,
                metricDefinition,
                aggregationLevel,
                windowStart,
                windowEnd,
                sampleCount,
                validSampleCount,
                invalidSampleCount,
                minValue,
                maxValue,
                avgValue,
                lastValue
        );
    }

    public MeasurementAggregate aggregateFromAggregates(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<MeasurementAggregate> aggregates
    ) {
        validateAggregates(dataSourceId, metricDefinition, aggregationLevel, windowStart, windowEnd, aggregates);

        List<MeasurementAggregate> snapshot = List.copyOf(aggregates);

        int sampleCount = snapshot.stream()
                .mapToInt(MeasurementAggregate::sampleCount)
                .sum();

        int validSampleCount = snapshot.stream()
                .mapToInt(MeasurementAggregate::validSampleCount)
                .sum();

        int invalidSampleCount = snapshot.stream()
                .mapToInt(MeasurementAggregate::invalidSampleCount)
                .sum();

        Double minValue = snapshot.stream()
                .map(MeasurementAggregate::minValue)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);

        Double maxValue = snapshot.stream()
                .map(MeasurementAggregate::maxValue)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(null);

        Double lastValue = snapshot.stream()
                .filter(a -> a.lastValue() != null)
                .max(Comparator.comparing(MeasurementAggregate::windowEnd))
                .map(MeasurementAggregate::lastValue)
                .orElse(null);

        Double avgValue = null;
        int weightedCount = 0;
        double weightedSum = 0.0;

        for (MeasurementAggregate aggregate : snapshot) {
            if (aggregate.avgValue() != null && aggregate.validSampleCount() > 0) {
                weightedSum += aggregate.avgValue() * aggregate.validSampleCount();
                weightedCount += aggregate.validSampleCount();
            }
        }

        if (weightedCount > 0) {
            avgValue = weightedSum / weightedCount;
        }

        return new MeasurementAggregate(
                null,
                dataSourceId,
                metricDefinition,
                aggregationLevel,
                windowStart,
                windowEnd,
                sampleCount,
                validSampleCount,
                invalidSampleCount,
                minValue,
                maxValue,
                avgValue,
                lastValue
        );
    }

    private static void validateMeasurements(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<Measurement> measurements
    ) {
        if (dataSourceId == null) {
            throw new IllegalArgumentException("dataSourceId must not be null");
        }
        if (metricDefinition == null) {
            throw new IllegalArgumentException("metricDefinition must not be null");
        }
        if (aggregationLevel == null) {
            throw new IllegalArgumentException("aggregationLevel must not be null");
        }
        if (windowStart == null || windowEnd == null) {
            throw new IllegalArgumentException("windowStart and windowEnd must not be null");
        }
        if (!windowStart.isBefore(windowEnd)) {
            throw new IllegalArgumentException("windowStart must be before windowEnd");
        }
        if (measurements == null || measurements.isEmpty()) {
            throw new IllegalArgumentException("measurements must not be null or empty");
        }
    }

    private static void validateAggregates(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<MeasurementAggregate> aggregates
    ) {
        if (dataSourceId == null) {
            throw new IllegalArgumentException("dataSourceId must not be null");
        }
        if (metricDefinition == null) {
            throw new IllegalArgumentException("metricDefinition must not be null");
        }
        if (aggregationLevel == null) {
            throw new IllegalArgumentException("aggregationLevel must not be null");
        }
        if (windowStart == null || windowEnd == null) {
            throw new IllegalArgumentException("windowStart and windowEnd must not be null");
        }
        if (!windowStart.isBefore(windowEnd)) {
            throw new IllegalArgumentException("windowStart must be before windowEnd");
        }
        if (aggregates == null || aggregates.isEmpty()) {
            throw new IllegalArgumentException("aggregates must not be null or empty");
        }
    }
}