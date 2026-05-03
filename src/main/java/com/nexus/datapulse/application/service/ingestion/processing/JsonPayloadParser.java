package com.nexus.datapulse.application.service.ingestion.processing;

import tools.jackson.databind.ObjectMapper;
import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JsonPayloadParser implements PayloadParser {

    private final ObjectMapper objectMapper;

    @Override
    public NormalizedIngestionEvent parse(RawPayloadEnvelope envelope) {
        try {
            NormalizedIngestionEvent event =
                    objectMapper.readValue(envelope.payload(), NormalizedIngestionEvent.class);

            return new NormalizedIngestionEvent(
                    event.sourceKey(),
                    event.measuredAt(),
                    envelope.receivedAt(),
                    event.metrics()
            );
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse payload: " + ex.getMessage(), ex);
        }
    }
}
