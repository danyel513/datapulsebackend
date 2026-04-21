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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "data_source_id", nullable = false)
    private DataSourceEntity dataSource;

    @Column(name = "metric_key", nullable = false, length = 100)
    private String metricKey;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "unit", nullable = false, length = 50)
    private String unit;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "min_valid_value")
    private Double minValidValue;

    @Column(name = "max_valid_value")
    private Double maxValidValue;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.TRUE;
}
