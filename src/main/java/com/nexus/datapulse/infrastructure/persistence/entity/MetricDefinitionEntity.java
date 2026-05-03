package com.nexus.datapulse.infrastructure.persistence.entity;

import com.nexus.datapulse.domain.measurement.MetricDataType;
import com.nexus.datapulse.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "metric_definitions")
public class MetricDefinitionEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "metric_key", nullable = false, length = 100, unique = true)
    private String metricKey;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 50)
    private MetricDataType dataType;

    @Column(name = "min_valid_value")
    private Double minValidValue;

    @Column(name = "max_valid_value")
    private Double maxValidValue;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.TRUE;
}
