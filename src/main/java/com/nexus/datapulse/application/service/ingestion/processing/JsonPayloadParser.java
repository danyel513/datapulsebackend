package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.application.service.ingestion.dto.IncomingIngestionPayload;
import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.NormalizedMetricValue;
import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JsonPayloadParser implements PayloadParser {

    private final ObjectMapper objectMapper;

    @Override
    public NormalizedIngestionEvent parse(RawPayloadEnvelope envelope) {
        try {
            IncomingIngestionPayload incomingPayload =
                    objectMapper.readValue(envelope.payload(), IncomingIngestionPayload.class);

            return new NormalizedIngestionEvent(
                    incomingPayload.sourceKey(),
                    incomingPayload.measuredAt(),
                    envelope.receivedAt(),
                    incomingPayload.metrics().stream()
                            .map(metric -> new NormalizedMetricValue(
                                    metric.name(),
                                    metric.value(),
                                    metric.unit()
                            ))
                            .toList()
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse payload: " + ex.getMessage(), ex);
        }
    }
}