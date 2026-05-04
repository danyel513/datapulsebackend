package com.nexus.datapulse.application.service.ingestion.orchestration;

import com.nexus.datapulse.application.service.measurement.MeasurementAggregateService;
import com.nexus.datapulse.application.service.measurement.MeasurementService;
import com.nexus.datapulse.application.service.measurement.MetricDefinitionService;
import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.Measurement;
import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class WindowFlushService {

    private final InMemoryMeasurementBuffer measurementBuffer;
    private final MeasurementService measurementService;
    private final MeasurementAggregateService measurementAggregateService;
    private final MetricDefinitionService metricDefinitionService;

    @Scheduled(fixedRate = 1000)
    public void flushExpiredWindows() {
        Instant now = Instant.now();

        Map<MeasurementWindowKey, List<Measurement>> expiredWindows =
                measurementBuffer.findExpiredWindows(now);

        for (Map.Entry<MeasurementWindowKey, List<Measurement>> entry : expiredWindows.entrySet()) {
            MeasurementWindowKey key = entry.getKey();
            List<Measurement> measurements = entry.getValue();

            try {
                if (measurements.isEmpty()) {
                    measurementBuffer.remove(key);
                    continue;
                }

                MetricDefinition metricDefinition = metricDefinitionService.findById(key.metricDefinitionId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Metric definition not found: " + key.metricDefinitionId()
                        ));

                MeasurementAggregate savedAggregate = measurementService.aggregateAndSave(
                        key.dataSourceId(),
                        metricDefinition.metricKey(),
                        metricDefinition.displayName(),
                        metricDefinition.unit(),
                        metricDefinition.dataType(),
                        metricDefinition.minValidValue(),
                        metricDefinition.maxValidValue(),
                        key.aggregationLevel(),
                        key.windowStart(),
                        key.windowEnd(),
                        measurements
                );

                measurementBuffer.remove(key);

                if (key.aggregationLevel() == AggregationLevel.ONE_MINUTE) {
                    tryPromoteToTenMinutes(savedAggregate);
                }

            } catch (Exception ex) {
                log.error("Failed to flush window {}: {}", key, ex.getMessage(), ex);
                measurementBuffer.remove(key);
            }
        }
    }

    private void tryPromoteToTenMinutes(MeasurementAggregate oneMinuteAggregate) {
        Instant tenMinuteWindowEnd = oneMinuteAggregate.windowEnd();

        if (tenMinuteWindowEnd.getEpochSecond() % 600 != 0) {
            return;
        }

        Instant tenMinuteWindowStart = tenMinuteWindowEnd.minusSeconds(600);

        List<MeasurementAggregate> sourceAggregates =
                measurementAggregateService.findByWindowAndAggregationLevel(
                        oneMinuteAggregate.dataSourceId(),
                        oneMinuteAggregate.metricDefinition().id(),
                        AggregationLevel.ONE_MINUTE,
                        tenMinuteWindowStart,
                        tenMinuteWindowEnd
                );

        if (sourceAggregates.size() != 10) {
            log.debug(
                    "Skipping TEN_MINUTES aggregation for dataSourceId={}, metricDefinitionId={}, window=[{}, {}) because only {} ONE_MINUTE aggregates exist",
                    oneMinuteAggregate.dataSourceId(),
                    oneMinuteAggregate.metricDefinition().id(),
                    tenMinuteWindowStart,
                    tenMinuteWindowEnd,
                    sourceAggregates.size()
            );
            return;
        }

        measurementService.aggregateFromAggregatesAndSave(
                oneMinuteAggregate.dataSourceId(),
                oneMinuteAggregate.metricDefinition().metricKey(),
                oneMinuteAggregate.metricDefinition().displayName(),
                oneMinuteAggregate.metricDefinition().unit(),
                oneMinuteAggregate.metricDefinition().dataType(),
                oneMinuteAggregate.metricDefinition().minValidValue(),
                oneMinuteAggregate.metricDefinition().maxValidValue(),
                AggregationLevel.TEN_MINUTES,
                tenMinuteWindowStart,
                tenMinuteWindowEnd,
                sourceAggregates
        );

        log.info(
                "Created TEN_MINUTES aggregate for dataSourceId={}, metricDefinitionId={}, window=[{}, {})",
                oneMinuteAggregate.dataSourceId(),
                oneMinuteAggregate.metricDefinition().id(),
                tenMinuteWindowStart,
                tenMinuteWindowEnd
        );
    }
}