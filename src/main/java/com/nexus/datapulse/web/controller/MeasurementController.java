package com.nexus.datapulse.web.controller;

import com.nexus.datapulse.application.service.measurement.MeasurementAggregateService;
import com.nexus.datapulse.application.service.measurement.MeasurementService;
import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.web.dto.measurement.LatestMeasurementSummaryResponse;
import com.nexus.datapulse.web.dto.measurement.MeasurementAggregateResponse;
import com.nexus.datapulse.web.dto.measurement.MeasurementChartResponse;
import com.nexus.datapulse.web.mapper.MeasurementWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final MeasurementAggregateService measurementAggregateService;

    @GetMapping("/data-source/{dataSourceId}")
    public List<MeasurementAggregateResponse> getByDataSourceId(@PathVariable UUID dataSourceId) {
        return measurementService.getAggregatesForDataSource(dataSourceId)
                .stream()
                .map(MeasurementWebMapper::toAggregateResponse)
                .toList();
    }

    @GetMapping("/latest")
    public LatestMeasurementSummaryResponse getLatest(
            @RequestParam UUID dataSourceId,
            @RequestParam UUID metricDefinitionId,
            @RequestParam AggregationLevel aggregationLevel
    ) {
        MeasurementAggregate aggregate = measurementAggregateService.findLatest(
                        dataSourceId,
                        metricDefinitionId,
                        aggregationLevel
                )
                .orElseThrow(() -> new IllegalArgumentException("No aggregate found for given filters"));

        return MeasurementWebMapper.toLatestSummaryResponse(aggregate);
    }

    @GetMapping("/chart")
    public MeasurementChartResponse getChart(
            @RequestParam UUID dataSourceId,
            @RequestParam UUID metricDefinitionId,
            @RequestParam AggregationLevel aggregationLevel,
            @RequestParam Instant from,
            @RequestParam Instant to
    ) {
        List<MeasurementAggregate> aggregates = measurementService.getAggregatesForChart(
                dataSourceId,
                metricDefinitionId,
                aggregationLevel,
                from,
                to
        );

        if (aggregates.isEmpty()) {
            throw new IllegalArgumentException("No aggregates found for given filters");
        }

        MeasurementAggregate first = aggregates.stream()
                .min(Comparator.comparing(MeasurementAggregate::windowStart))
                .orElseThrow();

        return MeasurementWebMapper.toChartResponse(
                dataSourceId,
                metricDefinitionId,
                first.metricDefinition().metricKey(),
                first.metricDefinition().displayName(),
                first.metricDefinition().unit(),
                aggregationLevel.name(),
                aggregates
        );
    }
}
