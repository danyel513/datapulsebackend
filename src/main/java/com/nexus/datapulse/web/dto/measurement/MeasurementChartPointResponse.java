package com.nexus.datapulse.web.dto.measurement;

import java.time.Instant;

public record MeasurementChartPointResponse(
        Instant timestamp,
        Double value
) {
}
