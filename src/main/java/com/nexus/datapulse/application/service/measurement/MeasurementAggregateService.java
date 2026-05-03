package com.nexus.datapulse.application.service.measurement;

import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.infrastructure.persistence.entity.DataSourceEntity;
import com.nexus.datapulse.infrastructure.persistence.entity.MeasurementAggregateEntity;
import com.nexus.datapulse.infrastructure.persistence.entity.MetricDefinitionEntity;
import com.nexus.datapulse.infrastructure.persistence.mapper.MeasurementAggregateMapper;
import com.nexus.datapulse.infrastructure.persistence.repository.DataSourceRepository;
import com.nexus.datapulse.infrastructure.persistence.repository.MeasurementAggregateRepository;
import com.nexus.datapulse.infrastructure.persistence.repository.MetricDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MeasurementAggregateService {

    private final MeasurementAggregateRepository measurementAggregateRepository;
    private final DataSourceRepository dataSourceRepository;
    private final MetricDefinitionRepository metricDefinitionRepository;

    public MeasurementAggregate save(MeasurementAggregate model) {
        DataSourceEntity dataSourceEntity = dataSourceRepository.findById(model.dataSourceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Data source not found: " + model.dataSourceId()
                ));

        MetricDefinitionEntity metricDefinitionEntity = metricDefinitionRepository.findById(model.metricDefinition().id())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metric definition not found: " + model.metricDefinition().id()
                ));

        MeasurementAggregateEntity entity = MeasurementAggregateMapper.toEntity(
                model,
                dataSourceEntity,
                metricDefinitionEntity
        );

        MeasurementAggregateEntity savedEntity = measurementAggregateRepository.save(entity);
        return MeasurementAggregateMapper.toModel(savedEntity);
    }

    public MeasurementAggregate update(MeasurementAggregate model) {
        MeasurementAggregateEntity existingEntity = measurementAggregateRepository.findById(model.id())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Measurement aggregate not found: " + model.id()
                ));

        DataSourceEntity dataSourceEntity = dataSourceRepository.findById(model.dataSourceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Data source not found: " + model.dataSourceId()
                ));

        MetricDefinitionEntity metricDefinitionEntity = metricDefinitionRepository.findById(model.metricDefinition().id())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metric definition not found: " + model.metricDefinition().id()
                ));

        MeasurementAggregateMapper.updateEntity(
                model,
                existingEntity,
                dataSourceEntity,
                metricDefinitionEntity
        );

        MeasurementAggregateEntity savedEntity = measurementAggregateRepository.save(existingEntity);
        return MeasurementAggregateMapper.toModel(savedEntity);
    }

    public Optional<MeasurementAggregate> findById(UUID id) {
        return measurementAggregateRepository.findById(id)
                .map(MeasurementAggregateMapper::toModel);
    }

    public List<MeasurementAggregate> findByDataSourceId(UUID dataSourceId) {
        return measurementAggregateRepository.findAllByDataSourceId(dataSourceId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public List<MeasurementAggregate> findByMetricDefinitionId(UUID metricDefinitionId) {
        return measurementAggregateRepository.findAllByMetricDefinitionId(metricDefinitionId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public List<MeasurementAggregate> findByDataSourceIdAndMetricDefinitionId(
            UUID dataSourceId,
            UUID metricDefinitionId
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionId(dataSourceId, metricDefinitionId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public List<MeasurementAggregate> findByDataSourceIdAndAggregationLevel(
            UUID dataSourceId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndAggregationLevel(dataSourceId, String.valueOf(aggregationLevel))
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public List<MeasurementAggregate> findByDataSourceIdAndMetricDefinitionIdAndAggregationLevel(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevel(
                        dataSourceId,
                        metricDefinitionId,
                        String.valueOf(aggregationLevel)
                )
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public List<MeasurementAggregate> findByWindow(
            UUID dataSourceId,
            UUID metricDefinitionId,
            Instant windowStart,
            Instant windowEnd
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdAndWindowStartGreaterThanEqualAndWindowEndLessThanEqual(
                        dataSourceId,
                        metricDefinitionId,
                        windowStart,
                        windowEnd
                )
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    public Optional<MeasurementAggregate> findLatest(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findTopByDataSourceIdAndMetricDefinitionIdAndAggregationLevelOrderByWindowEndDesc(
                        dataSourceId,
                        metricDefinitionId,
                        String.valueOf(aggregationLevel)
                )
                .map(MeasurementAggregateMapper::toModel);
    }

    public boolean existsByNaturalKey(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd
    ) {
        return measurementAggregateRepository
                .existsByDataSourceIdAndMetricDefinitionIdAndAggregationLevelAndWindowStartAndWindowEnd(
                        dataSourceId,
                        metricDefinitionId,
                        String.valueOf(aggregationLevel),
                        windowStart,
                        windowEnd
                );
    }
}
