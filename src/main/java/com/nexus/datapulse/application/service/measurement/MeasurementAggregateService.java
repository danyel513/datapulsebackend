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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
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

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByWindowAndAggregationLevel(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevelAndWindowStartGreaterThanEqualAndWindowEndLessThanEqual(
                        dataSourceId,
                        metricDefinitionId,
                        aggregationLevel,
                        windowStart,
                        windowEnd
                )
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
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

    @Transactional(readOnly = true)
    public Optional<MeasurementAggregate> findById(UUID id) {
        return measurementAggregateRepository.findByIdWithRelations(id)
                .map(MeasurementAggregateMapper::toModel);
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByDataSourceId(UUID dataSourceId) {
        return measurementAggregateRepository.findAllByDataSourceIdWithRelations(dataSourceId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByMetricDefinitionId(UUID metricDefinitionId) {
        return measurementAggregateRepository.findAllByMetricDefinitionIdWithMetric(metricDefinitionId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByDataSourceIdAndMetricDefinitionId(
            UUID dataSourceId,
            UUID metricDefinitionId
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdWithRelations(dataSourceId, metricDefinitionId)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByDataSourceIdAndAggregationLevel(
            UUID dataSourceId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndAggregationLevelWithRelations(dataSourceId, aggregationLevel)
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByDataSourceIdAndMetricDefinitionIdAndAggregationLevel(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevelWithRelations(
                        dataSourceId,
                        metricDefinitionId,
                        aggregationLevel
                )
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementAggregate> findByWindow(
            UUID dataSourceId,
            UUID metricDefinitionId,
            Instant windowStart,
            Instant windowEnd
    ) {
        return measurementAggregateRepository
                .findAllByDataSourceIdAndMetricDefinitionIdAndWindowStartGreaterThanEqualAndWindowEndLessThanEqualWithRelations(
                        dataSourceId,
                        metricDefinitionId,
                        windowStart,
                        windowEnd
                )
                .stream()
                .map(MeasurementAggregateMapper::toModel)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<MeasurementAggregate> findLatest(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    ) {
        return measurementAggregateRepository
                .findTopByDataSourceIdAndMetricDefinitionIdAndAggregationLevelOrderByWindowEndDesc(
                        dataSourceId,
                        metricDefinitionId,
                        aggregationLevel
                )
                .map(MeasurementAggregateMapper::toModel);
    }

    @Transactional(readOnly = true)
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
                        aggregationLevel,
                        windowStart,
                        windowEnd
                );
    }
}