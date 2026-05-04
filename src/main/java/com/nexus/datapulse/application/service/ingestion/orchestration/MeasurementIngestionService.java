package com.nexus.datapulse.application.service.ingestion.orchestration;

import com.nexus.datapulse.application.service.datasource.DataSourceService;
import com.nexus.datapulse.application.service.measurement.MetricDefinitionService;
import com.nexus.datapulse.domain.datasource.DataSource;
import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.NormalizedMetricValue;
import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.Measurement;
import com.nexus.datapulse.domain.measurement.MeasurementQuality;
import com.nexus.datapulse.domain.measurement.MetricDataType;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MeasurementIngestionService {

    private static final AggregationLevel DEFAULT_AGGREGATION_LEVEL = AggregationLevel.ONE_MINUTE;

    private final DataSourceService dataSourceService;
    private final MetricDefinitionService metricDefinitionService;
    private final InMemoryMeasurementBuffer measurementBuffer;

    public void ingest(NormalizedIngestionEvent event) {
        DataSource dataSource = dataSourceService.getBySourceKey(event.sourceKey());

        for (NormalizedMetricValue metricValue : event.metrics()) {
            processMetric(dataSource, event, metricValue);
        }
    }

    private void processMetric(
            DataSource dataSource,
            NormalizedIngestionEvent event,
            NormalizedMetricValue metricValue
    ) {
        MetricDefinition metricDefinition = metricDefinitionService.getOrCreate(
                metricValue.metricKey(),
                buildDisplayName(metricValue.metricKey()),
                resolveUnit(metricValue),
                MetricDataType.DOUBLE,
                null,
                null
        );

        Measurement measurement = buildMeasurement(
                dataSource.getId(),
                metricDefinition,
                metricValue,
                event.measuredAt()
        );

        Instant windowStart = computeWindowStart(event.measuredAt(), DEFAULT_AGGREGATION_LEVEL);
        Instant windowEnd = computeWindowEnd(windowStart, DEFAULT_AGGREGATION_LEVEL);

        MeasurementWindowKey key = new MeasurementWindowKey(
                dataSource.getId(),
                metricDefinition.id(),
                DEFAULT_AGGREGATION_LEVEL,
                windowStart,
                windowEnd
        );

        measurementBuffer.add(key, measurement);
    }

    private Measurement buildMeasurement(
            UUID dataSourceId,
            MetricDefinition metricDefinition,
            NormalizedMetricValue metricValue,
            Instant measuredAt
    ) {
        MeasurementQuality quality = metricDefinition.accepts(metricValue.value())
                ? MeasurementQuality.VALID
                : MeasurementQuality.INVALID;

        return new Measurement(
                dataSourceId,
                metricDefinition,
                metricValue.value(),
                measuredAt,
                quality
        );
    }

    private String resolveUnit(NormalizedMetricValue metricValue) {
        return metricValue.unit() != null ? metricValue.unit() : "unknown";
    }

    private String buildDisplayName(String metricKey) {
        if (metricKey == null || metricKey.isBlank()) {
            return "Unknown Metric";
        }

        String normalized = metricKey.replace("_", " ").trim();
        String[] parts = normalized.split("\\s+");
        List<String> capitalized = new ArrayList<>();

        for (String part : parts) {
            if (!part.isBlank()) {
                capitalized.add(part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase());
            }
        }

        return String.join(" ", capitalized);
    }

    private Instant computeWindowStart(Instant measuredAt, AggregationLevel aggregationLevel) {
        long epochSeconds = measuredAt.getEpochSecond();

        return switch (aggregationLevel) {
            case ONE_MINUTE -> Instant.ofEpochSecond((epochSeconds / 60) * 60);
            case TEN_MINUTES -> Instant.ofEpochSecond((epochSeconds / 600) * 600);
            case THIRTY_MINUTES -> Instant.ofEpochSecond((epochSeconds / 1800) * 1800);
            case ONE_HOUR -> Instant.ofEpochSecond((epochSeconds / 3600) * 3600);
            case SIX_HOURS -> Instant.ofEpochSecond((epochSeconds / 21600) * 21600);
            case ONE_DAY -> Instant.ofEpochSecond((epochSeconds / 86400) * 86400);
        };
    }

    private Instant computeWindowEnd(Instant windowStart, AggregationLevel aggregationLevel) {
        return switch (aggregationLevel) {
            case ONE_MINUTE -> windowStart.plusSeconds(60);
            case TEN_MINUTES -> windowStart.plusSeconds(600);
            case THIRTY_MINUTES -> windowStart.plusSeconds(1800);
            case ONE_HOUR -> windowStart.plusSeconds(3600);
            case SIX_HOURS -> windowStart.plusSeconds(21600);
            case ONE_DAY -> windowStart.plusSeconds(86400);
        };
    }
}