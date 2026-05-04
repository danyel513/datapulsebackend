package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.PayloadValidationResult;

public interface PayloadValidator {
    PayloadValidationResult validate(NormalizedIngestionEvent event);
}
