package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.NormalizedMetricValue;
import com.nexus.datapulse.domain.ingestion.PayloadValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NormalizedPayloadValidator implements PayloadValidator {

    @Override
    public PayloadValidationResult validate(NormalizedIngestionEvent event) {
        List<String> errors = new ArrayList<>();

        if (event == null) {
            return PayloadValidationResult.failure("Event must not be null");
        }

        if (event.sourceKey() == null || event.sourceKey().isBlank()) {
            errors.add("sourceKey must not be null or blank");
        }

        if (event.measuredAt() == null) {
            errors.add("measuredAt must not be null");
        }

        if (event.receivedAt() == null) {
            errors.add("receivedAt must not be null");
        }

        if (event.metrics() == null) {
            errors.add("metrics must not be null");
        } else if (event.metrics().isEmpty()) {
            errors.add("metrics must not be empty");
        } else {
            for (int i = 0; i < event.metrics().size(); i++) {
                validateMetric(event.metrics().get(i), i, errors);
            }
        }

        return errors.isEmpty()
                ? PayloadValidationResult.success()
                : PayloadValidationResult.failure(errors);
    }

    private void validateMetric(NormalizedMetricValue metric, int index, List<String> errors) {
        if (metric == null) {
            errors.add("metrics[" + index + "] must not be null");
            return;
        }

        if (metric.metricKey() == null || metric.metricKey().isBlank()) {
            errors.add("metrics[" + index + "].metricKey must not be null or blank");
        }

        if (metric.value() == null) {
            errors.add("metrics[" + index + "].value must not be null");
        }

        if (metric.unit() != null && metric.unit().isBlank()) {
            errors.add("metrics[" + index + "].unit must not be blank when provided");
        }
    }
}