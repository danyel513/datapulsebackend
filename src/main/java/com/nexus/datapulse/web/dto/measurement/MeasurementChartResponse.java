package com.nexus.datapulse.web.dto.measurement;

import java.util.List;
import java.util.UUID;

public record MeasurementChartResponse(
        UUID dataSourceId,
        UUID metricDefinitionId,
        String metricKey,
        String displayName,
        String unit,
        String aggregationLevel,
        List<MeasurementChartPointResponse> points
) {
}
