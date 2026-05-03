package com.nexus.datapulse.application.service.ingestion.reader;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class IngestionPipelineRunner {

    private final SocketReader socketReader;
    private final PayloadWorker payloadWorker;

    private Thread socketReaderThread;
    private Thread payloadWorkerThread;

    @PostConstruct
    public void start() {
        socketReaderThread = new Thread(socketReader, "socket-reader-thread");
        payloadWorkerThread = new Thread(payloadWorker, "payload-worker-thread");

        socketReaderThread.start();
        payloadWorkerThread.start();

        log.info("Ingestion pipeline started");
    }

    @PreDestroy
    public void stop() {
        socketReader.stop();
        payloadWorker.stop();

        if (socketReaderThread != null) {
            socketReaderThread.interrupt();
        }

        if (payloadWorkerThread != null) {
            payloadWorkerThread.interrupt();
        }

        log.info("Ingestion pipeline stopped");
    }
}
