package com.nexus.datapulse.web.mapper;

import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.web.dto.measurement.LatestMeasurementSummaryResponse;
import com.nexus.datapulse.web.dto.measurement.MeasurementAggregateResponse;
import com.nexus.datapulse.web.dto.measurement.MeasurementChartPointResponse;
import com.nexus.datapulse.web.dto.measurement.MeasurementChartResponse;

import java.util.List;
import java.util.UUID;

public final class MeasurementWebMapper {

    private MeasurementWebMapper() {
    }

    public static MeasurementAggregateResponse toAggregateResponse(MeasurementAggregate model) {
        if (model == null) {
            return null;
        }

        return new MeasurementAggregateResponse(
                model.id(),
                model.dataSourceId(),
                model.metricDefinition().id(),
                model.metricDefinition().metricKey(),
                model.metricDefinition().displayName(),
                model.metricDefinition().unit(),
                model.aggregationLevel().name(),
                model.windowStart(),
                model.windowEnd(),
                model.sampleCount(),
                model.validSampleCount(),
                model.invalidSampleCount(),
                model.minValue(),
                model.maxValue(),
                model.avgValue(),
                model.lastValue()
        );
    }

    public static LatestMeasurementSummaryResponse toLatestSummaryResponse(MeasurementAggregate model) {
        if (model == null) {
            return null;
        }

        return new LatestMeasurementSummaryResponse(
                model.dataSourceId(),
                model.metricDefinition().id(),
                model.metricDefinition().metricKey(),
                model.metricDefinition().displayName(),
                model.metricDefinition().unit(),
                model.aggregationLevel().name(),
                model.windowStart(),
                model.windowEnd(),
                model.lastValue(),
                model.avgValue(),
                model.minValue(),
                model.maxValue(),
                model.sampleCount(),
                model.validSampleCount(),
                model.invalidSampleCount()
        );
    }

    public static MeasurementChartResponse toChartResponse(
            UUID dataSourceId,
            UUID metricDefinitionId,
            String metricKey,
            String displayName,
            String unit,
            String aggregationLevel,
            List<MeasurementAggregate> aggregates
    ) {
        List<MeasurementChartPointResponse> points = aggregates.stream()
                .map(aggregate -> new MeasurementChartPointResponse(
                        aggregate.windowEnd(),
                        aggregate.avgValue()
                ))
                .toList();

        return new MeasurementChartResponse(
                dataSourceId,
                metricDefinitionId,
                metricKey,
                displayName,
                unit,
                aggregationLevel,
                points
        );
    }
}
