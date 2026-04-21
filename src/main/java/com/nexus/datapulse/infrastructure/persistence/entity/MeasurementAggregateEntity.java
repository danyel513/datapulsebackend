package com.nexus.datapulse.infrastructure.persistence.entity;

import com.nexus.datapulse.infrastructure.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "measurement_aggregates")
public class MeasurementAggregateEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "data_source_id", nullable = false)
    private DataSourceEntity dataSource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "metric_definition_id", nullable = false)
    private MetricDefinitionEntity metricDefinition;

    @Column(name = "aggregation_level", nullable = false, length = 50)
    private String aggregationLevel;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    @Column(name = "sample_count", nullable = false)
    private Integer sampleCount;

    @Column(name = "valid_sample_count", nullable = false)
    private Integer validSampleCount;

    @Column(name = "invalid_sample_count", nullable = false)
    private Integer invalidSampleCount;

    @Column(name = "min_value", nullable = false)
    private Double minValue;

    @Column(name = "max_value", nullable = false)
    private Double maxValue;

    @Column(name = "avg_value", nullable = false)
    private Double avgValue;

    @Column(name = "last_value")
    private Double lastValue;
}
