package com.nexus.datapulse.application.service.ingestion.orchestration;

import com.nexus.datapulse.domain.measurement.Measurement;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMeasurementBuffer {

    private final Map<MeasurementWindowKey, List<Measurement>> buffer = new ConcurrentHashMap<>();

    public void add(MeasurementWindowKey key, Measurement measurement) {
        buffer.computeIfAbsent(key, ignored -> java.util.Collections.synchronizedList(new ArrayList<>())).add(measurement);
    }

    public List<Measurement> getMeasurements(MeasurementWindowKey key) {
        return List.copyOf(buffer.getOrDefault(key, List.of()));
    }

    public Map<MeasurementWindowKey, List<Measurement>> findExpiredWindows(Instant now) {
        Map<MeasurementWindowKey, List<Measurement>> expired = new ConcurrentHashMap<>();

        for (Map.Entry<MeasurementWindowKey, List<Measurement>> entry : buffer.entrySet()) {
            if (!entry.getKey().windowEnd().isAfter(now)) {
                expired.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
        }

        return expired;
    }

    public void remove(MeasurementWindowKey key) {
        buffer.remove(key);
    }

    public int size() {
        return buffer.size();
    }
}
