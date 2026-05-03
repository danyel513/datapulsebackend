package com.nexus.datapulse.application.service.ingestion.reader;

import com.nexus.datapulse.domain.ingestion.RawPayloadEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class SocketReader implements Runnable {

    @Value("${datapulse.ingestion.socket.host}")
    private String host;

    @Value("${datapulse.ingestion.socket.port}")
    private int port;

    @Value("${datapulse.ingestion.socket.reconnect-delay-ms}")
    private long reconnectDelayMs;

    private final IngestionQueue ingestionQueue;

    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            try (Socket socket = new Socket(host, port);
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                 )) {

                log.info("Connected to ingestion socket at {}:{}", host, port);

                String line;
                while (running && (line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    RawPayloadEnvelope envelope = new RawPayloadEnvelope(
                            line,
                            Instant.now()
                    );

                    ingestionQueue.publish(envelope);
                }

                log.warn("Socket stream ended, reconnecting...");
            } catch (Exception ex) {
                log.error("Error while reading from socket: {}", ex.getMessage(), ex);

                try {
                    Thread.sleep(reconnectDelayMs);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    log.warn("SocketReader interrupted during reconnect delay");
                    return;
                }
            }
        }

        log.info("SocketReader stopped");
    }

    public void stop() {
        this.running = false;
    }
}