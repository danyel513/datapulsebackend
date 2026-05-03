package com.nexus.datapulse.application.service.ingestion.reader;

import com.nexus.datapulse.application.service.ingestion.orchestration.MeasurementIngestionService;
import com.nexus.datapulse.application.service.ingestion.processing.PayloadProcessingService;
import com.nexus.datapulse.domain.ingestion.NormalizedIngestionEvent;
import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadWorker implements Runnable {

    private final IngestionQueue ingestionQueue;
    private final PayloadProcessingService payloadProcessingService;
    private final MeasurementIngestionService measurementIngestionService;

    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                RawPayloadEnvelope envelope = ingestionQueue.consume();

                NormalizedIngestionEvent event = payloadProcessingService.process(envelope);

                measurementIngestionService.ingest(event);
            } catch (Exception ex) {
                log.error("Error while processing payload: {}", ex.getMessage(), ex);
            }
        }

        log.info("PayloadWorker stopped");
    }

    public void stop() {
        this.running = false;
    }
}
