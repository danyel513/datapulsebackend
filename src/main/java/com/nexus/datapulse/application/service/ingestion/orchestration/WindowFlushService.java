package com.nexus.datapulse.application.service.ingestion.orchestration;

import com.nexus.datapulse.application.service.measurement.MeasurementService;
import com.nexus.datapulse.application.service.measurement.MetricDefinitionService;
import com.nexus.datapulse.domain.measurement.Measurement;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class WindowFlushService {

    private final InMemoryMeasurementBuffer measurementBuffer;
    private final MeasurementService measurementService;
    private final MetricDefinitionService metricDefinitionService;

    @Scheduled(fixedRate = 1000)
    public void flushExpiredWindows() {
        Instant now = Instant.now();

        Map<MeasurementWindowKey, List<Measurement>> expiredWindows =
                measurementBuffer.findExpiredWindows(now);

        for (Map.Entry<MeasurementWindowKey, List<Measurement>> entry : expiredWindows.entrySet()) {
            MeasurementWindowKey key = entry.getKey();
            List<Measurement> measurements = entry.getValue();

            if (measurements.isEmpty()) {
                measurementBuffer.remove(key);
                continue;
            }

            MetricDefinition metricDefinition = metricDefinitionService.findById(key.metricDefinitionId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Metric definition not found: " + key.metricDefinitionId()
                    ));

            measurementService.aggregateAndSave(
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
        }
    }
}
