package com.nexus.datapulse.infrastructure.persistence.repository;

import com.nexus.datapulse.infrastructure.persistence.entity.MeasurementAggregateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasurementAggregateRepository extends JpaRepository<MeasurementAggregateEntity, UUID> {

    List<MeasurementAggregateEntity> findAllByDataSourceId(UUID dataSourceId);
    List<MeasurementAggregateEntity> findAllByMetricDefinitionId(UUID metricDefinitionId);
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionId(
            UUID dataSourceId,
            UUID metricDefinitionId);
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndAggregationLevel(
            UUID dataSourceId,
            String aggregationLevel);
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevel(
            UUID dataSourceId,
            UUID metricDefinitionId,
            String aggregationLevel);
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionIdAndWindowStartGreaterThanEqualAndWindowEndLessThanEqual(
            UUID dataSourceId,
            UUID metricDefinitionId,
            Instant windowStart,
            Instant windowEnd);
    Optional<MeasurementAggregateEntity> findTopByDataSourceIdAndMetricDefinitionIdAndAggregationLevelOrderByWindowEndDesc(
            UUID dataSourceId,
            UUID metricDefinitionId,
            String aggregationLevel);
    boolean existsByDataSourceIdAndMetricDefinitionIdAndAggregationLevelAndWindowStartAndWindowEnd(
            UUID dataSourceId,
            UUID metricDefinitionId,
            String aggregationLevel,
            Instant windowStart,
            Instant windowEnd);
}
