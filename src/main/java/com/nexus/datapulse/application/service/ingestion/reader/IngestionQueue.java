package com.nexus.datapulse.application.service.ingestion.reader;

import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class IngestionQueue {

    private final BlockingQueue<RawPayloadEnvelope> queue = new LinkedBlockingQueue<>(10_000);

    public void publish(RawPayloadEnvelope envelope) {
        try {
            queue.put(envelope);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing payload to ingestion queue", ex);
        }
    }

    public RawPayloadEnvelope consume() {
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while consuming payload from ingestion queue", ex);
        }
    }

    public int size() {
        return queue.size();
    }
}
