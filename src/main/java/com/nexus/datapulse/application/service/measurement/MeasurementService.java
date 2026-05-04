package com.nexus.datapulse.application.service.measurement;

import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.Measurement;
import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.domain.measurement.MetricDataType;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MeasurementService {

    private final AggregationService aggregationService;
    private final MetricDefinitionService metricDefinitionService;
    private final MeasurementAggregateService measurementAggregateService;

    public MeasurementAggregate aggregateAndSave(
            UUID dataSourceId,
            String metricKey,
            String displayName,
            String unit,
            MetricDataType dataType,
            Double minValidValue,
            Double maxValidValue,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<Measurement> measurements
    ) {
        MetricDefinition metricDefinition = metricDefinitionService.getOrCreate(
                metricKey,
                displayName,
                unit,
                dataType,
                minValidValue,
                maxValidValue
        );

        MeasurementAggregate aggregate = aggregationService.aggregate(
                dataSourceId,
                metricDefinition,
                aggregationLevel,
                windowStart,
                windowEnd,
                measurements
        );

        return measurementAggregateService.save(aggregate);
    }

    public List<MeasurementAggregate> getAggregatesForDataSource(UUID dataSourceId) {
        return measurementAggregateService.findByDataSourceId(dataSourceId);
    }

    public List<MeasurementAggregate> getAggregatesForMetric(
            UUID dataSourceId,
            UUID metricDefinitionId
    ) {
        return measurementAggregateService.findByDataSourceIdAndMetricDefinitionId(
                dataSourceId,
                metricDefinitionId
        );
    }

    public List<MeasurementAggregate> getAggregatesForChart(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel,
            Instant from,
            Instant to
    ) {
        return measurementAggregateService
                .findByDataSourceIdAndMetricDefinitionIdAndAggregationLevel(
                        dataSourceId,
                        metricDefinitionId,
                        aggregationLevel
                )
                .stream()
                .filter(aggregate ->
                        !aggregate.windowEnd().isBefore(from) &&
                                !aggregate.windowStart().isAfter(to)
                )
                .toList();
    }

    public MeasurementAggregate aggregateFromAggregatesAndSave(
            UUID dataSourceId,
            String metricKey,
            String displayName,
            String unit,
            MetricDataType dataType,
            Double minValidValue,
            Double maxValidValue,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd,
            List<MeasurementAggregate> aggregates
    ) {
        MetricDefinition metricDefinition = metricDefinitionService.getOrCreate(
                metricKey,
                displayName,
                unit,
                dataType,
                minValidValue,
                maxValidValue
        );

        if (measurementAggregateService.existsByNaturalKey(
                dataSourceId,
                metricDefinition.id(),
                aggregationLevel,
                windowStart,
                windowEnd
        )) {
            return measurementAggregateService.findLatest(
                            dataSourceId,
                            metricDefinition.id(),
                            aggregationLevel
                    )
                    .filter(existing ->
                            existing.windowStart().equals(windowStart) &&
                                    existing.windowEnd().equals(windowEnd)
                    )
                    .orElseThrow(() -> new IllegalStateException(
                            "Aggregate already exists, but could not be reloaded"
                    ));
        }

        MeasurementAggregate aggregate = aggregationService.aggregateFromAggregates(
                dataSourceId,
                metricDefinition,
                aggregationLevel,
                windowStart,
                windowEnd,
                aggregates
        );

        return measurementAggregateService.save(aggregate);
    }
}