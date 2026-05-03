package com.nexus.datapulse.application.service.ingestion.processing;

import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;

public interface PayloadParser {
    NormalizedIngestionEvent parse(RawPayloadEnvelope envelope);
}
