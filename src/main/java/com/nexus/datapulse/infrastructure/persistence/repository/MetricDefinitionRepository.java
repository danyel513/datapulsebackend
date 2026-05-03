package com.nexus.datapulse.infrastructure.persistence.repository;

import com.nexus.datapulse.infrastructure.persistence.entity.MetricDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetricDefinitionRepository extends JpaRepository<MetricDefinitionEntity, UUID> {
    Optional<MetricDefinitionEntity> findByMetricKey(String metricKey);
    Optional<MetricDefinitionEntity> findByMetricKeyAndEnabledTrue(String metricKey);
    List<MetricDefinitionEntity> findAllByEnabledTrue();
    boolean existsByMetricKey(String metricKey);
}