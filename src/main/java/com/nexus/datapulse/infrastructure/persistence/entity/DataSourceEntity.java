package com.nexus.datapulse.infrastructure.persistence.entity;

import com.nexus.datapulse.domain.datasource.DataSourceStatus;
import com.nexus.datapulse.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "data_sources")
public class DataSourceEntity extends AuditableEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "source_key", nullable = false, unique = true, length = 100)
    private String sourceKey;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "host", length = 255)
    private String host;

    @Column(name = "port")
    private Integer port;

    @Column(name = "protocol", length = 50)
    private String protocol;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DataSourceStatus status;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "dataSource", fetch = FetchType.LAZY)
    private List<MeasurementAggregateEntity> measurementAggregates = new ArrayList<>();

}
