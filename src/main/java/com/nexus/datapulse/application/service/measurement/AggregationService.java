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
        if (measurements == null || measurements.isEmpty()) {
            throw new IllegalArgumentException("measurements must not be null or empty");
        }

        List<Measurement> validMeasurements = measurements.stream()
                .filter(Measurement::isValid)
                .toList();

        List<Measurement> invalidMeasurements = measurements.stream()
                .filter(measurement -> measurement.quality() == MeasurementQuality.INVALID)
                .toList();

        int sampleCount = measurements.size();
        int validSampleCount = validMeasurements.size();
        int invalidSampleCount = invalidMeasurements.size();

        Double minValue = validMeasurements.stream()
                .map(Measurement::value)
                .min(Double::compareTo)
                .orElse(null);

        Double maxValue = validMeasurements.stream()
                .map(Measurement::value)
                .max(Double::compareTo)
                .orElse(null);

        Double avgValue = validMeasurements.stream()
                .mapToDouble(Measurement::value)
                .average()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);

        Double lastValue = validMeasurements.stream()
                .max(Comparator.comparing(Measurement::measuredAt))
                .map(Measurement::value)
                .orElse(null);

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
}