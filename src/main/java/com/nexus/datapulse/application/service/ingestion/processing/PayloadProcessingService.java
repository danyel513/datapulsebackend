package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.PayloadValidationResult;
import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import org.springframework.stereotype.Service;

@Service
public class PayloadProcessingService {

    private final PayloadParser payloadParser;
    private final PayloadValidator payloadValidator;

    public PayloadProcessingService(
            PayloadParser payloadParser,
            PayloadValidator payloadValidator
    ) {
        this.payloadParser = payloadParser;
        this.payloadValidator = payloadValidator;
    }

    public NormalizedIngestionEvent process(RawPayloadEnvelope envelope) {
        NormalizedIngestionEvent event = payloadParser.parse(envelope);

        PayloadValidationResult validationResult = payloadValidator.validate(event);
        if (!validationResult.valid()) {
            throw new IllegalArgumentException(
                    "Invalid payload: " + String.join(", ", validationResult.errors())
            );
        }

        return event;
    }
}