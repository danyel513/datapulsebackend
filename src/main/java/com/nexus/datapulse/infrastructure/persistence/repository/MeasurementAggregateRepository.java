package com.nexus.datapulse.infrastructure.persistence.repository;

import com.nexus.datapulse.domain.measurement.AggregationLevel;
import com.nexus.datapulse.infrastructure.persistence.entity.MeasurementAggregateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasurementAggregateRepository extends JpaRepository<MeasurementAggregateEntity, UUID> {

    List<MeasurementAggregateEntity>
    findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevelAndWindowStartGreaterThanEqualAndWindowEndLessThanEqual(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd
    );

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByDataSourceIdWithRelations(UUID dataSourceId);

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        where ma.metricDefinition.id = :metricDefinitionId
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByMetricDefinitionIdWithMetric(UUID metricDefinitionId);

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
          and ma.metricDefinition.id = :metricDefinitionId
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionIdWithRelations(
            UUID dataSourceId,
            UUID metricDefinitionId
    );

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
          and ma.aggregationLevel = :aggregationLevel
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndAggregationLevelWithRelations(
            UUID dataSourceId,
            AggregationLevel aggregationLevel
    );

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
          and ma.metricDefinition.id = :metricDefinitionId
          and ma.aggregationLevel = :aggregationLevel
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionIdAndAggregationLevelWithRelations(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    );

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
          and ma.metricDefinition.id = :metricDefinitionId
          and ma.windowStart >= :windowStart
          and ma.windowEnd <= :windowEnd
        order by ma.windowStart desc
    """)
    List<MeasurementAggregateEntity> findAllByDataSourceIdAndMetricDefinitionIdAndWindowStartGreaterThanEqualAndWindowEndLessThanEqualWithRelations(
            UUID dataSourceId,
            UUID metricDefinitionId,
            Instant windowStart,
            Instant windowEnd
    );

    @Query("""
        select ma
        from MeasurementAggregateEntity ma
        join fetch ma.metricDefinition
        join fetch ma.dataSource
        where ma.dataSource.id = :dataSourceId
          and ma.metricDefinition.id = :metricDefinitionId
          and ma.aggregationLevel = :aggregationLevel
        order by ma.windowEnd desc
    """)
    List<MeasurementAggregateEntity> findLatestCandidatesWithRelations(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    );

    @Query("""
    select ma
    from MeasurementAggregateEntity ma
    join fetch ma.metricDefinition
    join fetch ma.dataSource
    where ma.id = :id
""")
    Optional<MeasurementAggregateEntity> findByIdWithRelations(UUID id);

    default Optional<MeasurementAggregateEntity> findTopByDataSourceIdAndMetricDefinitionIdAndAggregationLevelOrderByWindowEndDesc(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel
    ) {
        return findLatestCandidatesWithRelations(dataSourceId, metricDefinitionId, aggregationLevel)
                .stream()
                .findFirst();
    }

    boolean existsByDataSourceIdAndMetricDefinitionIdAndAggregationLevelAndWindowStartAndWindowEnd(
            UUID dataSourceId,
            UUID metricDefinitionId,
            AggregationLevel aggregationLevel,
            Instant windowStart,
            Instant windowEnd
    );
}
