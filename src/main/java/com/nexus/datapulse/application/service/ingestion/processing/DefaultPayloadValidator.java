package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.NormalizedMetricValue;
import com.nexus.datapulse.domain.ingestion.PayloadValidationResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultPayloadValidator implements PayloadValidator {

    @Override
    public PayloadValidationResult validate(NormalizedIngestionEvent event) {
        List<String> errors = new ArrayList<>();

        if (event.sourceKey() == null || event.sourceKey().isBlank()) {
            errors.add("sourceKey must not be blank");
        }

        if (event.measuredAt() == null) {
            errors.add("measuredAt must not be null");
        } else if (event.measuredAt().isAfter(Instant.now().plusSeconds(5))) {
            errors.add("measuredAt cannot be in the future");
        }

        if (event.metrics() == null || event.metrics().isEmpty()) {
            errors.add("metrics must not be empty");
        } else {
            for (NormalizedMetricValue metric : event.metrics()) {
                validateMetric(metric, errors);
            }
        }

        if (!errors.isEmpty()) {
            return PayloadValidationResult.failure(errors);
        }

        return PayloadValidationResult.success();
    }

    private void validateMetric(NormalizedMetricValue metric, List<String> errors) {
        if (metric.metricKey() == null || metric.metricKey().isBlank()) {
            errors.add("metricKey must not be blank");
        }

        if (metric.value() == null) {
            errors.add("metric value must not be null");
        }

        if (metric.unit() != null && metric.unit().isBlank()) {
            errors.add("unit must not be blank when provided");
        }
    }
}
